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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import androidx.annotation.WorkerThread;
import androidx.paging.PagedList;
import androidx.paging.RxPagedListBuilder;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.SingleSource;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;

public class OfflineFirstRepository implements FontRepository {

    private final WebFontService webFontService;
    private final WebFontDao dao;
    private final File ExternalStoragePath;
    @SuppressLint("CheckResult")
    public OfflineFirstRepository(Context context, WebFontDatabase database) {
        this.webFontService = new GoogleApiClient(context).getWebFontService();
        ExternalStoragePath = context.getExternalFilesDir(null);
        this.dao = database.webFontDao();
        webFontService.getWebFonts().map(result -> result.items)
                .subscribeOn(Schedulers.io())
                .subscribe(dao::setWebFonts, Throwable::printStackTrace);
    }
    @Override
    public Observable<PagedList<FontFamily>> getFontFamilyList(int orderBy) {
        return getOrderPagedList(orderBy);
    }

    private Observable<PagedList<FontFamily>> getOrderPagedList(int orderBy) {
        switch (orderBy) {
            case 0:
            default:
                return new RxPagedListBuilder<>(
                        dao.getWebFontsSourceOrderByFamily().map(this::convertToFontFamily), 20)
                        .buildObservable();
            case 1:
                return new RxPagedListBuilder<>(
                        dao.getWebFontsSourceOrderByLastModified().map(this::convertToFontFamily), 20)
                        .buildObservable();
        }
    }


    @SuppressLint("CheckResult")
    @Override
    public Observable<List<FontFamily>> getFontFamily() {
        return dao.getWebFonts().flatMap(webFontBeans -> {
            if (webFontBeans.size() == 0) {
                return webFontService.getWebFonts().map(result -> result.items).doOnSuccess(dao::setWebFonts).toObservable();
            }
            return Observable.just(webFontBeans);
        }).map(this::convertToFontFamily).take(1);
    }

    private FontFamily convertToFontFamily(WebFontBean item) {
        Date date;
        try {
            date = parseDate(item.lastModified);
        } catch (ParseException e) {
            date = new Date();
        }
        return FontFamily.builder().setFamily(item.family)
                .setLastModified(date)
                .setFiles(item.files)
                .build();
    }

    private List<FontFamily> convertToFontFamily(List<WebFontBean> beans) {

        final List<FontFamily> families = new ArrayList<>(beans.size());
        for (WebFontBean item : beans) {
            families.add(convertToFontFamily(item));
        }
        return families;
    }

    private Date parseDate(String lastModified) throws ParseException {
        return new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(lastModified);
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
