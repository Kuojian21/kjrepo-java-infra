package com.kjrepo.infra.text.freemarker;

import java.io.StringWriter;
import java.util.Map;

import com.annimon.stream.function.ThrowableFunction;
import com.google.common.collect.Maps;
import com.kjrepo.infra.common.number.N_humanUtils;
import com.kjrepo.infra.common.number.N_zhUtils;

import freemarker.template.Configuration;
import freemarker.template.Template;

public class Freemarker {

	public static Freemarker freemarker(String basePackagePath) {
		return new Freemarker(basePackagePath, Freemarker.class.getClassLoader());
	}

	public static Freemarker freemarker(String basePackagePath, ClassLoader loader) {
		return new Freemarker(basePackagePath, loader);
	}

	private final Configuration cfg;

	private Freemarker(String basePackagePath, ClassLoader loader) {
		cfg = new Configuration(Configuration.VERSION_2_3_32);
		cfg.setClassLoaderForTemplateLoading(loader, basePackagePath);
	}

	public String process(String ftl, Map<String, Object> data) {
		return process(cfg -> cfg.getTemplate(ftl), data);
	}

	public String process(ThrowableFunction<Configuration, Template, Exception> function, Map<String, Object> data) {
		try {
			Template template = function.apply(this.cfg);
			StringWriter out = new StringWriter();
			Map<String, Object> obj = Maps.newHashMap(data);
			obj.put("N_humanUtils", new N_humanUtils());
			obj.put("N_zhUtils", new N_zhUtils());
			template.process(obj, out);
			return out.toString();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}
