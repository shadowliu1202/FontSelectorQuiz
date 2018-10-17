package com.shadow.fontselectorquiz.domain.repository.db;

import com.shadow.fontselectorquiz.domain.repository.bean.WebFontBean;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import io.reactivex.Observable;

@Dao
public interface WebFontDao {

    @Query("SELECT * from WebFontBean")
    Observable<List<WebFontBean>> getWebFonts();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void setWebFonts(List<WebFontBean> gitHubUserBeans);
}
