package com.wangdiam.studybuddycapstoneproject.viewmodels;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.wangdiam.studybuddycapstoneproject.models.Subject;
import com.wangdiam.studybuddycapstoneproject.repository.SubjectRepository;

import java.util.ArrayList;

public class SubjectViewModel extends AndroidViewModel {
    SubjectRepository repo;
    public SubjectViewModel(@NonNull Application application) {
        super(application);
        repo = new SubjectRepository(application);
    }

    public void insertSubject(Subject subject) {
        repo.insertSubject(subject);
    }
    public void updateSubject(Subject subject) { repo.updateSubject(subject); }
    public void deleteSubject(Subject subject) { repo.deleteSubject(subject); }
    public MutableLiveData<DataSnapshot> getUpdatedSubjectNameWithId(Long id) { return repo.getUpdatedSubjectNameWithId(id); }
    public MutableLiveData<DataSnapshot> getAllSubjectsLiveData() { return repo.getAllSubjectsLiveData(); }


}
