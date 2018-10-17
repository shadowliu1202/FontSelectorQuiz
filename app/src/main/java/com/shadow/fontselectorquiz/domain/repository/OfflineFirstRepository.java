package com.shadow.fontselectorquiz.domain.repository;

import com.shadow.fontselectorquiz.domain.executor.FontRepository;
import com.shadow.fontselectorquiz.domain.model.FontFamily;

import java.util.List;

import io.reactivex.Observable;

public class OfflineFirstRepository implements FontRepository {

    @Override
    public Observable<List<FontFamily>> getFontFamily() {
        return null;
    }

}
