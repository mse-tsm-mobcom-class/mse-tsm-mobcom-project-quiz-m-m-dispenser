package ch.mse.quiz.listeners;

import android.os.Build;
import android.widget.NumberPicker;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.stubbing.OngoingStubbing;
import org.powermock.api.mockito.PowerMockito;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.ArrayList;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.rule.ActivityTestRule;
import ch.mse.quiz.QuestionActivity;
import ch.mse.quiz.models.question;

import static androidx.lifecycle.Lifecycle.State.CREATED;
import static androidx.lifecycle.Lifecycle.State.INITIALIZED;
import static androidx.test.espresso.intent.Intents.times;
import static org.mockito.Mockito.verify;


@RunWith(RobolectricTestRunner.class)
@Config(sdk = {Build.VERSION_CODES.O_MR1})
public class FirebaseQuestionListenerTest extends TestCase {

    private FirebaseQuestionListener firebaseQuestionListener;
    int questionNumber = 1;
    ArrayList<question> questions;
    ArrayList<DataSnapshot> data;

    @Mock
    QuestionActivity questionActivity;
    @Mock
    question exquestion;

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
        questionActivity = PowerMockito.mock(QuestionActivity.class);
        //exquestion = PowerMockito.mock(question.class);
        questions = new ArrayList<question>();
        data = new ArrayList<DataSnapshot>();
        firebaseQuestionListener = new FirebaseQuestionListener(questions, questionNumber, questionActivity);
    }

    @Test
    public void onDataChange() {
        // do nothing when callback called
        PowerMockito.doNothing().when(questionActivity).createQuestion(1);

        // mock datasnapshots and add 2 times
        DataSnapshot d1 = PowerMockito.mock(DataSnapshot.class);
        PowerMockito.when(d1.getValue()).thenReturn(String.valueOf(exquestion));
        data.add(d1);
        data.add(d1);

        // mock datasnapshot
        DataSnapshot dataSnapshot = PowerMockito.mock(DataSnapshot.class);
        PowerMockito.when(dataSnapshot.getChildren()).thenReturn(data);

        firebaseQuestionListener.onDataChange(dataSnapshot);
        // should contain elements
        assertTrue(0 < questions.size());
        //should contain 2 elements
        assertTrue(questions.size() == 2);
        //should call callback method
        verify(questionActivity).createQuestion(1);
    }

    @Test
    public void onCancelled() {
        firebaseQuestionListener.onCancelled(PowerMockito.mock(DatabaseError.class));
        assertTrue(true);
    }
}
