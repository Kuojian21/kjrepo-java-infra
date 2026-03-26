package com.kjrepo.infra.dlock.impl;

import java.io.File;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.annimon.stream.Optional;
import com.google.common.util.concurrent.Uninterruptibles;
import com.kjrepo.infra.common.file.utils.FileUtils;
import com.kjrepo.infra.common.info.Pair;
import com.kjrepo.infra.common.utils.RunUtils;
import com.kjrepo.infra.dlock.AbstractDLock;
import com.kjrepo.infra.register.utils.RegisterUtils;

public class FileDLock extends AbstractDLock {

	private final ThreadLocal<Pair<FileLock, AtomicInteger>> fileLock = new ThreadLocal<>();
	private final Lock lock = new ReentrantLock();

	private final String workspace;
	private final File file;
	private final RandomAccessFile raf;
	private final FileChannel channel;

	public FileDLock(String key) {
		this(key, System.getProperty("user.dir") + File.separator + "dlock");
	}

	public FileDLock(String key, String workspace) {
		super(key);
		this.workspace = workspace;
		this.file = new File(RegisterUtils.toFile(this.workspace, key()) + File.separator + "main.lock");
		FileUtils.createFileIfNoExists(file, "DLock");
		this.raf = RunUtils.throwing(() -> new RandomAccessFile(file, "rw"));
		this.channel = raf.getChannel();
	}

	@Override
	public boolean tryLock(long timeout, TimeUnit unit) {
		return RunUtils.throwing(() -> {
			long timestamp = timeout >= 0 ? System.currentTimeMillis() + unit.toMillis(timeout) : Long.MAX_VALUE;
			if (fileLock.get() != null) {
				fileLock.get().getValue().incrementAndGet();
				return true;
			}
			if (Optional.of(timeout).map(l -> {
				if (l == -1L) {
					lock.lock();
					return true;
				} else {
					return RunUtils.throwing(() -> lock.tryLock(timeout, unit));
				}
			}).get()) {
				do {
					FileLock flock = channel.tryLock();
					if (flock == null) {
						Uninterruptibles.sleepUninterruptibly(
								Math.max(0L,
										Math.min(timestamp - System.currentTimeMillis(), TimeUnit.SECONDS.toMillis(6))),
								TimeUnit.MILLISECONDS);
					} else {
						fileLock.set(Pair.pair(flock, new AtomicInteger(1)));
						raf.writeUTF("PID:" + ProcessHandle.current().pid());
						raf.getFD().sync();
						return true;
					}
				} while (System.currentTimeMillis() <= timestamp);
				lock.unlock();
			}
			return false;
		});
	}

	@Override
	public void unlock() {
		RunUtils.throwing(() -> {
			Pair<FileLock, AtomicInteger> pair = fileLock.get();
			if (pair == null) {
				logger.error("The key has not been locked by you,please check your code!!!");
			} else {
				if (pair.getValue().decrementAndGet() == 0) {
					raf.writeUTF("PID:" + 0L);
					raf.getFD().sync();
					pair.getKey().release();
					fileLock.set(null);
					lock.unlock();
				}
			}
		});
	}

}
