package com.shadow.fontselectorquiz.domain.executor;

import android.graphics.Typeface;

import com.shadow.fontselectorquiz.domain.model.FontFamily;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Single;

public interface FontRepository {
    Observable<List<FontFamily>> getFontFamily();
    Single<Typeface> getFont(FontFamily family);
}
