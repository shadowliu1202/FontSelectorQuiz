package com.shadow.fontselectorquiz.domain.repository;

import android.graphics.Typeface;

import com.shadow.fontselectorquiz.domain.executor.FontRepository;
import com.shadow.fontselectorquiz.domain.model.FontFamily;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Single;

public class OfflineFirstRepository implements FontRepository {

    @Override
    public Observable<List<FontFamily>> getFontFamily() {
        return null;
    }

    @Override
    public Single<Typeface> fetchTypeface(String family) {
        return null;
    }
}
