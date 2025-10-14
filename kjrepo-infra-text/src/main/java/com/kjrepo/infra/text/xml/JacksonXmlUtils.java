package com.kjrepo.infra.text.xml;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;

import org.apache.commons.lang3.StringUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.annotation.JsonInclude;

public class JacksonXmlUtils {

	public static final XmlMapper MAPPER = XmlMapper.builder()
			.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
			.serializationInclusion(JsonInclude.Include.NON_NULL)
			.propertyNamingStrategy(PropertyNamingStrategy.LOWER_CAMEL_CASE).build();

	public static String toXml(Object obj) {
		if (obj == null) {
			return null;
		}
		try {
			return MAPPER.writeValueAsString(obj);
		} catch (JsonProcessingException e) {
			throw new RuntimeException(e);
		}
	}

	public static String toPrettyXml(Object obj) {
		if (obj == null) {
			return null;
		}
		try {
			return MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(obj);
		} catch (JsonProcessingException e) {
			throw new RuntimeException(e);
		}
	}

	public static <T> T fromXml(String xml, Class<T> clazz) {
		if (StringUtils.isEmpty(xml)) {
			return null;
		}
		try {
			return MAPPER.readValue(xml, clazz);
		} catch (JsonProcessingException e) {
			throw new RuntimeException(e);
		}
	}

	public static <T> T fromXml(InputStream is, Class<T> clazz) {
		if (is == null) {
			return null;
		}
		try {
			return MAPPER.readValue(is, clazz);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static <T> T fromXml(Reader reader, Class<T> clazz) {
		if (reader == null) {
			return null;
		}
		try {
			return MAPPER.readValue(reader, clazz);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

}
