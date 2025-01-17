// Copyright (c) 2020, Steiner Pascal, Strässle Nikolai, Radinger Martin
// All rights reserved.

// Licensed under LICENSE, see LICENSE file

package ch.mse.quiz.listeners;

import android.os.Build;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.api.mockito.PowerMockito;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.ArrayList;

import ch.mse.quiz.QuizResultActivity;
import ch.mse.quiz.models.UserScore;

import static org.mockito.Mockito.verify;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = {Build.VERSION_CODES.O_MR1})
public class FireBaseScoreListenerTest extends TestCase {

    private FirebaseScoreListener firebaseScoreListener;
    public UserScore currentPlayer;
    ArrayList<UserScore> playerScores;
    ArrayList<DataSnapshot> data;

    @Mock
    QuizResultActivity quizResultActivity;
    @Mock
    UserScore userScore;

    @Override
    @Before
    public void setUp() throws Exception {

        super.setUp();
        quizResultActivity = PowerMockito.mock(QuizResultActivity.class);
        //exquestion = PowerMockito.mock(question.class);
        playerScores = new ArrayList<>();
        data = new ArrayList<>();
        firebaseScoreListener = new FirebaseScoreListener(playerScores, currentPlayer, quizResultActivity);
    }

    @Test
    public void onDataChange() {
        // do nothing when callback called
        PowerMockito.doNothing().when(quizResultActivity).updateList();

        // mock datasnapshots and add 2 times
        DataSnapshot d1 = PowerMockito.mock(DataSnapshot.class);
        PowerMockito.when(d1.getValue()).thenReturn(userScore);
        data.add(d1);
        data.add(d1);

        // mock datasnapshot
        DataSnapshot dataSnapshot = PowerMockito.mock(DataSnapshot.class);
        PowerMockito.when(dataSnapshot.getChildren()).thenReturn(data);

        firebaseScoreListener.onDataChange(dataSnapshot);
        // should contain elements
        assertTrue(0 < playerScores.size());
        //should contain 2 elements
        assertEquals(3, playerScores.size());
        //should call callback method
        verify(quizResultActivity).updateList();
    }

    @Test
    public void onCancelled() {
        firebaseScoreListener.onCancelled(PowerMockito.mock(DatabaseError.class));
        assertTrue(true);
    }
}

