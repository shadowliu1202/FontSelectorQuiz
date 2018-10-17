package com.shadow.fontselectorquiz.domain.executor;

import com.shadow.fontselectorquiz.domain.model.FontFamily;

import java.util.List;

import io.reactivex.Observable;

public interface FontRepository {
    Observable<List<FontFamily>> getFontFamily();
}
