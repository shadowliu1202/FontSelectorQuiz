package com.shadow.fontselectorquiz.domain.repository;

import android.content.Context;
import android.util.Log;

import com.shadow.fontselectorquiz.domain.executor.FontRepository;
import com.shadow.fontselectorquiz.domain.model.FontFamily;
import com.shadow.fontselectorquiz.domain.repository.bean.WebFontBean;
import com.shadow.fontselectorquiz.domain.repository.bean.WebFontResults;
import com.shadow.fontselectorquiz.domain.repository.cloud.GoogleApiClient;
import com.shadow.fontselectorquiz.domain.repository.cloud.WebFontService;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.WorkerThread;
import io.reactivex.Observable;
import io.reactivex.Single;
import okhttp3.ResponseBody;

public class OfflineFirstRepository implements FontRepository {

    private final WebFontService webFontService;
    private final File ExternalStoragePath;
    public OfflineFirstRepository(Context context) {
        this.webFontService = new GoogleApiClient(context).getWebFontService();
        ExternalStoragePath = context.getExternalFilesDir(null);
    }

    @Override
    public Observable<List<FontFamily>> getFontFamily() {
        return webFontService.getWebFonts().map(this::convertToFontFamily).toObservable();
    }

    private List<FontFamily> convertToFontFamily(WebFontResults webFontResults) {
        final List<FontFamily> families = new ArrayList<>(webFontResults.items.size());
        for (WebFontBean item : webFontResults.items) {
            families.add(FontFamily.builder().setFamily(item.family)
                    .setFiles(item.files)
                    .build());
        }
        return families;
    }

    @Override
    public Single<File> getFont(FontFamily family) {
        return webFontService.downloadWebFont(family.regularFontUrl())
                .map(responseBody -> writeFileToDisk(responseBody,family));
    }


    @WorkerThread
    private File writeFileToDisk(ResponseBody body, FontFamily family) throws IOException {
        File ttfFile = new File(ExternalStoragePath + File.separator + family.family()+".ttf");
        if(ttfFile.exists()){
            return ttfFile;
        }
        InputStream inputStream;
        OutputStream outputStream;
        byte[] fileReader = new byte[4096];
        inputStream = body.byteStream();
        outputStream = new FileOutputStream(ttfFile);
        while (true) {
            int read = inputStream.read(fileReader);
            if (read == -1) {
                break;
            }
            outputStream.write(fileReader, 0, read);
        }
        outputStream.flush();
        inputStream.close();
        outputStream.close();
        return ttfFile;
    }

}
