package com.kjrepo.infra.common.file.watch;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.concurrent.ConcurrentMap;

import com.google.common.collect.Maps;
import com.kjrepo.infra.common.utils.RunUtils;

public class WatchServiceHolder {

	public static WatchServiceHolder of() {
		return RunUtils.throwing(WatchServiceHolder::new);
	}

	private final WatchService service;
	private final ConcurrentMap<WatchKey, String> keys = Maps.newConcurrentMap();

	private WatchServiceHolder() throws IOException {
		this.service = FileSystems.getDefault().newWatchService();
	}

	public void register(String path, WatchEvent.Kind<Path> event) {
		RunUtils.throwing(() -> {
			keys.put(Path.of(path).register(service, event), path);
		});
	}

	public WatchService service() {
		return service;
	}

	public String path(WatchKey key) {
		return keys.get(key);
	}

}
