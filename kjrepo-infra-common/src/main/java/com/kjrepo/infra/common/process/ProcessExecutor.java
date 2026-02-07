package com.kjrepo.infra.common.process;

import java.io.File;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;

import com.kjrepo.infra.common.logger.LoggerUtils;

public class ProcessExecutor {

	private static final Logger logger = LoggerUtils.logger(ProcessExecutor.class);

	public static Process execute(String command) throws Exception {
		return execute(command, null);
	}

	public static Process execute(String command, String workDir) throws Exception {
		ProcessBuilder builder = new ProcessBuilder();
		builder.redirectErrorStream(true);
		if (System.getProperty("os.name").toLowerCase().contains("win")) {
			builder.command("cmd.exe", "/c", command);
		} else {
			builder.command("sh", "-c", command);
		}
		if (workDir != null) {
			builder.directory(new File(workDir));
		}
		return builder.start();
	}

	public static void main(String[] args) throws Exception {
		ProcessResult<Void> result1 = ProcessResult
				.ofVoid(ProcessExecutor.execute("echo kjrepo" + StringUtils.repeat("kjrepo", 1000)));
		logger.info("exit:{}", result1.exit());

		ProcessResult<List<String>> result2 = ProcessResult
				.of(ProcessExecutor.execute("echo kjrepo" + StringUtils.repeat("kjrepo", 1000)));
		logger.info("exit:{}", result2.exit());
		result2.out().forEach(logger::info);
	}
}