package com.kjrepo.infra.script.ognl;

import java.io.Reader;

import javax.script.AbstractScriptEngine;
import javax.script.Bindings;
import javax.script.Compilable;
import javax.script.CompiledScript;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineFactory;
import javax.script.ScriptException;
import javax.script.SimpleBindings;

import org.apache.commons.io.IOUtils;

import com.annimon.stream.function.ThrowableSupplier;
import com.google.common.collect.Maps;

import ognl.ClassResolver;
import ognl.DefaultClassResolver;
import ognl.DefaultTypeConverter;
import ognl.MemberAccess;
import ognl.Node;
import ognl.Ognl;
import ognl.OgnlRuntime;
import ognl.TypeConverter;

public class OgnlScriptEngine extends AbstractScriptEngine implements ScriptEngine, Compilable {

	public static final MemberAccess MEMBER_ACCESS = new OgnlMemberAccess();
	public static final ClassResolver CLASS_RESOLVER = new DefaultClassResolver();
	public static final TypeConverter TYPE_CONVERTER = new DefaultTypeConverter();
	static {
		OgnlRuntime.setPropertyAccessor(ScriptContext.class, new OgnlPropertyAccessor());
	}

	private final OgnlScriptEngineFactory factory;

	public OgnlScriptEngine(OgnlScriptEngineFactory factory) {
		this.factory = factory;
	}

	@Override
	public Object eval(String script, ScriptContext context) throws ScriptException {
		return execute(() -> {
			Node node = (Node) Ognl.parseExpression(script);
			return Ognl.getValue(node, Maps.newHashMap(), context);
		});
	}

	@Override
	public Object eval(Reader reader, ScriptContext context) throws ScriptException {
		return this.eval(readFully(reader), context);
	}

	@Override
	public Bindings createBindings() {
		return new SimpleBindings();
	}

	@Override
	public ScriptEngineFactory getFactory() {
		return this.factory;
	}

	@Override
	public CompiledScript compile(String script) throws ScriptException {
		return new OgnlCompiledScript(this, execute(() -> (Node) Ognl.parseExpression(script)));
	}

	@Override
	public CompiledScript compile(Reader reader) throws ScriptException {
		return this.compile(readFully(reader));
	}

	private String readFully(Reader reader) throws ScriptException {
		return execute(() -> IOUtils.toString(reader));
	}

	<T> T execute(ThrowableSupplier<T, Exception> supplier) throws ScriptException {
		try {
			return supplier.get();
		} catch (Exception e) {
			if (e instanceof ScriptException) {
				throw (ScriptException) e;
			}
			throw new ScriptException(e);
		}
	}
}
