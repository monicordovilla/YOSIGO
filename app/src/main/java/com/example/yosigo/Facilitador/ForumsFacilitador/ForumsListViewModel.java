package com.example.yosigo.Facilitador.ForumsFacilitador;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ForumsListViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public ForumsListViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is slideshow fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}