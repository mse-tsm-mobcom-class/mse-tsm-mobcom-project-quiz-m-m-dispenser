package ch.mse.quiz.runnables;

import android.os.Build;

import junit.framework.TestCase;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import ch.mse.quiz.ble.BleGattCallback;
import ch.mse.quiz.printes.ToastPrinter;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = {Build.VERSION_CODES.O_MR1})
public class TheftRunnableTest extends TestCase {

    @Test
    public void testRun() {
        BleGattCallback bleGattCallback = PowerMockito.mock(BleGattCallback.class);
        PowerMockito.when(bleGattCallback.isTheft()).thenReturn(true);
        ToastPrinter toastPrinter = PowerMockito.mock(ToastPrinter.class);
        TheftRunnable runnable = new TheftRunnable(toastPrinter, bleGattCallback);
        runnable.run();
        assertTrue(runnable.isTheftNotificationShown());
    }

    @Test
    public void testRunNoTheft() {
        BleGattCallback bleGattCallback = PowerMockito.mock(BleGattCallback.class);
        PowerMockito.when(bleGattCallback.isTheft()).thenReturn(false);
        ToastPrinter toastPrinter = PowerMockito.mock(ToastPrinter.class);
        TheftRunnable runnable = new TheftRunnable(toastPrinter, bleGattCallback);
        runnable.run();
        assertFalse(runnable.isTheftNotificationShown());
    }
}