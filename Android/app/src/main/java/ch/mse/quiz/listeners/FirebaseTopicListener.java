package ch.mse.quiz.listeners;

import android.util.Log;
import android.widget.NumberPicker;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import static android.content.ContentValues.TAG;

public class FirebaseTopicListener implements ValueEventListener {

    private final ArrayList<String> topics = new ArrayList<>();
    private final NumberPicker npTopic;

    public FirebaseTopicListener(NumberPicker npTopic) {
        this.npTopic = npTopic;
    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        //Getting the string value of that node
        Iterable<DataSnapshot> children = dataSnapshot.getChildren();
        children.forEach(i -> {
            topics.add(i.getKey());
        });
        String[] topicSelection = topics.toArray(new String[topics.size()]);
        npTopic.setDisplayedValues(null);
        //which topic?
        npTopic.setMinValue(1);
        npTopic.setMaxValue(topics.size());
        npTopic.setDisplayedValues(topicSelection);
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {
        Log.e(TAG, "onCancelled: Something went wrong! Error:" + databaseError.getMessage());
    }

    public ArrayList<String> getTopics() {
        return topics;
    }
}
