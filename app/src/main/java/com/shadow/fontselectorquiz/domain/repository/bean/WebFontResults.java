
package com.shadow.fontselectorquiz.domain.repository.bean;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class WebFontResults {

    @SerializedName("kind")
    @Expose
    public String kind;
    @SerializedName("items")
    @Expose
    public List<WebFontBean> items = null;

}
