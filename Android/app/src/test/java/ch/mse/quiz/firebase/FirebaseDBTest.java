package ch.mse.quiz.firebase;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.ktx.Firebase;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.modules.junit4.PowerMockRunner;
import org.robolectric.RobolectricTestRunner;

import java.sql.DriverManager;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import ch.mse.quiz.MainActivity;


@RunWith(PowerMockRunner.class)
public class FirebaseDBTest extends TestCase {

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    @Mock
    public MainActivity mainActivity;

    @Mock
    FirebaseApp firebaseApp;
    @Mock
    FirebaseDatabase firebaseDatabase;
    @Mock
    DatabaseReference dbRef;


    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
        PowerMockito.mockStatic(FirebaseApp.class);
        BDDMockito.given(FirebaseApp.getInstance()).willReturn(firebaseApp);

        PowerMockito.mockStatic(FirebaseDatabase.class);
        BDDMockito.given(FirebaseDatabase.getInstance()).willReturn(firebaseDatabase);
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
