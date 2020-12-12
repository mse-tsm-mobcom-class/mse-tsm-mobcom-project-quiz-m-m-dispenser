// Copyright (c) 2020, Steiner Pascal, Strässle Nikolai, Radinger Martin
// All rights reserved.

// Licensed under LICENSE, see LICENSE file

package ch.mse.quiz.runnables;

import ch.mse.quiz.R;
import ch.mse.quiz.ble.BleGattCallback;
import ch.mse.quiz.printes.ToastPrinter;

public class TheftRunnable implements Runnable {

    private final BleGattCallback bleGattCallback;
    private final ToastPrinter toastPrinter;
    private boolean theftNotificationShown;

    public TheftRunnable(ToastPrinter toastPrinter, BleGattCallback bleGattCallback) {
        this.toastPrinter = toastPrinter;
        this.bleGattCallback = bleGattCallback;
    }

    @Override
    public void run() {
        bleGattCallback.getFillingLevelPercentage();
        if (bleGattCallback.isTheft() && !theftNotificationShown) {
            toastPrinter.printError(R.string.current_theft);
            theftNotificationShown = true;
        } else {
            theftNotificationShown = false;
        }
    }

    public boolean isTheftNotificationShown() {
        return theftNotificationShown;
    }
}
