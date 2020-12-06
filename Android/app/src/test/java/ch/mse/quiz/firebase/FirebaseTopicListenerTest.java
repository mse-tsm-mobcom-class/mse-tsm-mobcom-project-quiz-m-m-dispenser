package ch.mse.quiz.firebase;

import android.util.Log;
import android.widget.NumberPicker;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.robolectric.RobolectricTestRunner;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import ch.mse.quiz.MainActivity;
import ch.mse.quiz.listeners.FirebaseTopicListener;

import static android.content.ContentValues.TAG;

@RunWith(RobolectricTestRunner.class)
@PrepareForTest(MainActivity.class)
public class FirebaseTopicListenerTest extends TestCase {

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    public NumberPicker numberPicker;

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
    }

    public FirebaseTopicListener firebaseTopicListener;

    @Test
    public void testGetAllTheTopics() {
        CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
            // Simulate a long-running Job
            try {
                firebaseTopicListener = new FirebaseTopicListener(numberPicker);
                TimeUnit.SECONDS.sleep(5);
            } catch (InterruptedException e) {
                throw new IllegalStateException(e);
            }
            System.out.println("I'll run in a separate thread than the main thread.");
        });

// Block and wait for the future to complete
        try {
            future.get();
        } catch (ExecutionException e) {
            Log.e(TAG, e.getLocalizedMessage());
        } catch (InterruptedException e) {
            Log.e(TAG, e.getLocalizedMessage());
        }



    }

}
