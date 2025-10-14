package com.kjrepo.infra.common.utils;

public class HexUtils {

	public static final String STR = "0123456789ABCDEF";

	public static String toHex(byte[] datas) {
		StringBuilder sb = new StringBuilder();
		for (byte data : datas) {
			sb.append(Integer.toHexString(data & 0xFF));
		}
		return sb.toString();
	}

	public static byte[] fromHex(String str) {
		char[] hexs = str.toUpperCase().toCharArray();
		byte[] datas = new byte[hexs.length / 2];
		for (int i = 0, len = datas.length; i < len; i++) {
			datas[i] = (byte) (((STR.indexOf(hexs[2 * i]) << 4) + STR.indexOf(hexs[2 * i + 1])) & 0xFF);
		}
		return datas;
	}
}
