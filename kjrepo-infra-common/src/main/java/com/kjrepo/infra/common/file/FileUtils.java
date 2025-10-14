package com.kjrepo.infra.common.file;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.annimon.stream.Optional;
import com.google.common.io.Files;

public class FileUtils {

	public static void createFileIfNoExists(File file, String initText) {
		try {
			if (file.exists()) {
				return;
			}
			synchronized (FileUtils.class + "#" + file.getName()) {
				if (file.exists()) {
					return;
				}
				file.getParentFile().mkdirs();
				file.createNewFile();
				if (StringUtils.isNoneEmpty(initText)) {
					Files.asCharSink(file, StandardCharsets.UTF_8).write(initText);
				}
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static void createDirIfNoExists(File file) {
		if (file.exists()) {
			return;
		}
		synchronized (FileUtils.class + "#" + file.getName()) {
			if (file.exists()) {
				return;
			}
			file.mkdirs();
		}
	}

	public static List<String> read(File file) {
		return read(file, StandardCharsets.UTF_8);
	}

	public static List<String> read(File file, Charset charset) {
		try {
			return Files.readLines(file, charset);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static void write(File file, String content) {
		write(file, StandardCharsets.UTF_8, content);
	}

	public static void write(File file, Charset charset, String content) {
		try {
			if (StringUtils.isNoneEmpty(content)) {
				Files.asCharSink(file, Optional.ofNullable(charset).orElse(StandardCharsets.UTF_8)).write(content);
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

}
