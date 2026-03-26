package com.kjrepo.infra.script.ognl;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Member;
import java.lang.reflect.Modifier;
import java.util.Map;

import ognl.MemberAccess;

@SuppressWarnings("rawtypes")
public class OgnlMemberAccess implements MemberAccess {

	public OgnlMemberAccess() {
	}

	@SuppressWarnings("deprecation")
	@Override
	public Object setup(Map context, Object target, Member member, String propertyName) {
		Object result = null;
		if (isAccessible(context, target, member, propertyName)) {
			AccessibleObject accessible = (AccessibleObject) member;
			if (!accessible.isAccessible()) {
				result = Boolean.TRUE;
				accessible.setAccessible(true);
			}
		}
		return result;
	}

	@Override
	public void restore(Map context, Object target, Member member, String propertyName, Object state) {
		if (state != null) {
			((AccessibleObject) member).setAccessible((Boolean) state);
		}
	}

	@Override
	public boolean isAccessible(Map context, Object target, Member member, String propertyName) {
		int modifiers = member.getModifiers();
		if (Modifier.isPublic(modifiers)) {
			return true;
		} else if (Modifier.isPrivate(modifiers)) {
			return true;
		} else if (Modifier.isProtected(modifiers)) {
			return true;
		} else {
			return true;
		}
	}
}