package com.kjrepo.infra.text.xml;

import java.util.Stack;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class XmlSAXHandler extends DefaultHandler {

	private final Stack<XmlModel> stack = new Stack<>();

	public XmlSAXHandler() {
		stack.push(new XmlModel());
	}

	public XmlModel getModel() {
		return this.stack.peek().getChildren().get(0);
	}

	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		XmlModel parent = stack.peek();
		XmlModel model = new XmlModelBuilder().setqName(qName).setLocalName(localName).setUri(uri)
				.setAttributes(attributes).build();
		stack.push(model);
		parent.addChild(model);
	}

	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
		stack.pop();
	}

	@Override
	public void characters(char ch[], int start, int length) throws SAXException {
		stack.peek().setValue(new String(ch, start, length));
	}

}
