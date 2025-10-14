package com.kjrepo.infra.script.ognl;

import java.util.Map;

import javax.script.ScriptContext;

import ognl.NoSuchPropertyException;
import ognl.OgnlContext;
import ognl.OgnlException;
import ognl.PropertyAccessor;

@SuppressWarnings("rawtypes")
public class OgnlPropertyAccessor implements PropertyAccessor {

	public Object getProperty(Map context, Object target, Object name) throws OgnlException {
		ScriptContext ctx = (ScriptContext) target;
		synchronized (ctx) {
			if (ctx.getAttributesScope((String) name) != -1) {
				return ctx.getAttribute((String) name);
			} else {
				throw new NoSuchPropertyException(target, name);
			}
		}
	}

	public void setProperty(Map context, Object target, Object name, Object value) throws OgnlException {
		ScriptContext ctx = (ScriptContext) target;
		int scope;
		synchronized (ctx) {
			if ((scope = ctx.getAttributesScope((String) name)) != -1) {
				ctx.setAttribute((String) name, value, scope);
			} else {
				ctx.setAttribute((String) name, value, ScriptContext.ENGINE_SCOPE);
			}
		}
	}

	@Override
	public String getSourceAccessor(OgnlContext context, Object target, Object index) {
		context.setCurrentAccessor(ScriptContext.class);
		context.setCurrentType(Object.class);
		return ".getAttribute(" + index.toString() + ")";
	}

	@Override
	public String getSourceSetter(OgnlContext context, Object target, Object index) {
		context.setCurrentAccessor(ScriptContext.class);
		context.setCurrentType(Object.class);
		return ".setAttribute(" + index.toString() + ", $3)";
	}

}
