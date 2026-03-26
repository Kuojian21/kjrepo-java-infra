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

	private final String[] o_args;

	private final List<Value> m_args = Lists.newArrayList();

	private Args(String[] args) {
		this.o_args = args;
		for (int i = 0; i < args.length; i++) {
			if (args[i].startsWith("-")) {
				if (i + 1 == args.length || args[i + 1].startsWith("-")) {
					this.m_args.add(Value.of(args[i]));
				} else {
					this.m_args.add(Value.of(args[i], args[i + 1]));
					i++;
				}
			} else if (args[i].indexOf("=") > 0 && args[i].indexOf("=") < args[i].length()) {
				this.m_args.add(Value.of(args[i].substring(0, args[i].indexOf("=")),
						args[i].substring(args[i].indexOf("=") + 1)));
			} else {
				this.m_args.add(Value.of("", args[i]));
			}
		}
	}

	public String[] args() {
		return o_args;
	}

	public int length() {
		return o_args.length;
	}

	public String get(int idx) {
		return o_args[idx];
	}

	public Optional<String> option(String option) {
		return options(option).findFirst();
	}

	public Stream<String> options(String option) {
		return Stream.of(this.m_args).filter(v -> v.option().equals(option)).map(Value::value);
	}

	public CommandLine commandLine(String prefix, Options options) {
		try {
			return new DefaultParser().parse(options, Stream.of(this.m_args).filter(v -> v.option().startsWith(prefix))
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
