package com.example.myaplicationrecuperar.ui.estatistica;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class EstatisticaViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public EstatisticaViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is slideshow fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}