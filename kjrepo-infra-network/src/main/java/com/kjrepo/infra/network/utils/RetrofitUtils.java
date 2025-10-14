package com.kjrepo.infra.network.utils;

import com.kjrepo.infra.network.NetworkRuntimeException;
import com.kjrepo.infra.network.okhttp.OkhttpInfo;
import com.kjrepo.infra.network.okhttp.OkhttpUtils;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.guava.GuavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.jackson.JacksonConverterFactory;
import retrofit2.converter.jaxb.JaxbConverterFactory;
import retrofit2.converter.protobuf.ProtoConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class RetrofitUtils {

	public static <T> T retrofit(String baseUrl, Class<T> iface) {
		try {
			return retrofit(baseUrl, iface, OkhttpUtils.okhttp(new OkhttpInfo()));
		} catch (Exception e) {
			throw new NetworkRuntimeException(e);
		}
	}

	public static <T> T retrofit(String baseUrl, Class<T> iface, OkHttpClient client) {
		return new Retrofit.Builder().baseUrl(baseUrl) //
				.addConverterFactory(ScalarsConverterFactory.create()) //
				.addConverterFactory(ProtoConverterFactory.create()) //
				.addConverterFactory(JaxbConverterFactory.create()) //
				.addConverterFactory(GsonConverterFactory.create()) //
				.addConverterFactory(JacksonConverterFactory.create()) //
				.addCallAdapterFactory(GuavaCallAdapterFactory.create()) //
				.client(client).build().create(iface);
	}

}
