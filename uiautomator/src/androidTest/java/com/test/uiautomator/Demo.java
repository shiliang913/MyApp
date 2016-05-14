package com.test.uiautomator;

import android.app.Instrumentation;
import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.support.test.uiautomator.UiDevice;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class Demo {

    Instrumentation instrumentation;
    UiDevice device;
    Context context;

    @Before
    public void setUp(){
        instrumentation = InstrumentationRegistry.getInstrumentation();
        context = InstrumentationRegistry.getContext();
        device = UiDevice.getInstance(instrumentation);
    }

    @Test
    public void test() throws Exception{
    }
}
