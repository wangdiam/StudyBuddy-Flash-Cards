package com.wangdiam.studybuddycapstoneproject.application;

import com.google.firebase.database.FirebaseDatabase;

public class StudyBuddyApp extends android.app.Application {
    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        FirebaseDatabase.getInstance().getReference().keepSynced(true);
    }
}
