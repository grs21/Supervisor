package com.grs21.supervisor.util;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.grs21.supervisor.model.Apartment;

import java.util.ArrayList;

public class ItemViewModel extends ViewModel {
    private final MutableLiveData<ArrayList<Apartment>> selectedItem = new MutableLiveData();
    public void selectItem(ArrayList<Apartment> item) {
        selectedItem.setValue(item);
    }

    public LiveData<ArrayList<Apartment>> getSelectedItem() {
        return selectedItem;
    }
}
