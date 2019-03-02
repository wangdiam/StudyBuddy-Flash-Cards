package com.wangdiam.studybuddycapstoneproject.repository;

import android.app.Application;
import android.arch.lifecycle.MutableLiveData;
import android.service.autofill.Dataset;
import android.support.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.wangdiam.studybuddycapstoneproject.models.Subject;
import com.wangdiam.studybuddycapstoneproject.utils.FirebaseQueryLiveData;

import java.util.ArrayList;

public class SubjectRepository {
    Application application;
    FirebaseDatabase db = FirebaseDatabase.getInstance();
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    DatabaseReference reference = db.getReference();

    public SubjectRepository(Application application) {
        this.application = application;
    }

    public void insertSubject(final Subject subject) {
        if (subject.getId() == null) {
            Long currentTime = System.currentTimeMillis();
            subject.setId(currentTime);
            subject.setCardCount((long) 0);
        }
        final DatabaseReference databaseReference = reference.child("users")
                .child(user.getUid())
                .child("subjects")
                .child(subject.getId().toString());
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                databaseReference.setValue(subject);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    public void updateSubject(final Subject subject) {
        final DatabaseReference databaseReference = reference.child("users")
                .child(user.getUid())
                .child("subjects")
                .child(subject.getId().toString())
                .child("name");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.hasChild(subject.getId().toString())) {
                    databaseReference.setValue(subject.getName());
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void deleteSubject(Subject subject) {
        reference.child("users")
                .child(user.getUid())
                .child("subjects")
                .child(subject.getId().toString())
                .removeValue();
    }

    public MutableLiveData<DataSnapshot> getUpdatedSubjectNameWithId(Long id) {
        DatabaseReference ref = reference.child("users")
                .child(user.getUid())
                .child("subjects")
                .child(id.toString())
                .child("name");
        FirebaseQueryLiveData queryLiveData = new FirebaseQueryLiveData(ref);
        return queryLiveData;
    }
    public MutableLiveData<DataSnapshot> getAllSubjectsLiveData() {
        DatabaseReference ref = reference.child("users")
                .child(user.getUid())
                .child("subjects");
        FirebaseQueryLiveData queryLiveData = new FirebaseQueryLiveData(ref);

        return queryLiveData;
    }


}
