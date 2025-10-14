package com.kjrepo.infra.text.xml;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.google.common.collect.Lists;

public class XmlModel {
	private String qName;
	private String localName;
	private String uri;
	private String value;
	private String type;
	private List<XmlModel> attributes;
	private List<XmlModel> children;

	public List<XmlModel> getChildrenByQName(String qName) {
		List<XmlModel> models = Lists.newArrayList();
		if (!StringUtils.isEmpty(qName) && this.children != null) {
			this.children.forEach(child -> {
				if (qName.equals(child.getqName())) {
					models.add(child);
				}
			});
		}
		return models;
	}

	public XmlModel getAttributeByQName(String qName) {
		if (!StringUtils.isEmpty(qName) && this.attributes != null) {
			for (XmlModel model : this.attributes) {
				if (qName.equals(model.getqName())) {
					return model;
				}
			}
		}
		return null;
	}

	public XmlModel addChild(XmlModel child) {
		if (this.children == null) {
			this.children = Lists.newArrayList();
		}
		this.children.add(child);
		return this;
	}

	public String getqName() {
		return qName;
	}

	public void setqName(String qName) {
		this.qName = qName;
	}

	public String getLocalName() {
		return localName;
	}

	public void setLocalName(String localName) {
		this.localName = localName;
	}

	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public List<XmlModel> getAttributes() {
		return attributes;
	}

	public void setAttributes(List<XmlModel> attributes) {
		this.attributes = attributes;
	}

	public List<XmlModel> getChildren() {
		return children;
	}

	public void setChildren(List<XmlModel> children) {
		this.children = children;
	}
}
