// Copyright (c) 2020, Steiner Pascal, Strässle Nikolai, Radinger Martin
// All rights reserved.

// Licensed under LICENSE, see LICENSE file

package ch.mse.quiz.listeners;

import android.app.Activity;
import android.os.Build;
import android.widget.NumberPicker;

import junit.framework.TestCase;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.ArrayList;

import ch.mse.quiz.MainActivity;
import ch.mse.quiz.ble.BleGattCallback;
import ch.mse.quiz.printes.ToastPrinter;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = {Build.VERSION_CODES.O_MR1})
public class StartQuizListenerTest extends TestCase {

    @Test
    public void onClick() {
        Activity activity = PowerMockito.mock(MainActivity.class);
        ToastPrinter toastPrinter = PowerMockito.mock(ToastPrinter.class);
        BleGattCallback bleGattCallback = PowerMockito.mock(BleGattCallback.class);
        NumberPicker npTopics = PowerMockito.mock(NumberPicker.class);
        NumberPicker npNumberOfQuestions = PowerMockito.mock(NumberPicker.class);
        ArrayList<String> topics = new ArrayList<>();
        topics.add("Test");

        PowerMockito.when(bleGattCallback.isConnected()).thenReturn(true);
        PowerMockito.when(npTopics.getValue()).thenReturn(5);
        PowerMockito.when(npNumberOfQuestions.getValue()).thenReturn(2);

        StartQuizListener startQuizListener = new StartQuizListener(activity, toastPrinter, bleGattCallback, npTopics, npNumberOfQuestions, topics);
        startQuizListener.onClick(null);
        Mockito.verify(activity, Mockito.times(1)).startActivity(Mockito.any());
    }

    @Test
    public void onClickNotConencted() {
        Activity activity = PowerMockito.mock(MainActivity.class);
        ToastPrinter toastPrinter = PowerMockito.mock(ToastPrinter.class);
        BleGattCallback bleGattCallback = PowerMockito.mock(BleGattCallback.class);
        NumberPicker npTopics = PowerMockito.mock(NumberPicker.class);
        NumberPicker npNumberOfQuestions = PowerMockito.mock(NumberPicker.class);
        ArrayList<String> topics = new ArrayList<>();

        PowerMockito.when(bleGattCallback.isConnected()).thenReturn(false);

        StartQuizListener startQuizListener = new StartQuizListener(activity, toastPrinter, bleGattCallback, npTopics, npNumberOfQuestions, topics);
        startQuizListener.onClick(null);
        Mockito.verify(activity, Mockito.times(0)).startActivity(Mockito.any());
    }
}