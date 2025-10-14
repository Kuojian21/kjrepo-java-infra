package com.kjrepo.infra.text.xml;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

public class XmlUtils {
	private static final SAXParserFactory FACTORY = SAXParserFactory.newInstance();
	static {
		FACTORY.setNamespaceAware(true);
		FACTORY.setValidating(true);
	}

	public static XmlModel fromXml(String xml) {
		return fromXml(new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8)));
	}

	public static XmlModel fromXml(InputStream xml) {
		try {
			SAXParser parser = FACTORY.newSAXParser();
			XmlSAXHandler handler = new XmlSAXHandler();
			parser.parse(xml, handler);
			return handler.getModel();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static XmlModel fromXml(File xml) {
		try {
			SAXParser parser = FACTORY.newSAXParser();
			XmlSAXHandler handler = new XmlSAXHandler();
			parser.parse(xml, handler);
			return handler.getModel();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
