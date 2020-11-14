package ch.mse.quiz.permission;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;

import androidx.core.app.ActivityCompat;

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
        return hasPermission(Manifest.permission.ACCESS_FINE_LOCATION);
    }

    /**
     * @param permission {@link String}
     * @return boolean
     */
    private boolean hasPermission(String permission) {
        return ActivityCompat.checkSelfPermission(this.currentActivity, permission) == PackageManager.PERMISSION_GRANTED;
    }
}
