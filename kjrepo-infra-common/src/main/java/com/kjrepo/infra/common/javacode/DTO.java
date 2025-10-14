package com.kjrepo.infra.common.javacode;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;

import com.annimon.stream.Stream;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.common.io.Files;
import com.kjrepo.infra.common.logger.LoggerUtils;

public class DTO {

	public static DTO of(String pkg, String name, Object json) {
		return of(System.getProperty("user.dir") + File.separator
				+ StringUtils.join(new String[] { "src", "main", "java" }, File.separator), pkg, name, json);
	}

	@SuppressWarnings("unchecked")
	public static DTO of(String dir, String pkg, String name, Object json) {
		return new DTO(dir, pkg, name, (Map<String, ?>) json);
	}

	private final Logger logger = LoggerUtils.logger();

	private final String dir;
	private final String pkg;
	private final File fodir;
	private final String name;
	private final Map<String, ?> json;
	private final Set<String> imports = Sets.newHashSet();
	private final List<Property> fields = Lists.newArrayList();
	private final List<DTO> dtos = Lists.newArrayList();

	private DTO(String dir, String pkg, String name, Map<String, ?> json) {
		super();
		this.dir = dir.endsWith(File.separator) ? dir : dir + File.separator;
		this.pkg = pkg;
		this.fodir = new File(this.dir + pkg.replace(".", File.separator));
		this.fodir.mkdirs();
		this.name = name;
		this.json = json;
	}

	public void make() {
		json.forEach((key, val) -> {
			fields.add(Property.of(key, type(key, val)));
		});

		List<String> list = Lists.newArrayList();
		if (StringUtils.isEmpty(pkg)) {

		} else {
			list.add("package " + pkg + ";");
		}
		imports.forEach(im -> {
			list.add("import " + im + ";");
		});
		list.add("public class " + name + "{");

		fields.forEach(p -> {
			list.add("\tprivate " + p.type + " " + p.name + ";");
		});
		fields.forEach(p -> {
			list.add(
					"\tpublic " + p.type + " get" + p.name.substring(0, 1).toUpperCase() + p.name.substring(1) + "(){");
			list.add("\t\treturn " + p.name + ";");
			list.add("\t}");
			list.add("\tpublic void set" + p.name.substring(0, 1).toUpperCase() + p.name.substring(1) + "(" + p.type
					+ " " + p.name + "){");
			list.add("\t\tthis." + p.name + "=" + p.name + ";");
			list.add("\t}");
		});
		list.add("}");

		try {
			String file = this.fodir.getAbsolutePath() + File.separator + this.name + ".java";
			String java = StringUtils.join(list, "\n");
			logger.info("file:{},java:{}", file, java);
			Files.asCharSink(Path.of(file).toFile(), StandardCharsets.UTF_8).write(java);
		} catch (IOException e) {
			logger.error("", e);
		}
		dtos.forEach(dto -> dto.make());
	}

	@SuppressWarnings("unchecked")
	private String type(String key, Object val) {
		if (val == null) {
			return "Object";
		} else if (val instanceof Boolean) {
			return "Boolean";
		} else if (val instanceof String) {
			return "String";
		} else if (val instanceof Integer) {
			return "Integer";
		} else if (val instanceof Long) {
			return "Long";
		} else if (val instanceof Float) {
			return "Float";
		} else if (val instanceof Double) {
			return "Double";
		} else if (val instanceof List) {
			List<?> vlist = (List<?>) val;
//			imports.add("java.util.List");
//			return "List<" + (vlist.isEmpty() ? "Object" : type(key, vlist.get(0))) + ">";
			return (vlist.isEmpty() ? "Object" : type(key, vlist.get(0))) + "[]";
		} else if (val instanceof Map) {
			if (Stream.of((Map<String, ?>) val)
					.anyMatch(e -> !JavaVariableValidator.validate(e.getKey().replaceAll("-", "_")))) {
				imports.add("java.util.Map");
				return "Map<String,Object>";
			}
			String sname = name + key.substring(0, 1).toUpperCase() + key.substring(1);
			this.dtos.add(DTO.of(dir, pkg, sname, (Map<String, ?>) val));
			return sname;
		}
		throw new RuntimeException("");
	}

	public static class Property {

		public static Property of(String name, String type) {
			name = name.replaceAll("-", "_");
			return new Property(name, type);
		}

		private final String name;
		private final String type;

		private Property(String name, String type) {
			super();
			this.name = name;
			this.type = type;
		}

		public String name() {
			return name;
		}

		public String type() {
			return type;
		}

	}

	public static void main(String[] args) {
		System.out.println(System.getProperty("user.dir"));
		System.out.println("com.kj.repo".replace(".", File.separator));
	}

}

class JavaVariableValidator {

	private static final Pattern VARIABLE_NAME_PATTERN = Pattern.compile("^[a-zA-Z_$][a-zA-Z0-9_$]*$");

	private static final Set<String> JAVA_KEYWORDS = Sets.newHashSet("abstract", "assert", "boolean", "break", "byte",
			"case", "catch", "char", "class", "const", "continue", "default", "do", "double", "else", "enum", "extends",
			"final", "finally", "float", "for", "goto", "if", "implements", "import", "instanceof", "int", "interface",
			"long", "native", "new", "package", "private", "protected", "public", "return", "short", "static",
			"strictfp", "super", "switch", "synchronized", "this", "throw", "throws", "transient", "try", "void",
			"volatile", "while", "true", "false", "null");

	public static boolean validate(String name) {
		if (name == null || name.isEmpty()) {
			return false;
		}

		if (!VARIABLE_NAME_PATTERN.matcher(name).matches()) {
			return false;
		}

		if (JAVA_KEYWORDS.contains(name)) {
			return false;
		}

		return true;
	}

	public static String reason(String name) {
		if (name == null) {
			return "The name can not be null!!!";
		}

		if (name.isEmpty()) {
			return "The name can not be empty!!!";
		}

		char fchar = name.charAt(0);
		if (!Character.isJavaIdentifierStart(fchar)) {
			return "The name must start with alphabet,'_' or '$'!!!";
		}

		for (int i = 1; i < name.length(); i++) {
			char c = name.charAt(i);
			if (!Character.isJavaIdentifierPart(c)) {
				return "The name constains invalid char:'" + c + "'!!!";
			}
		}

		if (JAVA_KEYWORDS.contains(name)) {
			return "The name can not be java-keyword!!!" + name;
		}

		return "valid";
	}
}
