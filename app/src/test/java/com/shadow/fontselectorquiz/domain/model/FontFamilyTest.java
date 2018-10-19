package com.shadow.fontselectorquiz.domain.model;

import android.text.TextUtils;

import org.junit.Test;

import java.util.Date;
import java.util.HashMap;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

public class FontFamilyTest {



    @Test
    public void regularFontUrl() {
        HashMap<String,String> maps = new HashMap<>();
        maps.put("regular","regular");
        FontFamily fontFamily = FontFamily.builder()
                .setLastModified(new Date())
                .setFamily("test")
                .setFiles(maps)
                .build();
        assertEquals("regular", fontFamily.regularFontUrl());
    }

    @Test
    public void equals() {
        HashMap<String,String> maps = new HashMap<>();
        maps.put("regular","regular");
        FontFamily fontFamily1 = FontFamily.builder()
                .setLastModified(new Date())
                .setFamily("test")
                .setFiles(maps)
                .build();
        FontFamily fontFamily2 = FontFamily.builder()
                .setLastModified(new Date())
                .setFamily("test")
                .setFiles(maps)
                .build();
        assertEquals(fontFamily1,fontFamily2);
    }
}