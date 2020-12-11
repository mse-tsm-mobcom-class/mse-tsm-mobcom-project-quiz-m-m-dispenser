package ch.mse.quiz.app;

import android.app.Activity;
import android.os.Build;

import junit.framework.TestCase;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.powermock.api.mockito.PowerMockito;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = {Build.VERSION_CODES.O_MR1})
public class AppTest extends TestCase {

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    @Test
    public void finish() {
        Activity activity = PowerMockito.mock(Activity.class);
        App.activities.add(activity);
        App.finish();
        assertEquals(0, App.activities.size());
    }
}