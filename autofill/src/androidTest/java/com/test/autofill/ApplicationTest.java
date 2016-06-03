package com.test.autofill;

import android.test.ActivityInstrumentationTestCase2;
import com.robotium.solo.Solo;

public class ApplicationTest extends ActivityInstrumentationTestCase2 {

    private Solo solo;

    public ApplicationTest() {
        super(MainActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        solo = new Solo(getInstrumentation(), getActivity());
    }

    public void testRun(){
        solo.clickOnEditText(0);
        solo.enterText(0,"15");
        solo.clickOnText("fill");
        solo.sleep(10000);
    }
}
