package com.shadow.fontselectorquiz.domain.executor;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.SparseArray;

import com.shadow.fontselectorquiz.R;
import com.shadow.fontselectorquiz.domain.model.FontFamily;

import java.util.concurrent.ConcurrentHashMap;

import androidx.annotation.MainThread;
import androidx.annotation.WorkerThread;
import androidx.core.provider.FontRequest;
import androidx.core.provider.FontsContractCompat;
import io.reactivex.Single;
import io.reactivex.SingleEmitter;
import io.reactivex.SingleOnSubscribe;
import io.reactivex.schedulers.Schedulers;

import static androidx.core.provider.FontsContractCompat.FontRequestCallback.FAIL_REASON_FONT_NOT_FOUND;

public class FontTypeFetcher {
    private final static int MAX_HANDLER = 20;
    private final SparseArray<Handler> handlers = new SparseArray<>(MAX_HANDLER);
    private final ConcurrentHashMap<String, Typeface> typefaces = new ConcurrentHashMap<>();
    private final FontRepository repository;
    private static FontTypeFetcher fetcher;

    public static FontTypeFetcher getInstance(FontRepository repository) {
        if (fetcher == null) {
            fetcher = new FontTypeFetcher(repository);
        }
        return fetcher;
    }

    private FontTypeFetcher(FontRepository repository) {
        this.repository = repository;
    }

    @MainThread
    Single<Typeface> requestFromService(final Context context, final String familyName) {
        Typeface typeface = typefaces.get(familyName);
        if (typeface != null) {
            return Single.just(typeface);
        }

        return Single.create(new SingleOnSubscribe<Typeface>() {
            @Override
            public void subscribe(final SingleEmitter<Typeface> e) {
                final FontRequest request = new FontRequest(
                        "com.google.android.gms.fonts",
                        "com.google.android.gms",
                        "name=" + familyName,
                        R.array.com_google_android_gms_fonts_certs);
                int bucket = familyName.hashCode() % MAX_HANDLER;
                if (handlers.get(bucket) == null) {
                    HandlerThread handlerThread = new HandlerThread(String.valueOf(bucket));
                    handlerThread.start();
                    handlers.put(bucket, new Handler(handlerThread.getLooper()));
                }

                FontsContractCompat.requestFont(context, request, new FontsContractCompat.FontRequestCallback() {
                    @Override
                    public void onTypefaceRetrieved(Typeface typeface) {
                        super.onTypefaceRetrieved(typeface);
                        if (!e.isDisposed()) {
                            e.onSuccess(typeface);
                        }
                    }

                    @Override
                    public void onTypefaceRequestFailed(int reason) {
                        super.onTypefaceRequestFailed(reason);
                        if (!e.isDisposed()) {
                            e.onError(handleError(reason));
                        }
                    }
                }, handlers.get(bucket));
            }
        }).doOnSuccess(typeface1 -> typefaces.put(familyName, typeface1));
    }

    @WorkerThread
    Single<Typeface> requestFromWeb(FontFamily fontFamily) {
        return repository.getFont(fontFamily).subscribeOn(Schedulers.io()).map(Typeface::createFromFile)
                .doOnSuccess(typeface1 -> typefaces.put(fontFamily.family(), typeface1));
    }

    private Throwable handleError(int reason) {
        if (reason == FAIL_REASON_FONT_NOT_FOUND) {
            return new FontNotFoundException();
        }
        return new Exception("Error:" + reason);
    }
}
