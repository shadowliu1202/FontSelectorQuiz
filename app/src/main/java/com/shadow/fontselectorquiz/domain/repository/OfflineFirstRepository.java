package com.shadow.fontselectorquiz.domain.repository;

import android.content.Context;
import android.graphics.Typeface;

import com.shadow.fontselectorquiz.domain.executor.FontRepository;
import com.shadow.fontselectorquiz.domain.model.FontFamily;
import com.shadow.fontselectorquiz.domain.repository.bean.WebFontBean;
import com.shadow.fontselectorquiz.domain.repository.bean.WebFontResults;
import com.shadow.fontselectorquiz.domain.repository.cloud.GoogleApiClient;
import com.shadow.fontselectorquiz.domain.repository.cloud.WebFontService;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.functions.Function;

public class OfflineFirstRepository implements FontRepository {

    final WebFontService webFontService;

    public OfflineFirstRepository(Context context) {
        this.webFontService = new GoogleApiClient(context).getWebFontService();
    }

    @Override
    public Observable<List<FontFamily>> getFontFamily() {
        return webFontService.getWebFonts().map(new Function<WebFontResults, List<FontFamily>>() {
            @Override
            public List<FontFamily> apply(WebFontResults webFontResults) throws Exception {
                return convertToFontFamily(webFontResults);
            }
        }).toObservable();
    }

    private List<FontFamily> convertToFontFamily(WebFontResults webFontResults) {
        final List<FontFamily> families = new ArrayList<>(webFontResults.items.size());
        for (WebFontBean item : webFontResults.items) {
            families.add(FontFamily.builder().setFamily(item.family)
                    .setFiles(item.files)
                    .build());
        }
        return families;
    }

    @Override
    public Single<Typeface> getFont(FontFamily family) {
        return null;
    }

}
