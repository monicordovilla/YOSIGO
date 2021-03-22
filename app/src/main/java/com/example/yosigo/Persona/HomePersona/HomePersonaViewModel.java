package com.example.yosigo.Persona.HomePersona;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class HomePersonaViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public HomePersonaViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is home fragment from persona");
    }

    public LiveData<String> getText() {
        return mText;
    }
}