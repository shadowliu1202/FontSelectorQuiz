package com.shadow.fontselectorquiz.domain.repository.db;

import com.shadow.fontselectorquiz.domain.repository.bean.WebFontBean;

import java.util.List;

import androidx.paging.DataSource;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import io.reactivex.Observable;

@Dao
public interface WebFontDao {

    @Query("SELECT * from WebFontBean")
    Observable<List<WebFontBean>> getWebFonts();

    @Query("SELECT * from WebFontBean  ORDER BY family ASC")
    DataSource.Factory<Integer, WebFontBean> getWebFontsSourceOrderByFamily();

    @Query("SELECT * from WebFontBean  ORDER BY lastModified ASC")
    DataSource.Factory<Integer, WebFontBean> getWebFontsSourceOrderByLastModified();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void setWebFonts(List<WebFontBean> gitHubUserBeans);


}
