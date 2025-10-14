package com.kjrepo.infra.crypto.utils;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class Base64Utils {

	public static String decodeToString(String data) {
		if (data == null) {
			return null;
		}
		return new String(decode(data), StandardCharsets.UTF_8);
	}

	public static String decodeToString(byte[] data) {
		if (data == null) {
			return null;
		}
		return new String(decode(data), StandardCharsets.UTF_8);
	}

	public static byte[] decode(String data) {
		if (data == null) {
			return null;
		}
		return decode(data.getBytes(StandardCharsets.UTF_8));
	}

	public static byte[] decode(byte[] data) {
		if (data == null) {
			return null;
		}
		return Base64.getDecoder().decode(data);
	}

	public static String encodeToString(String data) {
		if (data == null) {
			return null;
		}
		return new String(encode(data), StandardCharsets.UTF_8);
	}

	public static String encodeToString(byte[] data) {
		if (data == null) {
			return null;
		}
		return new String(encode(data), StandardCharsets.UTF_8);
	}

	public static byte[] encode(String data) {
		if (data == null) {
			return null;
		}
		return encode(data.getBytes(StandardCharsets.UTF_8));
	}

	public static byte[] encode(byte[] data) {
		if (data == null) {
			return null;
		}
		return Base64.getEncoder().encode(data);
	}

}
