package com.wangdiam.studybuddycapstoneproject.viewmodels;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.wangdiam.studybuddycapstoneproject.models.Card;
import com.wangdiam.studybuddycapstoneproject.models.Subject;
import com.wangdiam.studybuddycapstoneproject.repository.CardsRepository;

public class CardViewModel extends AndroidViewModel {
    CardsRepository repo;
    MutableLiveData<DataSnapshot> displayName;
    MutableLiveData<String> email;
    public CardViewModel(@NonNull Application application) {
        super(application);
        repo = new CardsRepository(application);
    }
    public MutableLiveData<DataSnapshot> getDisplayName() {
        displayName = repo.getDisplayName();
        return displayName;
    }
    public MutableLiveData<String> getEmail() {
        email = repo.getEmail();
        return email;
    }
    public void insertCard(Card card) {repo.insertCard(card);}
    public void updateCard(Card card) {repo.updateCard(card);}
    public void updateCardSubjectName(Card card, Subject subject) { repo.updateCardSubjectName(card,subject );}
    public void deleteCard(Card card) {repo.deleteCard(card);}
    public MutableLiveData<DataSnapshot> getCardWithID(Long id, Long subjectid) {return repo.getCardWithID(id,subjectid);}
    public MutableLiveData<DataSnapshot> getAllCardsLiveData() { return repo.getAllCardsLiveData(); }
    public MutableLiveData<DataSnapshot> getAllCardsForSubjectLiveData(Subject subject) {return repo.getAllCardsWithSubjectLiveData(subject);}
}
