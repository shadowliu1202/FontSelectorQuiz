package com.shadow.fontselectorquiz.domain.repository.db;

import android.content.Context;

import com.shadow.fontselectorquiz.domain.repository.bean.WebFontBean;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

@Database(entities = {WebFontBean.class}, version = 1 ,exportSchema = false)
@TypeConverters({Converters.class})
public abstract class WebFontDatabase extends RoomDatabase {
    public abstract WebFontDao webFontDao();

    private static WebFontDatabase INSTANCE;

    public static WebFontDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (WebFontDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            WebFontDatabase.class, "webfont_database")
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
