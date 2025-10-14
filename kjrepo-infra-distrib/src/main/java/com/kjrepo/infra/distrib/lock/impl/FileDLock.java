package com.kjrepo.infra.distrib.lock.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileLock;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import com.kjrepo.infra.common.file.FileUtils;
import com.kjrepo.infra.common.info.Tuple;
import com.kjrepo.infra.common.info.Tuple3;
import com.kjrepo.infra.distrib.lock.AbstractDLock;
import com.kjrepo.infra.register.utils.DfileUtils;

public class FileDLock extends AbstractDLock {

	private final ThreadLocal<Tuple3<FileOutputStream, FileLock, AtomicInteger>> lock = new ThreadLocal<>();

	private final String workspace;
	private final File file;

	public FileDLock(String key) {
		this(key, System.getProperty("user.dir") + File.separator + "register");
	}

	public FileDLock(String key, String workspace) {
		super(key);
		this.workspace = workspace;
		this.file = new File(DfileUtils.toFile(this.workspace, key()) + File.separator + "lock.json");
		FileUtils.createFileIfNoExists(file, "DLock");
	}

	@Override
	public boolean tryLock(long timeout, TimeUnit unit) {
		try {
			long timestamp = timeout >= 0 ? System.currentTimeMillis() + unit.toMillis(timeout) : Long.MAX_VALUE;
			do {
				if (lock.get() != null) {
					lock.get().getT3().incrementAndGet();
					return true;
				}
				FileOutputStream fos = new FileOutputStream(file.getAbsolutePath());
				FileLock flock = fos.getChannel().tryLock();
				if (flock == null) {
					fos.close();
					continue;
				} else {
					lock.set(Tuple.tuple(fos, flock, new AtomicInteger(1)));
					return true;
				}
			} while (System.currentTimeMillis() <= timestamp);
			return false;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void unlock() {
		try {
			Tuple3<FileOutputStream, FileLock, AtomicInteger> tuple = lock.get();
			if (tuple != null) {
				if (tuple.getT3().decrementAndGet() == 0) {
					tuple.getT2().release();
					tuple.getT1().close();
					lock.set(null);
				}
			} else {
				logger.info("The key has not been locked by you,please check your code!!!");
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

}
