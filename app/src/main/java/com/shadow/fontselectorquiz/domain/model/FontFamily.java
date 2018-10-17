package com.shadow.fontselectorquiz.domain.model;

import com.google.auto.value.AutoValue;

import java.util.Date;
import java.util.Map;

@AutoValue
public abstract class FontFamily {
    public abstract String family();
    public abstract Date lastModified();

    public abstract Map<String, String> files();

    public String regularFontUrl() {
        return files().containsKey("regular") ? files().get("regular") : getBestFitUrl();
    }

    private String getBestFitUrl() {
        if (files().size() > 0) return files().values().iterator().next();
        return "";
    }

    public static FontFamily.Builder builder() {
        return new AutoValue_FontFamily.Builder();
    }

    @AutoValue.Builder
    public abstract static class Builder {
        public abstract FontFamily.Builder setFamily(String name);

        public abstract FontFamily.Builder setLastModified(Date lastModified);

        public abstract FontFamily.Builder setFiles(Map<String, String> files);

        public abstract FontFamily build();
    }
}
