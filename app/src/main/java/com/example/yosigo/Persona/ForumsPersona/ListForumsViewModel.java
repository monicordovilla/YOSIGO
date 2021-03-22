package com.example.yosigo.Persona.ForumsPersona;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ListForumsViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public ListForumsViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is slideshow fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}