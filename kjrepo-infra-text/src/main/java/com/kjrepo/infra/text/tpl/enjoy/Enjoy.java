package com.kjrepo.infra.text.tpl.enjoy;

import java.io.File;
import java.util.Map;

import com.jfinal.template.Engine;
import com.kjrepo.infra.text.tpl.TplRender;

public class Enjoy implements TplRender {

	private final String workspace;
	private final Engine engine;

	public Enjoy(File dir) {
		this.workspace = dir.getAbsolutePath();
		this.engine = Engine.createIfAbsent(workspace, e -> {
		}).setDevMode(true);
	}

	@Override
	public String render(String ftl, Map<String, Object> data) {
		return this.engine.getTemplate(this.workspace + File.separator + ftl).renderToString(data);
	}

}
