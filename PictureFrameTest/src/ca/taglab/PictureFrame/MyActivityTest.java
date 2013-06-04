package ca.taglab.PictureFrame;

import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.SmallTest;

/**
 * This is a simple framework for a test of an Application.  See
 * {@link android.test.ApplicationTestCase ApplicationTestCase} for more information on
 * how to write and extend Application tests.
 * <p/>
 * To run this test, you can type:
 * adb shell am instrument -w \
 * -e class ca.taglab.PictureFrame.MyActivityTest \
 * ca.taglab.PictureFrame.tests/android.test.InstrumentationTestRunner
 */
public class MyActivityTest extends ActivityInstrumentationTestCase2<MyActivity> {

    public MyActivityTest() {
        super("ca.taglab.PictureFrame", MyActivity.class);
    }

    @SmallTest
    public void testBlah() {
        assertEquals(1, 1);
    }

}
