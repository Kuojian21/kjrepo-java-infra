package com.kjrepo.infra.common.process;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;
import org.slf4j.Logger;

import com.annimon.stream.Optional;
import com.annimon.stream.function.Consumer;
import com.google.common.base.Joiner;
import com.google.common.base.Stopwatch;
import com.google.common.collect.Lists;
import com.kjrepo.infra.common.info.Pair;
import com.kjrepo.infra.common.logger.LoggerUtils;
import com.kjrepo.infra.common.number.N_humanUtils;

public class ProcessExecutor {

	public static ProcessExecutor of() {
		return of(System.getProperty("user.dir"));
	}

	public static ProcessExecutor of(String workdir) {
		return of(workdir, System.getProperty("native.encoding", "UTF-8"));
	}

	public static ProcessExecutor of(String workdir, String charset) {
		return new ProcessExecutor(workdir, charset);
	}

	private final Logger logger = LoggerUtils.getLogger(getClass());
	private final String workdir;
	private final String charset;

	public ProcessExecutor(String workdir, String charset) {
		this.workdir = Optional.ofNullable(workdir).filter(StringUtils::isNotEmpty)
				.orElseGet(() -> System.getProperty("user.dir"));
		this.charset = charset;
	}

	public Pair<Integer, List<String>> exec(String command) throws IOException, InterruptedException {
		List<String> list = Lists.newArrayList();
		return Pair.pair(exec(new String[] { command }, list::add), list);
	}

	public int exec(String command, Consumer<String> handler) throws IOException, InterruptedException {
		return exec(new String[] { command }, handler);
	}

	public Pair<Integer, List<String>> exec(String[] commands) throws IOException, InterruptedException {
		List<String> list = Lists.newArrayList();
		return Pair.pair(exec(commands, list::add), list);
	}

	public int exec(String[] commands, Consumer<String> handler) throws IOException, InterruptedException {
		Stopwatch stopwatch = Stopwatch.createStarted();
		long pid = -1L;
		int exit = -1;
		try {
			ProcessBuilder builder = new ProcessBuilder();
			builder.redirectErrorStream(true);
			builder.directory(new File(workdir));
			if (SystemUtils.IS_OS_WINDOWS) {
				builder.command().add("cmd.exe");
				builder.command().add("/c");
			} else {
				builder.command().add("sh");
				builder.command().add("-c");
			}
			builder.command().addAll(Lists.newArrayList(commands));
			Process process = builder.start();
			pid = process.pid();
			/**
			 * 当日志量过大时，如果不处理的的话，waitFor会阻塞。
			 */
			try (BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream(), charset))) {
				String line;
				while ((line = br.readLine()) != null) {
					handler.accept(line);
				}
			}
			exit = process.waitFor();
			return exit;
		} finally {
			logger.info("command:[{}] pid:{} exit:{} elapsed:{}", Joiner.on(" ").join(commands), pid, exit,
					N_humanUtils.formatMills(stopwatch.elapsed(TimeUnit.MILLISECONDS)));
		}
	}
}