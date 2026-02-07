package com.kjrepo.infra.common.process;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;

import com.annimon.stream.function.ThrowableFunction;
import com.google.common.collect.Lists;
import com.kjrepo.infra.common.lazy.LazySupplier;
import com.kjrepo.infra.common.logger.LoggerUtils;

public class ProcessResult<T> {

	private static final Logger logger = LoggerUtils.logger(ProcessResult.class);

	public static ProcessResult<Void> ofVoid(Process process) {
		return ProcessResult.<Void>of(process, reader -> {
			while (reader.readLine() != null) {
			}
			return null;
		});
	}

	public static ProcessResult<List<String>> of(Process process) {
		return ProcessResult.<List<String>>of(process, reader -> {
			List<String> lines = Lists.newArrayList();
			String line;
			while ((line = reader.readLine()) != null) {
				lines.add(line);
			}
			return lines;
		});
	}

	public static <T> ProcessResult<T> of(Process process, ThrowableFunction<BufferedReader, T, Throwable> out_func) {
		return new ProcessResult<>(process, out_func);
	}

	private final Process process;
	private final LazySupplier<T> out;

	private ProcessResult(Process process, ThrowableFunction<BufferedReader, T, Throwable> out_func) {
		super();
		this.process = process;
		this.out = LazySupplier.wrap(() -> {
			try {
				try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream(),
						!System.getProperty("os.name").toLowerCase().contains("win") ? "UTF-8"
								: System.getProperty("sun.jnu.encoding", "GBK")))) {
					return out_func.apply(reader);
				}
			} catch (Throwable e) {
				logger.error("", e);
				return null;
			}
		});
		this.out.get();
	}

	public int exit() throws InterruptedException {
		while (exit(1, TimeUnit.MINUTES) == -1) {

		}
		return this.process.waitFor();
	}

	public int exit(int timeout, TimeUnit timeunit) throws InterruptedException {
		/**
		 * 当日志量过大时，如果不处理的的话，waitFor会阻塞。
		 */
		this.out();
		if (this.process.waitFor(timeout, timeunit)) {
			return this.process.waitFor();
		} else {
			return -1;
		}
	}

	public T out() {
		return out.get();
	}

}