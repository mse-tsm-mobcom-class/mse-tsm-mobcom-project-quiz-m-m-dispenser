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
public class FirebaseValueEventListenerTest extends TestCase {

    private NumberPicker numberPicker;

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
        numberPicker = PowerMockito.mock(NumberPicker.class);
    }

    @Test
    public void onDataChange() {
        ArrayList<String> topics = new ArrayList<>();
        FirebaseValueEventListener firebaseValueEventListener = new FirebaseValueEventListener(topics, numberPicker);
        DataSnapshot dataSnapshot = PowerMockito.mock(DataSnapshot.class);
        ArrayList<DataSnapshot> data = new ArrayList<>();
        DataSnapshot d1 = PowerMockito.mock(DataSnapshot.class);
        PowerMockito.when(d1.getKey()).thenReturn("Test Topic");
        data.add(d1);
        Iterable<DataSnapshot> dataSnapshots = data;
        PowerMockito.when(dataSnapshot.getChildren()).thenReturn(dataSnapshots);
        firebaseValueEventListener.onDataChange(dataSnapshot);
        assertTrue(0 < topics.size());
    }

    @Test
    public void onCancelled() {
        ArrayList<String> topics = new ArrayList<>();
        FirebaseValueEventListener firebaseValueEventListener = new FirebaseValueEventListener(topics, numberPicker);
        firebaseValueEventListener.onCancelled(PowerMockito.mock(DatabaseError.class));
        assertTrue(true);
    }
}