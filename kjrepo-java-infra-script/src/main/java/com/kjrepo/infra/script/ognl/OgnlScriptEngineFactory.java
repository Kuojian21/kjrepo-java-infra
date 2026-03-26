package com.kjrepo.infra.script.ognl;

import java.util.Collections;
import java.util.List;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineFactory;

import com.google.common.collect.Lists;

public class OgnlScriptEngineFactory implements ScriptEngineFactory {

	private static final String ENGINE_NAME = "ognl";
	private static final String ENGINE_VERSION = "0.0.1-SNAPSHOT";
	private static final String LANGUAGE_NAME = "ognl";
	private static final String LANGUAGE_VERSION = "0.0.1-SNAPSHOT";

	private static final List<String> NAMES = Collections.unmodifiableList(Lists.newArrayList(LANGUAGE_NAME));
	private static final List<String> EXTENSIONS = NAMES;
	private static final List<String> MIME_TYPES = Collections.unmodifiableList(Lists.newArrayList());

	@Override
	public String getEngineName() {
		return ENGINE_NAME;
	}

	@Override
	public String getEngineVersion() {
		return ENGINE_VERSION;
	}

	@Override
	public List<String> getExtensions() {
		return EXTENSIONS;
	}

	@Override
	public List<String> getMimeTypes() {
		return MIME_TYPES;
	}

	@Override
	public List<String> getNames() {
		return NAMES;
	}

	@Override
	public String getLanguageName() {
		return LANGUAGE_NAME;
	}

	@Override
	public String getLanguageVersion() {
		return LANGUAGE_VERSION;
	}

	@Override
	public Object getParameter(String key) {
		if (key.equals(ScriptEngine.NAME)) {
			return getLanguageName();
		} else if (key.equals(ScriptEngine.ENGINE)) {
			return getEngineName();
		} else if (key.equals(ScriptEngine.ENGINE_VERSION)) {
			return getEngineVersion();
		} else if (key.equals(ScriptEngine.LANGUAGE)) {
			return getLanguageName();
		} else if (key.equals(ScriptEngine.LANGUAGE_VERSION)) {
			return getLanguageVersion();
		} else if (key.equals("THREADING")) {
			return "THREAD-ISOLATED";
		} else {
			return null;
		}
	}

	@Override
	public String getMethodCallSyntax(String obj, String m, String... args) {
		return null;
	}

	@Override
	public String getOutputStatement(String toDisplay) {
		return null;
	}

	@Override
	public String getProgram(String... statements) {
		return null;
	}

	@Override
	public ScriptEngine getScriptEngine() {
		return new OgnlScriptEngine(this);
	}

}
