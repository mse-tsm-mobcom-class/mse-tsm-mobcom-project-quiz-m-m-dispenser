package ch.mse.quiz.firebase;

import android.content.Context;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.robolectric.RobolectricTestRunner;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;
import ch.mse.quiz.MainActivity;

import static org.mockito.Mockito.mock;


@RunWith(RobolectricTestRunner.class)
@PrepareForTest(MainActivity.class)
public class FirebaseDBTest extends TestCase {

    @Mock
    public MainActivity mainActivity;
    FirebaseDatabase firebaseDatabase;
    @Mock
    DatabaseReference dbRef;


    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
        //firebaseDatabase = PowerMockito.mock(FirebaseDatabase.class);
        //dbRef = PowerMockito.mock(DatabaseReference.class);

        //Context context = PowerMockito.mock(Context.class);
        Context context = mock(MainActivity.class);

        //PowerMockito.mockStatic(FirebaseDatabase.class);
        //Mockito.when(FirebaseDatabase.getInstance()).thenReturn(firebaseDatabase);
        // 10.0.2.2 is the special IP address to connect to the 'localhost' of
        // the host computer from an Android emulator.
        //mainActivity.database.useEmulator("192.168.8.106", 9000);
        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseDatabase.useEmulator("10.0.2.2", 9000);
    }

    @Test
    public void testGetQuestions() throws ExecutionException, InterruptedException {
        CompletableFuture<Void> future = CompletableFuture.runAsync(new Runnable() {
            @Override
            public void run() {
                // Simulate a long-running Job
                try {
                    mainActivity.getTopics();
                    TimeUnit.SECONDS.sleep(5);
                } catch (InterruptedException e) {
                    throw new IllegalStateException(e);
                }
                System.out.println("I'll run in a separate thread than the main thread.");
            }
        });

// Block and wait for the future to complete
        future.get();

    }


}
