package com.kjrepo.infra.text.tpl.beetl;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import org.beetl.core.Configuration;
import org.beetl.core.GroupTemplate;
import org.beetl.core.Template;
import org.beetl.core.resource.FileResourceLoader;

import com.kjrepo.infra.text.tpl.TplRender;

public class Beetl implements TplRender {

	private final GroupTemplate templates;

	public Beetl(File dir) {
		try {
			FileResourceLoader resourceLoader = new FileResourceLoader(dir.getAbsolutePath(), "utf-8");
			Configuration cfg = Configuration.defaultConfiguration();
			this.templates = new GroupTemplate(resourceLoader, cfg);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public String render(String ftl, Map<String, Object> data) {
		Template template = this.templates.getTemplate(ftl);
		template.binding(data);
		return template.render();
	}

}
