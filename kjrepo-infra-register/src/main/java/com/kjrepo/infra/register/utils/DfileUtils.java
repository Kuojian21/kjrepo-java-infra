package com.kjrepo.infra.register.utils;

import java.io.File;

import org.apache.commons.io.monitor.FileAlterationListenerAdaptor;

import com.kjrepo.infra.common.file.FileMonitor;
import com.kjrepo.infra.common.lazy.LazySupplier;

public class DfileUtils {

	private static final LazySupplier<FileMonitor> monitor = LazySupplier.wrap(() -> FileMonitor.monitor());

	public static String toFile(String workspace, String path) {
		path = path.trim();
		if (path.startsWith("/")) {
			path = path.substring(1);
		}
		if (path.endsWith("/")) {
			path = path.substring(0, path.length() - 1);
		}
		return workspace + File.separator + path.replaceAll("\\\\/", File.separator);
	}

	public static String toPath(String workspace, String file) {
		return file.substring(workspace.length() + 1);
	}

	public static void monitor(String file, FileAlterationListenerAdaptor listener) {
		monitor.get().monitor(file, listener);
	}

}
