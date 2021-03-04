package com.grs21.supervisor.adminFragment;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.MultiAutoCompleteTextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;

import com.grs21.supervisor.R;
import com.grs21.supervisor.databinding.FragmentRepairBinding;
import com.grs21.supervisor.model.Apartment;
import com.grs21.supervisor.util.ItemViewModel;

import java.util.ArrayList;

public class RepairFragment extends Fragment implements View.OnClickListener {

    private FragmentRepairBinding binding;
    private static final String TAG = "RepairFragment";


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding=FragmentRepairBinding.inflate(inflater,container,false);

        binding.imageButtonRepairAdd.setOnClickListener(this);


        return binding.getRoot();
    }


    @Override
    public void onClick(View v) {



    }
}


