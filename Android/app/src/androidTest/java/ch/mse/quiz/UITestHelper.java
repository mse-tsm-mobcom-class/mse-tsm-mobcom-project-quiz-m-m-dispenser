// Copyright (c) 2020, Steiner Pascal, Strässle Nikolai, Radinger Martin
// All rights reserved.

// Licensed under LICENSE, see LICENSE file

package ch.mse.quiz;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.test.espresso.IdlingRegistry;
import androidx.test.espresso.idling.CountingIdlingResource;
import androidx.test.platform.app.InstrumentationRegistry;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;

import org.junit.Before;

import static androidx.test.espresso.Espresso.onIdle;
import static org.junit.Assert.fail;

public class UITestHelper implements OnCompleteListener {

    private static final String IDLING_NAME = "cl.cutiko.espresofirebase.FireBaseTest.key.IDLING_NAME";
    private static final CountingIdlingResource idlingResource = new CountingIdlingResource(IDLING_NAME);

    // Based on https://github.com/cutiko/espressofirebase
    @Before
    public void prepare() {
        final Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        int apps = FirebaseApp.getApps(context).size();
        if (apps == 0) {
            FirebaseOptions options = new FirebaseOptions.Builder()
                    .setApiKey(BuildConfig.apiKey)
                    .setApplicationId(BuildConfig.applicationId)
                    .setDatabaseUrl(BuildConfig.databaseUrl)
                    .setProjectId(BuildConfig.projectId)
                    .build();
            FirebaseApp.initializeApp(context, options);
        }
        IdlingRegistry.getInstance().register(idlingResource);
        FirebaseAuth.getInstance()
                .signInWithEmailAndPassword("hans@test.com", "test123")
                .addOnCompleteListener(this);
        idlingResource.increment();
        onIdle();
    }

    @Override
    public void onComplete(@NonNull Task task) {
        if (task.isSuccessful()) {
            idlingResource.decrement();
        } else {
            fail("The user was not logged in successfully");
        }
    }
}
