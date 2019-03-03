package com.wangdiam.studybuddycapstoneproject.application;

import com.google.android.gms.ads.MobileAds;
import com.google.firebase.database.FirebaseDatabase;

public class StudyBuddyApp extends android.app.Application {
    @Override
    public void onCreate() {
        super.onCreate();
        MobileAds.initialize(this,"ca-app-pub-8718775070617099~3804253495");
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        FirebaseDatabase.getInstance().getReference().keepSynced(true);
    }
}
