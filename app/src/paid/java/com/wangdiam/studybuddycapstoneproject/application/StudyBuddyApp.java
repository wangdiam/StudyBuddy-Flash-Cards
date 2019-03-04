package com.wangdiam.studybuddycapstoneproject.application;

import com.google.firebase.database.FirebaseDatabase;

public class StudyBuddyApp extends android.app.Application {
    public static final String REMINDER_JOB_SERVICE = "REMINDER_JOB_SERVICE";

    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        FirebaseDatabase.getInstance().getReference().keepSynced(true);
    }
}
