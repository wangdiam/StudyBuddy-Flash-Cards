package com.wangdiam.studybuddycapstoneproject.repository;

import android.app.Application;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.wangdiam.studybuddycapstoneproject.models.Card;
import com.wangdiam.studybuddycapstoneproject.models.Subject;
import com.wangdiam.studybuddycapstoneproject.utils.FirebaseQueryLiveData;

import java.util.ArrayList;


public class CardsRepository {
    FirebaseDatabase db = FirebaseDatabase.getInstance();
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    DatabaseReference reference = db.getReference();
    Application application;
    MutableLiveData<ArrayList<Card>> cards = new MutableLiveData<>();

    public CardsRepository(Application application) {
        this.application = application;

    }
    public MutableLiveData<DataSnapshot> getDisplayName() {
        DatabaseReference ref = reference.child("users")
                .child(user.getUid())
                .child("displayName");
        FirebaseQueryLiveData firebaseQueryLiveData = new FirebaseQueryLiveData(ref);
        return firebaseQueryLiveData;
        /*final String[] name = new String[1];
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                name[0] = dataSnapshot.getValue().toString();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        MutableLiveData<String> stringMutableLiveData = new MutableLiveData<>();
        stringMutableLiveData.setValue(name[0]);
        return stringMutableLiveData;*/
    }

    public MutableLiveData<String> getEmail() {
        DatabaseReference ref = reference.child("users")
                .child(user.getUid())
                .child("Email");
        final String[] email = new String[1];
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                email[0] = dataSnapshot.getValue().toString();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        MutableLiveData<String> stringMutableLiveData = new MutableLiveData<>();
        stringMutableLiveData.setValue(email[0]);
        return stringMutableLiveData;
    }
    public void insertCard(Card card) {
        DatabaseReference dbRef = reference.child("users")
                .child(user.getUid())
                .child("subjects")
                .child(card.getSubjectId().toString())
                .child(card.getId().toString());
        final DatabaseReference cardCountRef = reference.child("users")
                .child(user.getUid())
                .child("subjects")
                .child(card.getSubjectId().toString());
        cardCountRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Long newCardCount = dataSnapshot.getChildrenCount();
                cardCountRef.child("cardCount").setValue(newCardCount-2);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        dbRef.setValue(card);

    }

    public void updateCard(Card card) {
        reference.child("users")
                .child(user.getUid())
                .child("subjects")
                .child(card.getSubjectId().toString())
                .child(card.getId().toString())
                .setValue(card);
    }

    public void updateCardSubjectName(Card card, Subject subject) {
        reference.child("users")
                .child(user.getUid())
                .child("subjects")
                .child(card.getSubjectId().toString())
                .child(card.getId().toString())
                .child("subjectName")
                .setValue(subject.getName());
    }

    public void deleteCard(Card card) {
        reference.child("users")
                .child(user.getUid())
                .child("subjects")
                .child(card.getSubjectId().toString())
                .child(card.getId().toString())
                .removeValue();
        final DatabaseReference cardCountRef = reference.child("users")
                .child(user.getUid())
                .child("subjects")
                .child(card.getSubjectId().toString());
        cardCountRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Long newCardCount = dataSnapshot.getChildrenCount();
                cardCountRef.child("cardCount").setValue(newCardCount-3);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    public MutableLiveData<DataSnapshot> getAllCardsWithSubjectLiveData(Subject subject) {
        DatabaseReference allCardsRef = reference.child("users")
                .child(user.getUid())
                .child("subjects")
                .child(subject.getId().toString());
        FirebaseQueryLiveData liveData = new FirebaseQueryLiveData(allCardsRef);
        return liveData;
    }
    public MutableLiveData<DataSnapshot> getAllCardsLiveData() {
        DatabaseReference allCardRef = reference.child("users")
                .child(user.getUid())
                .child("subjects");
        FirebaseQueryLiveData liveData = new FirebaseQueryLiveData(allCardRef);
        return liveData;
    }

    public MutableLiveData<DataSnapshot> getCardWithID(Long id, Long subjectid) {
        final DatabaseReference cardRef = reference.child("users")
                .child(user.getUid())
                .child("subjects")
                .child(subjectid.toString())
                .child(id.toString());
        FirebaseQueryLiveData liveData = new FirebaseQueryLiveData(cardRef);
        return liveData;
       /* final MutableLiveData<DataSnapshot>[] card = new MutableLiveData[1];
        cardRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                card[0] = dataSnapshot.getValue(Card.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        return card[0];*/
    }


}
