package com.shadow.fontselectorquiz.domain.executor;

import com.shadow.fontselectorquiz.domain.model.FontFamily;

import java.io.File;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Single;

public interface FontRepository {
    Observable<List<FontFamily>> getFontFamily();
    Single<File> getFont(FontFamily family);
}
