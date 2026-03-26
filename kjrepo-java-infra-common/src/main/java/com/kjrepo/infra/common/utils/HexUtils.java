package com.kjrepo.infra.common.utils;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;

public class HexUtils {

	public static final String STR = "0123456789ABCDEF";

	public static String toHex(byte[] data) {
		return Hex.encodeHexString(data);
	}

	public static byte[] fromHex(String str) {
		try {
			return Hex.decodeHex(str);
		} catch (DecoderException e) {
			char[] hexs = str.toUpperCase().toCharArray();
			byte[] datas = new byte[hexs.length / 2];
			for (int i = 0, len = datas.length; i < len; i++) {
				datas[i] = (byte) (((STR.indexOf(hexs[2 * i]) << 4) + STR.indexOf(hexs[2 * i + 1])) & 0xFF);
			}
			return datas;
		}
	}
}
