package ch.mse.quiz.firebase;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import junit.framework.TestCase;

import java.util.ArrayList;

import androidx.test.filters.RequiresDevice;
import ch.mse.quiz.models.question;

import static android.content.ContentValues.TAG;

@RunWith(RobolectricTestRunner.class)
public class FirebaseDBTest extends TestCase {

  DatabaseReference dbRef;
  FirebaseDatabase database = FirebaseDatabase.getInstance();

  @Override
  @Before
  public void setUp() throws Exception {
    super.setUp();
    // 10.0.2.2 is the special IP address to connect to the 'localhost' of
    // the host computer from an Android emulator.
    database.useEmulator("192.168.8.106", 9000);
  }

  @Test
  public void testGetTopics() {
    dbRef = database.getReference("topics");

    ArrayList<String> topics = new ArrayList<String>();

    dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
      @Override
      public void onDataChange(DataSnapshot dataSnapshot) {
        //Getting the string value of that node
        Iterable<DataSnapshot> children = dataSnapshot.getChildren();
        children.forEach(i -> {
          topics.add(i.getKey());
        });
        String[] topicSelection = topics.toArray(new String[topics.size()]);
      }
      @Override
      public void onCancelled(DatabaseError databaseError) {
        //TODO maybe change this to special test?
        assertTrue("Databseerror happened",false);
      }
    });
    assertTrue("List contains 3 topics",topics.size() == 3);
  }

  @Test
  public void testGetQuestions() {
    dbRef = database.getReference("topics/history/questions");
    // must be array because of anonymous inner class
    final int[] questionnr = new int[1];
    dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
      @Override
      public void onDataChange(DataSnapshot dataSnapshot) {
        //check if amount of question is h
        questionnr[0] = (int) dataSnapshot.getChildrenCount();
      }

      @Override
      public void onCancelled(DatabaseError databaseError) {
        //TODO maybe change this to special test?
        assertTrue("Databseerror happened",false);
      }
    });
    assertTrue("The result should be 10 questions", questionnr[0] == 10 );
  }






}
