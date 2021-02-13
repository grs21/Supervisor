package com.grs21.supervisor.adminFragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.grs21.supervisor.R;
import com.grs21.supervisor.databinding.FragmentRepairBinding;
import com.grs21.supervisor.databinding.FragmentServiceBinding;
import com.grs21.supervisor.model.Apartment;
import com.grs21.supervisor.util.ItemViewModel;

import java.util.ArrayList;

public class ServiceFragment extends Fragment {

    private FragmentServiceBinding binding;
    private static final String TAG = "RepairFragment";
    private AutoCompleteTextView autoComplete;
    private ArrayList<Apartment> apartmentArrayList=new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding= FragmentServiceBinding.inflate(inflater,container,false);
        autoComplete=binding.autoComplete;
        apartmentArrayList.clear();
        ItemViewModel viewModel=new ViewModelProvider(getActivity()).get(ItemViewModel.class);
        viewModel.getSelectedItem().observe(getActivity(), item->{

            apartmentArrayList.addAll(item);
        });
        ArrayAdapter<Apartment> arrayAdapter=new ArrayAdapter<Apartment>(getContext()
                ,R.layout.item_autocomplate,R.id.autocomplate_name,apartmentArrayList);
        autoComplete.setAdapter(arrayAdapter);
        autoComplete();
        return binding.getRoot();
    }

    private void autoComplete() {
        autoComplete.setThreshold(2);
        autoComplete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
    }
}
