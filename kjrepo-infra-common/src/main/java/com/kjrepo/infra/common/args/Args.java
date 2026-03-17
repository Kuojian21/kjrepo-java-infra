package com.kjrepo.infra.common.args;

import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.lang3.StringUtils;

import com.annimon.stream.Optional;
import com.annimon.stream.Stream;
import com.google.common.collect.Lists;

public class Args {

	public static Args of(String[] args) {
		return new Args(args);
	}

	private final List<Value> args = Lists.newArrayList();

	private Args(String[] args) {
		for (int i = 0; i < args.length; i++) {
			if (args[i].startsWith("-")) {
				if (i + 1 == args.length || args[i + 1].startsWith("-")) {
					this.args.add(Value.of(args[i]));
				} else {
					this.args.add(Value.of(args[i], args[i + 1]));
					i++;
				}
			}
		}
	}

	public Optional<String> option(String option) {
		return Stream.of(this.args).filter(v -> v.option().equals(option)).map(Value::value).findFirst();
	}

	public CommandLine commandLine(String prefix, Options options) {
		try {
			return new DefaultParser().parse(options, Stream.of(this.args).filter(v -> v.option().startsWith(prefix))
					.flatMap(v -> Stream.of(v.key(), v.value())).toArray(i -> new String[i]), true);
		} catch (ParseException e) {
			throw new RuntimeException(e);
		}
	}

	static class Value {

		public static Value of(String key) {
			return of(key, null);
		}

		public static Value of(String key, String value) {
			return new Value(key, value);
		}

		private final String key;
		private final String value;
		private final String option;

		private Value(String key, String value) {
			this.key = key;
			this.value = value;
			this.option = key.replaceAll("^--", "").replaceAll("^-", "");
		}

		public String key() {
			return key;
		}

		public String option() {
			return option;
		}

		public String value() {
			return value;
		}

		public boolean hasValues() {
			return StringUtils.isNotEmpty(this.value);
		}

	}

}
