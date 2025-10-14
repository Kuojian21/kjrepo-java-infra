package com.kjrepo.infra.crypto.mac;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Mac;
import javax.crypto.SecretKey;

import org.apache.commons.lang3.StringUtils;

import com.kjrepo.infra.crypto.CryptoRuntimeException;
import com.kjrepo.infra.crypto.utils.AlgoKeyUtils;

public class MacUtils {

	public static Mac mac(MacInfo info) {
		try {
			Mac mac = Mac.getInstance(info.getAlgorithm());
			SecretKey secretKey = AlgoKeyUtils.loadKey(info.getKeyAlgorithm(), info.getKey());
			if (StringUtils.isEmpty(info.getPadding())) {
				mac.init(secretKey);
			} else {
				mac.init(secretKey, AlgoKeyUtils.loadIvp(info.getPadding()));
			}
			return mac;
		} catch (InvalidKeyException | NoSuchAlgorithmException | InvalidAlgorithmParameterException e) {
			throw new CryptoRuntimeException(e);
		}
	}

}
