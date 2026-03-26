package com.kjrepo.infra.storage.db.utils;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.annimon.stream.Stream;

public class DB_tag_utils {

	public static final List<Pattern> patterns = Stream.of( //
			"jdbc:mysql://([^/]+)", //
			"jdbc:postgresql://([^/]+)", //
			"jdbc:oracle:thin:@(?://)?([^/]+)", //
			"jdbc:sqlserver://([^;]+)", //
			"jdbc:hive2://([^/]+)", //
			"jdbc:h2:(?:mem|file):([^;]+)", //
			"jdbc:sqlite:([^;]+)") //
			.map(Pattern::compile).toList();

	public static String tag(String jdbcUrl) {
		for (Pattern pattern : patterns) {
			Matcher matcher = pattern.matcher(jdbcUrl);
			if (matcher.find()) {
				return matcher.group(1);
			}
		}
		return null;
	}

	public static void main(String[] args) {
		System.out.println(tag("jdbc:h2:mem:stock;DB_CLOSE_DELAY=-1"));
		System.out.println(tag("jdbc:sqlite:stock.db"));
	}

}
