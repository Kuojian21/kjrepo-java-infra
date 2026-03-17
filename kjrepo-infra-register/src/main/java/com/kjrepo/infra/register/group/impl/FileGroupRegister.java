package com.kjrepo.infra.register.group.impl;

import java.io.File;
import java.util.List;

import org.apache.commons.io.monitor.FileAlterationListenerAdaptor;

import com.annimon.stream.Stream;
import com.kjrepo.infra.common.file.FileUtils;
import com.kjrepo.infra.register.Register;
import com.kjrepo.infra.register.RegisterListener;
import com.kjrepo.infra.register.group.AbstractGroupReigster;
import com.kjrepo.infra.register.impl.FileRegister;
import com.kjrepo.infra.register.utils.RegisterUtils;

public class FileGroupRegister<V, I> extends AbstractGroupReigster<V, I> {

	private final Register<V> vregister;
	private final String workspace;

	public FileGroupRegister(Class<V> vclazz, Class<I> clazz) {
		this(System.getProperty("user.dir") + File.separator + "register", vclazz, clazz);
	}

	public FileGroupRegister(String workspace, Class<V> vclazz, Class<I> clazz) {
		super(new FileRegister<I>(workspace, clazz));
		this.workspace = workspace;
		this.vregister = new FileRegister<>(this.workspace, vclazz);
	}

	@Override
	protected void init(String path) {
		File file = new File(RegisterUtils.toFile(this.workspace, path));
		FileUtils.createDirIfNoExists(file);
		RegisterUtils.monitor(file.getAbsolutePath(), new FileAlterationListenerAdaptor() {
			/*
			 * bugfix
			 */
//			java.lang.NullPointerException: Cannot invoke "com.kjrepo.infra.cluster.instance.InstanceInfo.getName()" because "tInfo" is null
//	        		at com.kjrepo.infra.cluster.Cluster.lambda$add$8(Cluster.java:89)
//	        		at com.kjrepo.infra.common.lazy.LazySupplier.get(LazySupplier.java:32)
//	        		at com.kjrepo.infra.cluster.selector.RandomSelector.select(RandomSelector.java:19)
//	        		at com.kjrepo.infra.cluster.Cluster.getResource(Cluster.java:76)
			@Override
			public void onDirectoryCreate(final File dir) {
				fireCreate(path, path + "/" + dir.getName());
			}

			@Override
			public void onDirectoryDelete(final File dir) {
				fireRemove(path, path + "/" + dir.getName());
			}
		});
	}

	@Override
	protected List<String> keys(String path) {
		return Stream.of(new File(RegisterUtils.toFile(this.workspace, path)).listFiles())
				.filter(dir -> dir.isDirectory()).map(dir -> dir.getName()).map(c -> path + "/" + c).toList();
	}

	@Override
	public void cadd(String pkey, I value) {
		this.cset(pkey + "/" + ProcessHandle.current().pid(), value);
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
