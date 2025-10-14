package com.kjrepo.infra.script.ognl;

import javax.script.CompiledScript;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptException;

import com.google.common.collect.Maps;

import ognl.Node;
import ognl.Ognl;

public class OgnlCompiledScript extends CompiledScript {

	private final OgnlScriptEngine scriptEngine;
	private final Node parsedScript;

	public OgnlCompiledScript(OgnlScriptEngine engine, Node parsedScript) {
		this.scriptEngine = engine;
		this.parsedScript = parsedScript;
	}

	@Override
	public Object eval(ScriptContext context) throws ScriptException {
		return scriptEngine.execute(() -> Ognl.getValue(parsedScript, Maps.newHashMap(), context));
	}

	@Override
	public ScriptEngine getEngine() {
		return scriptEngine;
	}
}
