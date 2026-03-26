package com.kjrepo.infra.text.xml;

import org.apache.commons.lang3.StringUtils;
import org.xml.sax.Attributes;

import com.google.common.collect.Lists;

public class XmlModelBuilder {

	private final XmlModel model = new XmlModel();

	public XmlModelBuilder setqName(String qName) {
		if (!StringUtils.isEmpty(qName)) {
			model.setqName(qName);
		}
		return this;
	}

	public XmlModelBuilder setLocalName(String localName) {
		if (!StringUtils.isEmpty(localName)) {
			model.setLocalName(localName);
		}
		return this;
	}

	public XmlModelBuilder setUri(String uri) {
		if (!StringUtils.isEmpty(uri)) {
			model.setUri(uri);
		}
		return this;
	}

	public XmlModelBuilder setValue(String value) {
		if (!StringUtils.isEmpty(value)) {
			model.setValue(value);
		}
		return this;
	}

	public XmlModelBuilder setType(String type) {
		if (!StringUtils.isEmpty(type)) {
			model.setType(type);
		}
		return this;
	}

	public XmlModelBuilder setAttributes(Attributes attributes) {
		if (attributes.getLength() > 0) {
			model.setAttributes(Lists.newArrayList());
			for (int i = 0; i < attributes.getLength(); i++) {
				model.getAttributes()
						.add(new XmlModelBuilder().setqName(attributes.getQName(i))
								.setLocalName(attributes.getLocalName(i)).setUri(attributes.getURI(i))
								.setValue(attributes.getValue(i)).setType(attributes.getType(i)).build());
			}
		}

		return this;
	}

	public XmlModel build() {
		return model;
	}
}
