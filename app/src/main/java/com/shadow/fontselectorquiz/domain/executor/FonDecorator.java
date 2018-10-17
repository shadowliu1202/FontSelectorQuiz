package com.shadow.fontselectorquiz.domain.executor;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.shadow.fontselectorquiz.R;
import com.shadow.fontselectorquiz.domain.model.FontFamily;


import androidx.core.provider.FontRequest;
import androidx.core.provider.FontsContractCompat;
import io.reactivex.Single;
import io.reactivex.SingleEmitter;
import io.reactivex.SingleOnSubscribe;

public class FonDecorator {
    private final FontRepository repository;
    private final Handler handler;
    public FonDecorator(FontRepository repository) {
        this.repository = repository;
        HandlerThread handlerThread = new HandlerThread("fonts");
        handlerThread.start();
        handler = new android.os.Handler(handlerThread.getLooper());
    }

    public Single<Typeface> getFontTypeFace(Context context, FontFamily fontFamily){
        return requestFromService(context,fontFamily.family());
    }

    private Single<Typeface> requestFromService(final Context context,final String familyName) {
        return Single.create(new SingleOnSubscribe<Typeface>() {
            @Override
            public void subscribe(final SingleEmitter<Typeface> e) {
                final FontRequest request = new FontRequest(
                        "com.google.android.gms.fonts",
                        "com.google.android.gms",
                        "name="+familyName,
                        R.array.com_google_android_gms_fonts_certs);
                FontsContractCompat.requestFont(context, request, new FontsContractCompat.FontRequestCallback(){
                    @Override
                    public void onTypefaceRetrieved(Typeface typeface) {
                        super.onTypefaceRetrieved(typeface);
                        e.onSuccess(typeface);
                    }

                    @Override
                    public void onTypefaceRequestFailed(int reason) {
                        super.onTypefaceRequestFailed(reason);
                        e.onError(new Exception("error:"+reason));
                    }
                }, handler);
            }
        });
    }
}
