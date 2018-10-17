package com.shadow.fontselectorquiz.domain.repository.db;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import androidx.room.TypeConverter;

public class Converters {
    @TypeConverter
    public static List<String> fromString(String value) {
        Type listType = new TypeToken<ArrayList<String>>() {
        }.getType();
        return new Gson().fromJson(value, listType);
    }

    @TypeConverter
    public static String fromArrayList(List<String> list) {
        Gson gson = new Gson();
        return gson.toJson(list);
    }

    @TypeConverter
    public static Map<String, String> fromMapString(String value) {
        Type map = new TypeToken<Map<String, String>>() {}.getType();
        return new Gson().fromJson(value, map);
    }

    @TypeConverter
    public static String fromMap(Map<String, String> map) {
        Gson gson = new Gson();
        return gson.toJson(map);
    }
}
