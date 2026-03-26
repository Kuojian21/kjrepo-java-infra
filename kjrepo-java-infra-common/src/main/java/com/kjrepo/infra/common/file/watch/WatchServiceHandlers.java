package com.kjrepo.infra.common.file.watch;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchEvent.Kind;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.monitor.FileAlterationListener;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.kjrepo.infra.common.utils.RunUtils;

public class WatchServiceHandlers {

	public static WatchServiceHandlers of(Kind<Path> event) {
		return new WatchServiceHandlers(event);
	}

	private final Set<FileAlterationListener> handlers = Sets.newConcurrentHashSet();
	private final Map<String, Long> updates = Maps.newConcurrentMap();
	private final WatchEvent.Kind<Path> event;

	public WatchServiceHandlers(Kind<Path> event) {
		super();
		this.event = event;
	}

	public void handle(Path path) {
		File file = path.toFile();
		if (event == StandardWatchEventKinds.ENTRY_MODIFY) {
			Long update = RunUtils
					.throwing(() -> Files.readAttributes(path.toAbsolutePath(), BasicFileAttributes.class))
					.lastModifiedTime().toMillis();
			if (!updates.getOrDefault(file.getAbsolutePath(), -1L).equals(update)) {
				if (file.isFile()) {
					handlers.forEach(handler -> handler.onFileChange(file));
				} else {
					handlers.forEach(handler -> handler.onDirectoryChange(file));
				}
				updates.put(file.getAbsolutePath(), update);
			}
		} else if (event == StandardWatchEventKinds.ENTRY_CREATE) {
			if (file.isFile()) {
				handlers.forEach(handler -> handler.onFileCreate(file));
			} else {
				handlers.forEach(handler -> handler.onDirectoryCreate(file));
			}
		} else if (event == StandardWatchEventKinds.ENTRY_DELETE) {
			if (file.isFile()) {
				handlers.forEach(handler -> handler.onFileDelete(file));
			} else {
				handlers.forEach(handler -> handler.onDirectoryDelete(file));
			}
		}
	}

	public void addHandler(FileAlterationListener handler) {
		this.handlers.add(handler);
	}

}
