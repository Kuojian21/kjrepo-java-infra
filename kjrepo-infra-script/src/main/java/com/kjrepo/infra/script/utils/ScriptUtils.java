package com.kjrepo.infra.script.utils;

import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.SimpleScriptContext;

import org.slf4j.Logger;

import com.kjrepo.infra.common.logger.LoggerUtils;

public class ScriptUtils {

	private static final Logger logger = LoggerUtils.logger(ScriptUtils.class);
	private static final ScriptEngineManager MANAGER = new ScriptEngineManager();

	static {
		MANAGER.getEngineFactories().forEach(factory -> {
			logger.info("factory:{} supported-engine:{}", factory.getEngineName(), factory.getNames());
		});
	}

	public static ScriptEngine engine(String name) {
		return MANAGER.getEngineByName(name);
	}

	public static ScriptContextBuilder contextBuilder() {
		return new ScriptContextBuilder();
	}

	public static class ScriptContextBuilder {
		private final ScriptContext context = new SimpleScriptContext();

		public ScriptContextBuilder setAttribute(String name, Object value) {
			context.setAttribute(name, value, ScriptContext.ENGINE_SCOPE);
			return this;
		}

		public ScriptContextBuilder setAttribute(String name, Object value, int scope) {
			context.setAttribute(name, value, scope);
			return this;
		}

		public ScriptContext build() {
			return this.context;
		}
	}

}
