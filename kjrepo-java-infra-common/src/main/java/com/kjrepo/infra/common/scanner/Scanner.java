package com.kjrepo.infra.common.scanner;

import java.io.File;
import java.net.JarURLConnection;
import java.util.List;
import java.util.jar.JarFile;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.annimon.stream.Stream;
import com.annimon.stream.function.Predicate;
import com.annimon.stream.function.ThrowableFunction;
import com.google.common.collect.Lists;
import com.kjrepo.infra.common.lazy.LazySupplier;
import com.kjrepo.infra.common.utils.RunUtils;

public class Scanner<T> {

	public static <T> Scanner<T> of(String path, String regex, ThrowableFunction<String, T, Throwable> mapper,
			Predicate<T> filter, ClassLoader clazzLoader) {
		return new Scanner<>(path, regex, mapper, filter, clazzLoader);
	}

//	private final String path;
//	private final String regex;
	private final Pattern pattern;
	private final ThrowableFunction<String, T, Throwable> mapper;
	private final Predicate<T> filter;
	private final LazySupplier<List<T>> datas;

	public Scanner(String path, String regex, ThrowableFunction<String, T, Throwable> mapper, Predicate<T> filter,
			ClassLoader clazzLoader) {
//		this.path = path;
//		this.regex = regex;
		this.pattern = Pattern.compile(regex);
		this.mapper = mapper;
		this.filter = filter;
		this.datas = LazySupplier.wrap(() -> {
			List<T> clazzes = Lists.newArrayList();
			RunUtils.catching(() -> {
				Stream.of(clazzLoader.getResources(path).asIterator()).forEach(url -> {
					switch (url.getProtocol()) {
					case "file":
						scan(new File(RunUtils.catching(() -> url.toURI())), clazzes);
						break;
					case "jar":
						scan(RunUtils.catching(() -> ((JarURLConnection) url.openConnection()).getJarFile()), clazzes);
						break;
					default:
					}
				});
			});
			return clazzes;
		});
	}

	public List<T> scan() {
		return this.datas.get();
	}

	private void scan(File file, List<T> datas) {
		if (file.isDirectory()) {
			Stream.of(file.listFiles()).forEach(f -> scan(f, datas));
		} else if (file.isFile()) {
			data(file.getAbsolutePath().replace(File.separator, "/"), datas);
		}
	}

	private void scan(JarFile jar, List<T> datas) {
		Stream.of(jar.entries().asIterator()).filter(entry -> !entry.isDirectory()).forEach(entry -> {
			data(jar.getName() + "?" + entry.getName(), datas);
		});
	}

	private void data(String name, List<T> datas) {
		Matcher matcher = pattern.matcher(name);
		if (matcher.find()) {
			RunUtils.catching(() -> {
				T data = mapper.apply(matcher.groupCount() == 0 ? matcher.group() : matcher.group(1));
				if (filter.test(data)) {
					datas.add(data);
				}
			});
		}
	}

}
