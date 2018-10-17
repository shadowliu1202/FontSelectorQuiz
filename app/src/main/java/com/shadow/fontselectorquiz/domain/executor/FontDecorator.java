package com.shadow.fontselectorquiz.domain.executor;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Handler;
import com.shadow.fontselectorquiz.R;
import com.shadow.fontselectorquiz.domain.model.FontFamily;

import androidx.annotation.MainThread;
import androidx.core.provider.FontRequest;
import androidx.core.provider.FontsContractCompat;
import io.reactivex.Single;
import io.reactivex.SingleEmitter;
import io.reactivex.SingleOnSubscribe;
import io.reactivex.schedulers.Schedulers;

import static androidx.core.provider.FontsContractCompat.FontRequestCallback.FAIL_REASON_FONT_NOT_FOUND;

public class FontDecorator {
    private final FontRepository repository;

    public FontDecorator(FontRepository repository) {
        this.repository = repository;
    }

    @MainThread
    public Single<Typeface> getFontTypeFace(Context context, FontFamily fontFamily) {
        return requestFromService(context, fontFamily.family())
                .onErrorResumeNext(throwable -> retryFromWeb(throwable,fontFamily));
    }

    private Single<Typeface> retryFromWeb(Throwable throwable, FontFamily fontFamily) {
        if (throwable instanceof FontNotFoundException) {
            return repository.getFont(fontFamily).subscribeOn(Schedulers.io()).map(Typeface::createFromFile);
        }
        return Single.error(throwable);
    }

    @MainThread
    private Single<Typeface> requestFromService(final Context context, final String familyName) {
        return Single.create(new SingleOnSubscribe<Typeface>() {
            @Override
            public void subscribe(final SingleEmitter<Typeface> e) {
                final FontRequest request = new FontRequest(
                        "com.google.android.gms.fonts",
                        "com.google.android.gms",
                        "name=" + familyName,
                        R.array.com_google_android_gms_fonts_certs);
                FontsContractCompat.requestFont(context, request, new FontsContractCompat.FontRequestCallback() {
                    @Override
                    public void onTypefaceRetrieved(Typeface typeface) {
                        super.onTypefaceRetrieved(typeface);
                        e.onSuccess(typeface);
                    }

                    @Override
                    public void onTypefaceRequestFailed(int reason) {
                        super.onTypefaceRequestFailed(reason);
                        if (!e.isDisposed()) {
                            e.onError(handleError(reason));
                        }
                    }
                }, new Handler());
            }
        });
    }

    private Throwable handleError(int reason) {
        if (reason == FAIL_REASON_FONT_NOT_FOUND) {
            return new FontNotFoundException();
        }
        return new Exception("Error:" + reason);
    }
}
