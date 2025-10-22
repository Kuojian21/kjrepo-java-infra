package com.kjrepo.infra.gcode;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;

import com.annimon.stream.Stream;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.common.io.Files;
import com.google.protobuf.Descriptors.Descriptor;
import com.google.protobuf.Descriptors.FieldDescriptor;
import com.kjrepo.infra.common.logger.LoggerUtils;
import com.kjrepo.infra.text.freemarker.Freemarker;

public class GenerateModel {

	public static GenerateModel of(String pkg) {
		return of(System.getProperty("user.dir") + File.separator
				+ StringUtils.join(new String[] { "src", "main", "java" }, File.separator), pkg);
	}

	public static GenerateModel of(String dir, String pkg) {
		return new GenerateModel(dir, pkg);
	}

	private final Logger logger = LoggerUtils.logger(getClass());
	private final Freemarker freemarker = Freemarker.freemarker("ftl");
	private final String dir;
	private final String pkg;
	private final File fodir;

	private GenerateModel(String dir, String pkg) {
		super();
		this.dir = dir.endsWith(File.separator) ? dir : dir + File.separator;
		this.pkg = pkg;
		this.fodir = new File(this.dir + pkg.replace(".", File.separator));
		this.fodir.mkdirs();
	}

	@SuppressWarnings("unchecked")
	public void make(String name, Object obj) {
		if (obj instanceof Descriptor) {
			make(name, (Descriptor) obj);
		} else if (obj instanceof Map) {
			make(name, (Map<String, ?>) obj);
		}
	}

	public void make(String name, Descriptor descriptor) {
		make(name, Sets.newHashSet(),
				Stream.of(descriptor.getFields()).map(d -> Property.of(d.getName(), type(name, d))).toList());
	}

	public void make(String name, Map<String, ?> json) {
		List<Property> fields = Lists.newArrayList();
		Set<String> imports = Sets.newHashSet();
		json.forEach((key, val) -> {
			fields.add(Property.of(key, type(name, key, val, imports)));
		});
		make(name, imports, fields);
	}

	public void make(String name, Set<String> imports, List<Property> fields) {
		try {
			String java = freemarker.process("java-model-code-template.ftl",
					ImmutableMap.of("pkg", this.pkg, "name", name, "imports", imports, "fields", fields));
			logger.info("code:{}", java);
			File file = new File(this.fodir.getAbsolutePath() + File.separator + name + ".java");
			if (file.exists()) {
				logger.warn("The java-file:{} has already existed!!!", file.getAbsolutePath());
			} else {
				Files.asCharSink(file, StandardCharsets.UTF_8).write(java);
			}
		} catch (IOException e) {
			logger.error("", e);
		}
	}

	private String type(String name, FieldDescriptor descriptor) {
		switch (descriptor.getJavaType()) {
		case INT:
			return "Integer";
		case LONG:
			return "Long";
		case FLOAT:
			return "Float";
		case DOUBLE:
			return "Double";
		case BOOLEAN:
			return "Boolean";
		case STRING:
			return "String";
		case BYTE_STRING:
			return "String";
		case ENUM:
			return "String";
		case MESSAGE:
			String sname = name + descriptor.getName();
			make(sname, descriptor.getMessageType());
			return sname;
		default:
			return "";
		}
	}

	@SuppressWarnings("unchecked")
	private String type(String name, String key, Object val, Set<String> imports) {
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
			return (vlist.isEmpty() ? "Object" : type(name, key, vlist.get(0), imports)) + "[]";
		} else if (val instanceof Map) {
			if (Stream.of((Map<String, ?>) val)
					.anyMatch(e -> !JavaVariableValidator.validate(e.getKey().replaceAll("-", "_")))) {
				imports.add("java.util.Map");
				return "Map<String,Object>";
			}
			String sname = name + key.substring(0, 1).toUpperCase() + key.substring(1);
			make(sname, (Map<String, ?>) val);
			return sname;
		} else {
			throw new RuntimeException("");
		}
	}

	public static void main(String[] args) {
		System.out.println(System.getProperty("user.dir"));
		System.out.println("com.kj.repo".replace(".", File.separator));
	}

	public static class Property {

		public static Property of(String name, String type) {
			return new Property(name.replaceAll("-", "_"), type);
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
