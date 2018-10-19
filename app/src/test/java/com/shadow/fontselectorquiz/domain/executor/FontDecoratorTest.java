package com.shadow.fontselectorquiz.domain.executor;

import android.content.Context;
import android.graphics.Typeface;

import com.shadow.fontselectorquiz.domain.model.FontFamily;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.io.File;
import java.util.Objects;

import io.reactivex.Observable;
import io.reactivex.Scheduler;
import io.reactivex.Single;
import io.reactivex.observers.TestObserver;
import io.reactivex.schedulers.TestScheduler;

import static org.junit.Assert.*;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class FontDecoratorTest {

    @Mock
    private
    FontTypeFetcher fontTypeFetcher;
    @Mock
    private
    FontFamily fontFamily;
    @Mock
    private
    Context context;
    private
    FontDecorator decorator;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(FontDecoratorTest.this);
        doReturn("test").when(fontFamily).family();
        decorator = new FontDecorator(fontTypeFetcher);
    }

    @Test
    public void getFontTypeFace_From_Service() {
        when(fontTypeFetcher.requestFromService(context,fontFamily.family())).thenReturn(Single.just(Mockito.mock(Typeface.class)));
        TestObserver<Typeface> observer = decorator.getFontTypeFace(context, fontFamily).test();
        observer.awaitTerminalEvent();
        observer.assertValue(Objects::nonNull);
        verify(fontTypeFetcher, times(1)).requestFromService(context, fontFamily.family());
    }

    @Test
    public void getFontTypeFace_Retry_From_Web() {
        when(fontTypeFetcher.requestFromService(context,fontFamily.family())).thenReturn(Single.error(new FontNotFoundException()));
        TestObserver<Typeface> observer = decorator.getFontTypeFace(context, fontFamily).test();
        observer.awaitTerminalEvent();
        verify(fontTypeFetcher, times(1)).requestFromService(context, fontFamily.family());
        verify(fontTypeFetcher, times(1)).requestFromWeb(fontFamily);
    }
}