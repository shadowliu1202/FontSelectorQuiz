package com.shadow.fontselectorquiz.domain.repository;

import android.annotation.SuppressLint;
import android.content.Context;

import com.shadow.fontselectorquiz.domain.executor.FontRepository;
import com.shadow.fontselectorquiz.domain.model.FontFamily;
import com.shadow.fontselectorquiz.domain.repository.bean.WebFontBean;
import com.shadow.fontselectorquiz.domain.repository.cloud.GoogleApiClient;
import com.shadow.fontselectorquiz.domain.repository.cloud.WebFontService;
import com.shadow.fontselectorquiz.domain.repository.db.WebFontDao;
import com.shadow.fontselectorquiz.domain.repository.db.WebFontDatabase;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.WorkerThread;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Single;
import io.reactivex.SingleSource;
import io.reactivex.functions.Function;
import okhttp3.ResponseBody;

public class OfflineFirstRepository implements FontRepository {

    private final WebFontService webFontService;
    private final WebFontDao dao;
    private final File ExternalStoragePath;

    public OfflineFirstRepository(Context context, WebFontDatabase database) {
        this.webFontService = new GoogleApiClient(context).getWebFontService();
        ExternalStoragePath = context.getExternalFilesDir(null);
        this.dao = database.webFontDao();
    }

    @SuppressLint("CheckResult")
    @Override
    public Observable<List<FontFamily>> getFontFamily() {
        return dao.getWebFonts().flatMap(webFontBeans -> {
            if(webFontBeans.size() == 0){
                return webFontService.getWebFonts().map(result->result.items).doOnSuccess(dao::setWebFonts).toObservable();
            }
            return Observable.just(webFontBeans);
        }).map(this::convertToFontFamily).take(1);
    }

    private List<FontFamily> convertToFontFamily(List<WebFontBean> beans) {

        final List<FontFamily> families = new ArrayList<>(beans.size());
        for (WebFontBean item : beans) {
            families.add(FontFamily.builder().setFamily(item.family)
                    .setFiles(item.files)
                    .build());
        }
        return families;
    }

    @Override
    public Single<File> getFont(FontFamily family) {
        return Single.defer(() -> Single.just(family))
                .flatMap((Function<FontFamily, SingleSource<File>>) fontFamily -> {
                    File ttfFile = new File(ExternalStoragePath + File.separator + family.family() + ".ttf");
                    if (ttfFile.exists()) {
                        return Single.just(ttfFile);
                    }
                    return webFontService.downloadWebFont(family.regularFontUrl()).map(responseBody -> writeFileToDisk(responseBody, ttfFile));
                });
    }


    @WorkerThread
    private File writeFileToDisk(ResponseBody body, File ttfFile) throws IOException {
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
