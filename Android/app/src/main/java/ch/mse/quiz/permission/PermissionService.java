package ch.mse.quiz.permission;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;

import androidx.core.app.ActivityCompat;

import static androidx.core.content.ContextCompat.checkSelfPermission;

public class PermissionService {

    private Activity currentActivity;

    /**
     * @param currentActivity {@link Activity}
     */
    public void checkPermissions(Activity currentActivity) {
        this.currentActivity = currentActivity;
        if (!hasRequiredPermissions()) {
            ActivityCompat.requestPermissions(this.currentActivity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PackageManager.PERMISSION_GRANTED);
        }
    }

    /**
     * @return boolean
     */
    public boolean hasRequiredPermissions() {
        return hasPermission();
    }

    /**
     * @return boolean
     */
    private boolean hasPermission() {
        return checkSelfPermission(this.currentActivity, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }
}
