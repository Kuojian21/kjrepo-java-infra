package com.kjrepo.infra.gregister.impl;

import java.io.File;
import java.util.List;

import org.apache.commons.io.monitor.FileAlterationListenerAdaptor;

import com.annimon.stream.Stream;
import com.kjrepo.infra.common.file.FileUtils;
import com.kjrepo.infra.common.info.Pair;
import com.kjrepo.infra.gregister.AbstractGroupReigster;
import com.kjrepo.infra.register.Register;
import com.kjrepo.infra.register.RegisterListener;
import com.kjrepo.infra.register.impl.FileRegister;
import com.kjrepo.infra.register.utils.DfileUtils;

public class FileGroupRegister<V, I> extends AbstractGroupReigster<V, I> {

	private final Register<V> vregister;
	private final String workspace;

	public FileGroupRegister(Class<V> vclazz, Class<I> clazz) {
		this(System.getProperty("user.dir") + File.separator + "register", vclazz, clazz);
	}

	public FileGroupRegister(String workspace, Class<V> vclazz, Class<I> clazz) {
		super(new FileRegister<I>(workspace, clazz));
		this.workspace = workspace;
		this.vregister = new FileRegister<>(vclazz);
	}

	@Override
	protected void init(String path) {
		File file = new File(DfileUtils.toFile(this.workspace, path));
		FileUtils.createDirIfNoExists(file);
		DfileUtils.monitor(file.getAbsolutePath(), new FileAlterationListenerAdaptor() {
			@Override
			public void onDirectoryCreate(final File dir) {
				fireCreate(path, dir.getName());
			}

			@Override
			public void onDirectoryDelete(final File dir) {
				fireRemove(path, dir.getName());
			}
		});
	}

	@Override
	protected List<Pair<String, I>> data(String path) {
		return Stream.of(new File(DfileUtils.toFile(this.workspace, path)).listFiles()).filter(dir -> dir.isDirectory())
				.map(dir -> dir.getName()).map(ckey -> Pair.pair(ckey, this.get(path, ckey))).toList();
	}

	@Override
	public void set(String key, V value) {
		this.vregister.set(key, value);
	}

	@Override
	public V get(String key) {
		return this.vregister.get(key);
	}

	@Override
	public void addListener(String key, RegisterListener<V> listener) {
		this.vregister.addListener(key, listener);
	}

}
