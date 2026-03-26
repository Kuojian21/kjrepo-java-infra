package com.kjrepo.infra.register.legacy;

import java.util.List;
import java.util.Set;

import org.slf4j.Logger;

import com.google.common.collect.Sets;
import com.kjrepo.infra.common.lazy.LazySupplier;
import com.kjrepo.infra.common.logger.LoggerUtils;
import com.kjrepo.infra.register.Register;
import com.kjrepo.infra.register.RegisterListener;

public class RegisterChildren<T> {

	protected final Logger logger = LoggerUtils.logger(this.getClass());

	private final Set<RegisterChildListener> listeners = Sets.newConcurrentHashSet();

	private final String pKey;
	private final LazySupplier<List<String>> childKeys;
	private final Register<T> register;

	public RegisterChildren(String pKey, LazySupplier<List<String>> childKeys, Register<T> register) {
		super();
		this.pKey = pKey;
		this.childKeys = childKeys;
		this.register = register;
	}

	public List<String> childKeys() {
		return this.childKeys.get();
	}

	public void set(String childKey, T info) {
		this.register.set(wrapKey(childKey), info);
	}

	public T get(String childKey) {
		return this.register.get(wrapKey(childKey));
	}

	public void addKey(String childKey) {
		fireAddChild(childKey);
		this.childKeys.refresh();
	}

	public void removeKey(String childKey) {
		fireRemoveChild(childKey);
		this.childKeys.refresh();
	}

	public void addListener(String childKey, RegisterListener<T> listener) {
		this.register.addListener(wrapKey(childKey), listener);
		logger.info("add listener for {} ", childKey);
	}

	public void addChildListener(RegisterChildListener listener) {
		listeners.add(listener);
		logger.info("add listener for {} ", pKey);
	}

	protected void fireAddChild(String childKey) {
		listeners.forEach(listener -> {
			listener.onCreate(childKey);
		});
	}

	protected void fireRemoveChild(String childKey) {
		listeners.forEach(listener -> {
			listener.onRemove(childKey);
		});
	}

	private String wrapKey(String childKey) {
		return this.pKey + "/" + childKey;
	}

}
