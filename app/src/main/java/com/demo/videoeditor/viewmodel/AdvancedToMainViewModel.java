package com.demo.videoeditor.viewmodel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

public class AdvancedToMainViewModel extends ViewModel {
    private final MutableLiveData<Integer> selected = new MutableLiveData<>();

    public void select(Integer item) {
        selected.setValue(item);
    }

    public LiveData<Integer> getSelected() {
        return selected;
    }
}
