// Copyright (c) 2020, Steiner Pascal, Strässle Nikolai, Radinger Martin
// All rights reserved.

// Licensed under LICENSE, see LICENSE file

package ch.mse.quiz.listeners;

import android.os.Build;
import android.widget.NumberPicker;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.ArrayList;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = {Build.VERSION_CODES.O_MR1})
public class FirebaseTopicListenerTest extends TestCase {
    private FirebaseTopicListener firebaseTopicListener;
    private NumberPicker numberPicker;
    private ArrayList<DataSnapshot> data;

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
        data = new ArrayList<>();
        numberPicker = PowerMockito.mock(NumberPicker.class);
        firebaseTopicListener = new FirebaseTopicListener(numberPicker);
    }

    @Test
    public void onDataChange() {
        // mock datasnapshots and add 2 times
        DataSnapshot dataSnapshot = PowerMockito.mock(DataSnapshot.class);
        DataSnapshot d1 = PowerMockito.mock(DataSnapshot.class);
        DataSnapshot d2 = PowerMockito.mock(DataSnapshot.class);

        // mock data and add 2x
        PowerMockito.when(d1.getKey()).thenReturn("First topic");
        PowerMockito.when(d2.getKey()).thenReturn("Second topic");
        data.add(d1);
        data.add(d2);

        // mock datasnapshot
        PowerMockito.when(dataSnapshot.getChildren()).thenReturn(data);
        firebaseTopicListener.onDataChange(dataSnapshot);
        // should contain elements
        assertTrue(0 < firebaseTopicListener.getTopics().size());
        //should contain 2 elements
        assertEquals(2, firebaseTopicListener.getTopics().size());
    }

    @Test
    public void onCancelled() {
        FirebaseTopicListener firebaseTopicListener = new FirebaseTopicListener(numberPicker);
        firebaseTopicListener.onCancelled(PowerMockito.mock(DatabaseError.class));
        assertTrue(true);
    }
}