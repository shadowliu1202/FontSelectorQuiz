package com.shadow.fontselectorquiz.domain.executor;

import com.shadow.fontselectorquiz.domain.model.FontFamily;

import java.io.File;
import java.util.List;

import androidx.paging.PagedList;
import io.reactivex.Observable;
import io.reactivex.Single;

public interface FontRepository {
    Observable<PagedList<FontFamily>> getFontFamilyList();
    Observable<List<FontFamily>> getFontFamily();
    Single<File> getFont(FontFamily family);
}
