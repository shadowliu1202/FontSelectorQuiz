package com.shadow.fontselectorquiz.domain.repository.cloud;

import com.shadow.fontselectorquiz.domain.repository.bean.WebFontResults;

import java.util.List;

import io.reactivex.Single;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface WebFontService {
    @GET("/webfonts/v1/webfonts")
    Single<List<WebFontResults>> getGitHubUsers(@Query("since") int since);
}
