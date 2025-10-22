package com.kjrepo.infra.common.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;
import org.slf4j.Logger;

import com.google.common.collect.Lists;
import com.kjrepo.infra.common.logger.LoggerUtils;

public class ProcessUtils {

	private static final Logger logger = LoggerUtils.logger(ProcessUtils.class);

	public static Result exec(Charset charset, String... command) {
		try {
			List<String> commands = Lists.newArrayList();
			if (SystemUtils.IS_OS_WINDOWS) {
				commands.add("cmd.exe");
				commands.add("/c");
			} else {
				commands.add("bash");
				commands.add("-c");
			}
			commands.addAll(Lists.newArrayList(command));
			ProcessBuilder builder = new ProcessBuilder(commands);
			builder.redirectErrorStream(true); // 合并错误流和输出流
			Process process = builder.start();
			List<String> output = Lists.newArrayList();
			try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream(), charset))) {
				String line;
				while ((line = reader.readLine()) != null) {
					output.add(line);
				}
			}
			int exitCode = process.waitFor();
			return Result.of(exitCode, StringUtils.join(output, "\n"));
		} catch (IOException | InterruptedException e) {
			logger.error("", e);
			throw new RuntimeException(e);
		}
	}

	public static class Result {

		public static Result of(int exitCode, String output) {
			return new Result(exitCode, output);
		}

		private final int exitCode;
		private final String output;

		public Result(int exitCode, String output) {
			super();
			this.exitCode = exitCode;
			this.output = output;
		}

		public int getExitCode() {
			return exitCode;
		}

		public String getOutput() {
			return output;
		}

	}

}
