package com.kjrepo.infra.crypto.utils;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import com.annimon.stream.Optional;

public class Base64Utils {

	public static String encodeToString(String data) {
		return Optional.ofNullable(data).map(s -> s.getBytes(StandardCharsets.UTF_8)).map(Base64Utils::encode)
				.map(b -> new String(b, StandardCharsets.UTF_8)).orElse(null);
	}

	public static String decodeToString(String data) {
		return Optional.ofNullable(data).map(s -> s.getBytes(StandardCharsets.UTF_8)).map(Base64Utils::decode)
				.map(b -> new String(b, StandardCharsets.UTF_8)).orElse(null);
	}

	public static String encodeToString(byte[] data) {
		return Optional.ofNullable(data).map(Base64Utils::encode).map(b -> new String(b, StandardCharsets.UTF_8))
				.orElse(null);
	}

	public static String decodeToString(byte[] data) {
		return Optional.ofNullable(data).map(Base64Utils::decode).map(b -> new String(b, StandardCharsets.UTF_8))
				.orElse(null);
	}

	public static byte[] encode(String data) {
		return Optional.ofNullable(data).map(s -> s.getBytes(StandardCharsets.UTF_8)).map(Base64Utils::encode)
				.orElse(null);
	}

	public static byte[] decode(String data) {
		return Optional.ofNullable(data).map(s -> s.getBytes(StandardCharsets.UTF_8)).map(Base64Utils::decode)
				.orElse(null);
	}

	public static byte[] encode(byte[] data) {
		return Optional.ofNullable(data).map(Base64.getEncoder()::encode).orElse(null);
	}

	public static byte[] decode(byte[] data) {
		return Optional.ofNullable(data).map(Base64.getDecoder()::decode).orElse(null);
	}

}
