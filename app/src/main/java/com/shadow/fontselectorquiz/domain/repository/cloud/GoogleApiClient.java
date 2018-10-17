package com.shadow.fontselectorquiz.domain.repository.cloud;

import android.content.Context;

import java.io.File;
import java.io.IOException;

import androidx.annotation.NonNull;
import okhttp3.Cache;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class GoogleApiClient {
    private static final String BASE_URL = "https://www.googleapis.com/";
    private static final String API_KEY = "AIzaSyDgWsKw6WOO5Bkk5kuRa-oigC4XSecrOu0";
    private static OkHttpClient mOkHttpClient;
    private Retrofit retrofit;

    public GoogleApiClient(Context context) {
        initOkHttpClient(context);
        initRetrofit();
    }

    private void initOkHttpClient(Context context) {
        if (mOkHttpClient == null) {
            synchronized (GoogleApiClient.class) {
                if (mOkHttpClient == null) {
                    Cache cache = new Cache(new File(context.getCacheDir(), "Cache"), 1024 * 1024 * 100);
                    mOkHttpClient = new OkHttpClient.Builder()
                            .addInterceptor(addAPIKey())
                            .cache(cache)
                            .retryOnConnectionFailure(true)
                            .build();
                }
            }
        }
    }

    private Interceptor addAPIKey() {
        return new Interceptor() {
            @Override
            public Response intercept(@NonNull Chain chain) throws IOException {
                Request original = chain.request();
                HttpUrl url = original.url().newBuilder()
                        .addQueryParameter("key", API_KEY)
                        .build();
                return chain.proceed(original.newBuilder().url(url).build());
            }
        };
    }

    private void initRetrofit() {
        retrofit = new retrofit2.Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(mOkHttpClient)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    public WebFontService getWebFontService() {
        return retrofit.create(WebFontService.class);
    }
}
