package com.kjrepo.infra.script.mvel;

import java.util.Map;
import java.util.concurrent.ConcurrentMap;

import org.mvel2.MVEL;
import org.mvel2.compiler.ExecutableAccessor;
import org.mvel2.optimizers.OptimizerFactory;

import com.google.common.collect.Maps;

public class MvelUtil {
	private static final ConcurrentMap<String, ExecutableAccessor> EXPR_MAP = Maps.newConcurrentMap();

	static {
		OptimizerFactory.setDefaultOptimizer("ASM");
	}

	@SuppressWarnings("unchecked")
	public static <T> T execute(String expr, Map<String, Object> vars) {
		ExecutableAccessor accessor = EXPR_MAP.computeIfAbsent(expr,
				key -> (ExecutableAccessor) MVEL.compileExpression(key));
		return (T) MVEL.executeExpression(accessor, vars);
	}

}
