package com.kjrepo.infra.register.impl;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.monitor.FileAlterationListenerAdaptor;
import org.apache.commons.lang3.StringUtils;

import com.google.common.io.Files;
import com.kjrepo.infra.common.file.FileUtils;
import com.kjrepo.infra.register.AbstractRegister;
import com.kjrepo.infra.register.utils.DfileUtils;
import com.kjrepo.infra.text.json.JsonUtils;

public class FileRegister<V> extends AbstractRegister<V> {

	private final String workspace;

	public FileRegister(Class<V> clazz) {
		this(System.getProperty("user.dir") + File.separator + "register", clazz);
	}

	public FileRegister(String workspace, Class<V> clazz) {
		super(clazz);
		this.workspace = workspace;
	}

	@Override
	public void set(String key, V value) {
		try {
			File file = new File(DfileUtils.toFile(this.workspace, key) + File.separator + "main.json");
			FileUtils.createFileIfNoExists(file, "");
			String json = "";
			if (value == null) {
			} else {
				json = JsonUtils.toPrettyJson(value);
			}
			Files.asCharSink(file, StandardCharsets.UTF_8).write(json);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	protected void init(String path) {
		File file = new File(DfileUtils.toFile(this.workspace, path) + File.separator + "main.json");
		FileUtils.createFileIfNoExists(file, defString());
		DfileUtils.monitor(file.getParent(), new FileAlterationListenerAdaptor() {
			@Override
			public void onFileChange(final File file) {
				logger.info("file change key:{} file:{}", path, file.getName());
				refresh(path);
			}
		});
	}

	@Override
	protected Object json(String path) {
		try {
			File file = new File(DfileUtils.toFile(this.workspace, path) + File.separator + "main.json");
			String json = StringUtils.join(Files.readLines(file, StandardCharsets.UTF_8), "\n").trim();
			if (json.startsWith("{") && json.endsWith("}") || json.startsWith("[") && json.endsWith("]")) {
				return JsonUtils.fromJson(json, Object.class);
			} else {
				return json;
			}
		} catch (IOException e) {
			logger.error("path:" + path, e);
			throw new RuntimeException(e);
		}
	}

}
