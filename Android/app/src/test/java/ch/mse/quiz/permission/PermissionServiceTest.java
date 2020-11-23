package ch.mse.quiz.permission;

import android.app.Activity;

import junit.framework.TestCase;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.robolectric.RobolectricTestRunner;

@RunWith(RobolectricTestRunner.class)
public class PermissionServiceTest extends TestCase {

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();
    @Mock
    private Activity activity;

    @Test
    public void checkPermissions() {
        PermissionService permissionService = new PermissionService();
        permissionService.checkPermissions(activity);
        assertTrue("failed to check permissions", true);
    }
}