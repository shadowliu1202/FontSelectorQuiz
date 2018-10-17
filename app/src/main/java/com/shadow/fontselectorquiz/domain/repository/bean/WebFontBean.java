
package com.shadow.fontselectorquiz.domain.repository.bean;

import java.util.List;
import java.util.Map;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;


@Entity
public class WebFontBean {

    @SerializedName("kind")
    @Expose
    public String kind;
    @PrimaryKey
    @NonNull
    @SerializedName("family")
    @Expose
    public String family = "";
    @SerializedName("category")
    @Expose
    public String category;
    @SerializedName("variants")
    @Expose
    public List<String> variants = null;
    @SerializedName("subsets")
    @Expose
    public List<String> subsets = null;
    @SerializedName("version")
    @Expose
    public String version;
    @SerializedName("lastModified")
    @Expose
    public String lastModified;
    @SerializedName("files")
    @Expose
    public Map<String, String> files;
}
