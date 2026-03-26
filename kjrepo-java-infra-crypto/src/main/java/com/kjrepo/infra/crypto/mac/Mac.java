package com.kjrepo.infra.crypto.mac;

import java.util.List;
import javax.crypto.SecretKey;

import org.apache.commons.lang3.StringUtils;

import com.kjrepo.infra.crypto.Crypto;
import com.kjrepo.infra.crypto.utils.AlgoKeyUtils;
import com.kjrepo.infra.crypto.utils.AlgoParameterUtils;

public class Mac extends Crypto<javax.crypto.Mac, MacInfo> {

	public Mac(MacInfo info) {
		super(info);
	}

	@Override
	protected javax.crypto.Mac create() throws Exception {
		MacInfo info = info();
		javax.crypto.Mac mac = javax.crypto.Mac.getInstance(info.getAlgorithm());
		SecretKey secretKey = AlgoKeyUtils.loadKey(info.getKeyAlgorithm(), info.getKey());
		if (StringUtils.isEmpty(info.getPadding())) {
			mac.init(secretKey);
		} else {
			mac.init(secretKey, AlgoParameterUtils.loadIvp(info.getPadding()));
		}
		return mac;
	}

	@Override
	protected void init(javax.crypto.Mac mac) {
		mac.reset();
	}

	public String mac(String data) {
		return encrypt(data);
	}

	public byte[] mac(byte[]... datas) {
		return crypt(datas);
	}

	@Override
	protected byte[] crypt(List<byte[]> datas) throws Exception {
		return execute(mac -> {
			datas.forEach(mac::update);
			return mac.doFinal();
		});
	}

}
