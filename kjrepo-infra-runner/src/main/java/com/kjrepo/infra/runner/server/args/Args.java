package com.kjrepo.infra.runner.server.args;

import java.util.List;

import com.annimon.stream.Stream;
import com.google.common.collect.Lists;

public class Args {

	public static Args of(String[] args) {
		Args bean = new Args();
		int i = 0;
		while (i < args.length) {
			if (args[i].startsWith("-")) {
				if (i + 1 < args.length && !args[i + 1].startsWith("-")) {
					bean.args.add(ArgsOption.of(args[i], args[i + 1]));
					i++;
				} else {
					bean.args.add(ArgsOption.of(args[i], null));
				}
			}
			i++;
		}
		return bean;
	}

	private final List<ArgsOption> args = Lists.newArrayList();

	public String[] arg(String name) {
		return Stream.of(args).filter(ap -> ap.key().equals(name) || ap.option().equals(name)).map(ArgsOption::value)
				.toArray(i -> new String[0]);
	}

	public String[] args(String aprefix) {
		return Stream.of(this.args).filter(ap -> ap.option().startsWith(aprefix))
				.flatMap(ap -> ap.hasArgs() ? Stream.of(ap.key()) : Stream.of(ap.key(), ap.value()))
				.toArray(i -> new String[0]);
	}

}
