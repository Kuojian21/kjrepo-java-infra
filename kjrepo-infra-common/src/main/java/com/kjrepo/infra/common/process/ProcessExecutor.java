package com.kjrepo.infra.common.process;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.SystemUtils;
import org.slf4j.Logger;

import com.annimon.stream.function.ThrowableConsumer;
import com.annimon.stream.function.ThrowableFunction;
import com.google.common.collect.Lists;
import com.kjrepo.infra.common.logger.LoggerUtils;

public class ProcessExecutor<T> {

	private static final Logger logger = LoggerUtils.logger(ProcessExecutor.class);

	public static ProcessExecutor<Void> ofVoid(String command, String workDir) {
		return ProcessExecutor.ofLine(command, workDir, line -> {

		});
	}

	public static ProcessExecutor<List<String>> ofString(String command, String workDir) {
		return ProcessExecutor.ofLine(command, workDir, line -> {
			return line;
		});
	}

	public static <X extends Throwable> ProcessExecutor<Void> ofLine(String command, String workDir,
			ThrowableConsumer<String, X> handler) {
		return ProcessExecutor.of(command, workDir, br -> {
			String line;
			while ((line = br.readLine()) != null) {
				handler.accept(line);
			}
			return null;
		});
	}

	public static <T, X extends Throwable> ProcessExecutor<List<T>> ofLine(String command, String workDir,
			ThrowableFunction<String, T, X> handler) {
		return ProcessExecutor.of(command, workDir, br -> {
			List<T> data = Lists.newArrayList();
			String line;
			while ((line = br.readLine()) != null) {
				data.add(handler.apply(line));
			}
			return data;
		});
	}

	public static <X extends Throwable> ProcessExecutor<Void> of(String command, String workDir,
			ThrowableConsumer<BufferedReader, X> handler) {
		return ProcessExecutor.of(command, workDir, br -> {
			handler.accept(br);
			return null;
		});
	}

	public static <T, X extends Throwable> ProcessExecutor<T> of(String command, String workDir,
			ThrowableFunction<BufferedReader, T, X> handler) {
		return new ProcessExecutor<T>(command, workDir, handler);
	}

	private final ProcessBuilder builder;
	private final ThrowableFunction<BufferedReader, T, ? extends Throwable> handler;
	private volatile Process process;
	private volatile T data;

	private ProcessExecutor(String command, String workDir,
			ThrowableFunction<BufferedReader, T, ? extends Throwable> handler) {
		super();
		ProcessBuilder builder = new ProcessBuilder();
		builder.redirectErrorStream(true);
		if (SystemUtils.IS_OS_WINDOWS) {
			builder.command("cmd.exe", "/c", command);
		} else {
			builder.command("sh", "-c", command);
		}
		if (workDir != null) {
			builder.directory(new File(workDir));
		}
		this.builder = builder;
		this.handler = handler;
	}

	public <X extends Throwable> void exec() throws IOException, X {
		this.get();
	}

	@SuppressWarnings("unchecked")
	public <X extends Throwable> T get() throws IOException, X {
		if (this.process == null) {
			synchronized (this) {
				if (this.process == null) {
					this.process = builder.start();
					try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream(),
							!SystemUtils.IS_OS_WINDOWS ? "UTF-8" : System.getProperty("sun.jnu.encoding", "GBK")))) {
						this.data = handler.apply(reader);
					} catch (IOException e) {
						logger.error("", e);
						throw e;
					} catch (Throwable e) {
						logger.error("", e);
						throw (X) e;
					}
				}
			}
		}
		return this.data;
	}

	public int exit() throws InterruptedException, IOException {
		while (exit(1, TimeUnit.MINUTES) == -1) {

		}
		return this.process.waitFor();
	}

	public int exit(int timeout, TimeUnit timeunit) throws InterruptedException, IOException {
		/**
		 * 当日志量过大时，如果不处理的的话，waitFor会阻塞。
		 */
		this.get();
		if (this.process.waitFor(timeout, timeunit)) {
			return this.process.waitFor();
		} else {
			return -1;
		}
	}

}