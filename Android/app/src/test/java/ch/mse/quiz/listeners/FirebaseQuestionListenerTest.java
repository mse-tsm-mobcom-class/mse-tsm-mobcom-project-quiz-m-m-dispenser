package ch.mse.quiz.listeners;

import android.os.Build;
import android.util.Log;

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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;

import ch.mse.quiz.QuestionActivity;
import ch.mse.quiz.models.Question;

import static org.mockito.Mockito.verify;


@RunWith(RobolectricTestRunner.class)
@Config(sdk = {Build.VERSION_CODES.O_MR1})
public class FirebaseQuestionListenerTest extends TestCase {

    public FirebaseQuestionListener firebaseQuestionListener;
    int questionNumber = 1;
    ArrayList<Question> questions;
    ArrayList<DataSnapshot> data;

    @Mock
    QuestionActivity questionActivity;
    @Mock
    Question exquestion;

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
        questionActivity = PowerMockito.mock(QuestionActivity.class);
        //exquestion = PowerMockito.mock(question.class);
        questions = new ArrayList<>();
        data = new ArrayList<>();
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
        assertEquals(2, questions.size());
        //should call callback method
        verify(questionActivity).createQuestion(1);
    }

    @Test
    public void onCancelled() {
        firebaseQuestionListener.onCancelled(PowerMockito.mock(DatabaseError.class));
        assertTrue(true);
    }

    @Test
    public void testGetRandomQuestions() {
        // init parameters
        questionNumber = 4;
        for(int i = 0; i <=10 ; i++) {
            questions.add(PowerMockito.mock(Question.class));
        }
        // take private method and make accessible for the test
        try {
            Method privateMethod = firebaseQuestionListener.getClass().getDeclaredMethod("getRandomQuestions", ArrayList.class,int.class);
            privateMethod.setAccessible(true);
            ArrayList<Question> arrList = (ArrayList<Question>) privateMethod.invoke(firebaseQuestionListener,questions,questionNumber);

            //should return a questionNumber size array
            assertEquals(arrList.size(), questionNumber);

        } catch (NoSuchMethodException e) {
            e.printStackTrace();
            Log.e("Private testmethod not found","exceptions"+e);
        } catch (IllegalAccessException | InvocationTargetException e){
            e.printStackTrace();
            Log.e("Can not access private method","exceptions"+e);
        }
    }

    @Test
    public void getRandomNumber() {
        int lowerbound = 0;
        int upperbound = 10;
        try {
            Method privateMethod = firebaseQuestionListener.getClass().getDeclaredMethod("getRandomNumber", int.class, int.class);
            privateMethod.setAccessible(true);
            int rndNumber = (int) privateMethod.invoke(firebaseQuestionListener,lowerbound,upperbound);

            //should return a questionNumber size array
            assertTrue(rndNumber <= upperbound );
            assertTrue(rndNumber >= lowerbound);

        } catch (NoSuchMethodException e) {
            e.printStackTrace();
            Log.e("Private testmethod not found","exceptions"+e);
        } catch (IllegalAccessException | InvocationTargetException e){
            e.printStackTrace();
            Log.e("Can not access private method","exceptions"+e);
        }

    }
}
