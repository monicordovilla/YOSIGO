package com.example.yosigo.Persona.ActivitiesPersona;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ListActivitiesViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public ListActivitiesViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is gallery fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}