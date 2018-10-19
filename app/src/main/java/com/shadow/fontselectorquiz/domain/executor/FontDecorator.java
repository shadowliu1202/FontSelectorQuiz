package com.shadow.fontselectorquiz.domain.executor;

import android.content.Context;
import android.graphics.Typeface;

import com.shadow.fontselectorquiz.domain.model.FontFamily;

import androidx.annotation.MainThread;
import io.reactivex.Single;

public class FontDecorator {
    private final FontTypeFetcher fontTypeFetcher;

    public FontDecorator(FontTypeFetcher fontTypeFetcher) {
        this.fontTypeFetcher = fontTypeFetcher;
    }

    @MainThread
    public Single<Typeface> getFontTypeFace(Context context, FontFamily fontFamily) {
        return fontTypeFetcher.requestFromService(context, fontFamily.family())
                .onErrorResumeNext(throwable -> retryFromWeb(throwable, fontFamily));
    }

    private Single<Typeface> retryFromWeb(Throwable throwable, FontFamily fontFamily) {
        if (throwable instanceof FontNotFoundException) {
            return fontTypeFetcher.requestFromWeb(fontFamily);
        }
        return Single.error(throwable);
    }

}
