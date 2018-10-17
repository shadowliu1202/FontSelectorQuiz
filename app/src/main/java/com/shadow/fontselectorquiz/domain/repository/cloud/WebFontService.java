package com.shadow.fontselectorquiz.domain.repository.cloud;

import com.shadow.fontselectorquiz.domain.repository.bean.WebFontResults;

import io.reactivex.Single;
import okhttp3.ResponseBody;
import retrofit2.http.GET;
import retrofit2.http.Url;

public interface WebFontService {
    @GET("/webfonts/v1/webfonts")
    Single<WebFontResults> getWebFonts();

    @GET
    Single<ResponseBody> downloadWebFont(@Url String fileUrl);
}
