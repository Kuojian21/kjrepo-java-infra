package com.kjrepo.infra.crypto.digest;

import java.security.MessageDigest;
import java.util.List;

import com.kjrepo.infra.crypto.Crypto;

public class Digest extends Crypto<MessageDigest, DigestInfo> {

	public Digest(DigestInfo info) {
		super(info);
	}

	@Override
	protected MessageDigest create() throws Exception {
		return MessageDigest.getInstance(info().getAlgorithm());
	}

	@Override
	protected void init(MessageDigest digest) {
		digest.reset();
	}

	public final String digest(String data) {
		return encrypt(data);
	}

	public final byte[] digest(byte[]... datas) {
		return crypt(datas);
	}

	@Override
	protected byte[] crypt(List<byte[]> datas) throws Exception {
		return execute(digest -> {
			datas.forEach(digest::update);
			return digest.digest();
		});
	}

}
