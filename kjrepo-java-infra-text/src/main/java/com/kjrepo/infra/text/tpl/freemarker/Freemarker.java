package com.kjrepo.infra.text.tpl.freemarker;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Map;

import com.google.common.collect.Maps;
import com.kjrepo.infra.common.logger.LoggerUtils;
import com.kjrepo.infra.common.number.N_humanUtils;
import com.kjrepo.infra.common.number.N_zhUtils;
import com.kjrepo.infra.text.tpl.TplRender;

import freemarker.template.Configuration;
import freemarker.template.Template;

public class Freemarker implements TplRender {

	public static Freemarker freemarker(File dir) {
		return new Freemarker(dir);
	}

	public static Freemarker freemarker(String basePackagePath) {
		return new Freemarker(basePackagePath, Freemarker.class.getClassLoader());
	}

	public static Freemarker freemarker(String basePackagePath, ClassLoader loader) {
		return new Freemarker(basePackagePath, loader);
	}

	private final Configuration cfg;

	private Freemarker(File dir) {
		cfg = new Configuration(Configuration.VERSION_2_3_32);
		try {
			cfg.setDirectoryForTemplateLoading(dir);
		} catch (IOException e) {
			LoggerUtils.logger(Freemarker.class).error("", e);
		}
	}

	private Freemarker(String basePackagePath, ClassLoader loader) {
		cfg = new Configuration(Configuration.VERSION_2_3_32);
		cfg.setClassLoaderForTemplateLoading(loader, basePackagePath);
	}

	@Override
	public String render(String ftl, Map<String, Object> data) {
		try {
			Template template = this.cfg.getTemplate(ftl);
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
