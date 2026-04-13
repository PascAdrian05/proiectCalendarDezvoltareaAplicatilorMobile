package com.example.proiectcalendarumfst.ConectareBackEnd;

import com.example.proiectcalendarumfst.BuildConfig;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    private static final String BASE_URL = normalizeBaseUrl(BuildConfig.BACKEND_BASE_URL);
    private static Retrofit retrofit = null;

    private static String normalizeBaseUrl(String rawBaseUrl) {
        if (rawBaseUrl == null || rawBaseUrl.trim().isEmpty()) {
            return "http://10.0.2.2:8080/";
        }
        return rawBaseUrl.endsWith("/") ? rawBaseUrl : rawBaseUrl + "/";
    }

    public static Retrofit getClient() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}
