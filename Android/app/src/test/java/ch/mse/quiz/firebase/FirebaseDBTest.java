package ch.mse.quiz.firebase;

import android.content.Context;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.rule.PowerMockRule;
import org.robolectric.RobolectricTestRunner;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import ch.mse.quiz.MainActivity;


@RunWith(RobolectricTestRunner.class)
@PowerMockIgnore({"org.mockito.*", "org.robolectric.*", "android.*"})
@PrepareForTest(FirebaseDatabase.class)
public class FirebaseDBTest extends TestCase {

    @Rule
    public PowerMockRule rule = new PowerMockRule();

    @Mock
    public MainActivity mainActivity;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference dbRef;


    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
        firebaseDatabase = PowerMockito.mock(FirebaseDatabase.class);
        dbRef = PowerMockito.mock(DatabaseReference.class);

        Context context = PowerMockito.mock(Context.class);

        PowerMockito.mockStatic(FirebaseDatabase.class);
        Mockito.when(FirebaseDatabase.getInstance()).thenReturn(firebaseDatabase);
        Mockito.when(firebaseDatabase.getReference(Mockito.anyString())).thenReturn(dbRef);


        //FirebaseApp.initializeApp(mainActivity.getBaseContext());
        //database = FirebaseDatabase.getInstance();
        // 10.0.2.2 is the special IP address to connect to the 'localhost' of
        // the host computer from an Android emulator.
        //mainActivity.database.useEmulator("192.168.8.106", 9000);
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
