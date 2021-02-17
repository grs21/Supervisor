package com.grs21.supervisor.adminFragment;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.grs21.supervisor.R;
import com.grs21.supervisor.databinding.FragmentServiceBinding;
import com.grs21.supervisor.model.Apartment;
import com.grs21.supervisor.model.Service;
import com.grs21.supervisor.util.CaptureAct;
import com.grs21.supervisor.util.ItemViewModel;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

import es.dmoral.toasty.Toasty;

public class ServiceFragment extends Fragment implements View.OnClickListener {

    private FragmentServiceBinding binding;
    private static final String TAG = "ServiceFragment";
    private AutoCompleteTextView autoComplete;
    private ArrayList<Apartment> apartmentArrayList=new ArrayList<>();
    private FirebaseFirestore firebaseFirestore;
    private FirebaseUser firebaseUser;
    private FirebaseAuth firebaseAuth;
    private String currentUserEmail;
    private Apartment apartment;
    private Service service=new Service();
    String currentDate;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding= FragmentServiceBinding.inflate(inflater,container,false);
        autoComplete=binding.autoComplete;
        binding.buttonServiceSave.setOnClickListener(this);
        binding.buttonServiceGetDate.setOnClickListener(this);
        currentUserEmail=FirebaseAuth.getInstance().getCurrentUser().getEmail();
        firebaseFirestore=FirebaseFirestore.getInstance();

        autoComplete();
        return binding.getRoot();
    }

    private void autoComplete() {
        ItemViewModel viewModel=new ViewModelProvider(getActivity()).get(ItemViewModel.class);
        viewModel.getSelectedItem().observe(getActivity(), item->{
            apartmentArrayList.addAll(item);
        });
        ArrayAdapter<Apartment> arrayAdapter=new ArrayAdapter<Apartment>(getContext()
                ,R.layout.item_autocomplate,R.id.autocomplate_name,apartmentArrayList);
        autoComplete.setAdapter(arrayAdapter);
        autoComplete.setThreshold(2);
        autoComplete.setDropDownVerticalOffset(7);
        autoComplete.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                apartment=(Apartment) parent.getItemAtPosition(position);
                binding.textViewServiceBuildName.setText(apartment.getApartmentName());
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.buttonServiceSave:
                Boolean well,elevatorUp,machineRoom;
                    well=binding.checkBoxServiceWell.isChecked();
                    elevatorUp=binding.checkBoxServiceFragmentElevatorUp.isChecked();
                    machineRoom=binding.checkBoxServiceMachineRoom.isChecked();

                if (well|| elevatorUp || machineRoom){
                    CheckBox DCWell,DCElevatorUp,DCMachineRoom;
                    Dialog dialog=new Dialog(getContext());
                    dialog.setContentView(R.layout.alert_dialog_service);

                    DCElevatorUp=dialog.findViewById(R.id.checkboxServiceDialogElevatorTop);
                    DCMachineRoom=dialog.findViewById(R.id.checkboxServiceDialogElevatorMachine);
                    DCWell=dialog.findViewById(R.id.checkboxServiceDialogWell);
                    DCWell.setChecked(well);
                    DCElevatorUp.setChecked(elevatorUp);
                    DCMachineRoom.setChecked(machineRoom);

                    TextView textViewName=dialog.findViewById(R.id.textViewServiceDialogName);
                    textViewName.setText(apartment.getApartmentName());

                    Button buttonCancel,buttonSave;
                    buttonCancel=dialog.findViewById(R.id.buttonServiceDialogCancel);
                    buttonSave=dialog.findViewById(R.id.buttonServiceDialogSave);
                    buttonCancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });
                    buttonSave.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                        }
                    });
                    dialog.show();
                    //Todo: Saying in AlertDialog error message not check checkBox
                    //todo: Show the which checkbox if not check
                    //todo: kaydedip kaydeticeğini sor
                    //todo: FirebaseFireStore da Service Arrayinin içine Service oluşturup kaydet

                }else{
                    Toast toastSuccess = Toasty.error(getActivity(), R.string.please_select_apartment
                            , Toast.LENGTH_LONG, true);
                    toastSuccess.setGravity(Gravity.CENTER, 0, 0);
                    toastSuccess.show();
                }
                break;
            case R.id.buttonServiceGetDate:
                Date date= Calendar.getInstance().getTime();
                currentDate= DateFormat.getDateInstance(DateFormat.DATE_FIELD).format(date);
                binding.EditTextServiceCurrentDate.setText(currentDate);
                break;
        }
    }
    private void scanCode() {
        IntentIntegrator integrator=new IntentIntegrator(getActivity() );
        integrator.setCaptureActivity(CaptureAct.class);
        integrator.setOrientationLocked(false);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
        integrator.setPrompt("Scanning Code");
        integrator.forSupportFragment(ServiceFragment.this).initiateScan();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result!=null){
            if (result.getContents()!=null) {

                try {
                    String well = apartment.getWell();
                    String up = apartment.getElevatorUp();
                    String machineRoom = apartment.getMachineRoom();
                    if (well.equals(result.getContents())) {
                        service.setWell(well);
                        binding.checkBoxServiceWell.setChecked(true);
                    } else if (up.equals(result.getContents())) {
                        service.setElevatorUp(up);
                        binding.checkBoxServiceFragmentElevatorUp.setChecked(true);
                    } else if (machineRoom.equals(result.getContents())) {
                        service.setMachineRoom(machineRoom);
                        binding.checkBoxServiceMachineRoom.setChecked(true);
                    } else {
                        Toast toastSuccess = Toasty.warning(requireActivity(),R.string.not_match
                                , Toast.LENGTH_LONG, true);
                        toastSuccess.setGravity(Gravity.CENTER, 0, 0);
                        toastSuccess.show();
                    }
                } catch (Exception e) {
                    Toast toastSuccess = Toasty.error(requireActivity(), R.string.please_select_apartment
                            , Toast.LENGTH_LONG, true);
                    toastSuccess.setGravity(Gravity.CENTER, 0, 0);
                    toastSuccess.show();
                }
            }
        }else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
