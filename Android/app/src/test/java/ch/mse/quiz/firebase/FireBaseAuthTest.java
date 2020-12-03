package ch.mse.quiz.firebase;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import junit.framework.TestCase;

import java.util.concurrent.Executor;

import androidx.annotation.NonNull;


@RunWith(RobolectricTestRunner.class)
public class FireBaseAuthTest extends TestCase {

    // Initialize Firebase Auth
    FirebaseAuth mAuth = FirebaseAuth.getInstance();

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
        // 10.0.2.2 is the special IP address to connect to the 'localhost' of
        // the host computer from an Android emulator.
        //TODO check if it works with 127.0.0.1
        FirebaseAuth.getInstance().useEmulator("192.168.8.106", 9099);
    }

    @Test
    public void onCreateMainActivityTest() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        assertTrue("there is no user logged in",currentUser == null);
    }
    @Test
    public void testCreateAccount() {
        /*** should create account**/
        String email = "testuser99@gmail.com";
        String password = "testuser99";
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener((Executor) this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = mAuth.getCurrentUser();
                            assertNotNull(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            assertTrue("Creating a new account failed",false);
                        }
                    }
                });
    }

}
