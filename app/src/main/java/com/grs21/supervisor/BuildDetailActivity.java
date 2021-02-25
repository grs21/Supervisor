package com.grs21.supervisor;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.grs21.supervisor.activity.ServiceActivity;
import com.grs21.supervisor.databinding.ActivityBuildDetailBinding;
import com.grs21.supervisor.model.Apartment;
import com.grs21.supervisor.model.Service;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

public class BuildDetailActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener{
    private ActivityBuildDetailBinding binding;
    private Intent intent;
    private Apartment apartment;
    private ArrayList<Service> serviceArrayList=new ArrayList<>();
    private Dialog dialog;
    private TextView textViewDialogDate,textViewDialogEmployee;
    private CheckBox checkBoxWell,checkBoxUp,checkBoxMachineRoom;
    private static final String TAG = "BuildDetailActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityBuildDetailBinding.inflate(getLayoutInflater());
        View view=binding.getRoot();
        setContentView(view);
        binding.buttonDetailToEdit.setOnClickListener(this);
        binding.spinnerDate.setOnItemSelectedListener(this);
        binding.buttonMakeService.setOnClickListener(this);


        intent=getIntent();
        apartment=(Apartment) intent.getSerializableExtra("apartment");

        Toolbar toolbar=findViewById(R.id.toolbarBuildDetail);
        toolbar.setTitle(apartment.getApartmentName());

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(apartment.getApartmentName());
        initializeData(apartment);

        for (HashMap service:apartment.getServiceArrayList()) {
            Service generateService=new Service((boolean)service.get("well"),(boolean)service.get("elevatorUp")
                    ,(boolean)service.get("machineRoom"),(String) service.get("date")
                    ,(String) service.get("employee"));
            serviceArrayList.add(generateService);
        }

    }

    private void initializeData(Apartment apartment) {
        ArrayAdapter<Service> spinnerAdapter=new ArrayAdapter<Service>(getApplicationContext()
                , android.R.layout.simple_spinner_item,serviceArrayList);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerDate.setAdapter(spinnerAdapter);


        binding.textViewBuildDetailBuildName.setText(apartment.getApartmentName());
        binding.textViewBuildDetailBuildAddress.setText(apartment.getApartmentAddress());
        binding.textViewBuildDetailCost.setText(apartment.getCost());
        binding.textViewBuildDetailManagerName.setText(apartment.getManagerName());
        binding.textViewBuildDetailManagerNumber.setText(apartment.getManagerNumber());
        binding.textViewBuildDetailManagerAddress.setText(apartment.getManagerAddress());
        binding.textViewBuildDetailEmployeeName.setText(apartment.getEmployeeName());
        binding.textViewBuildDetailEmployeeNumber.setText(apartment.getEmployeeNumber());
        binding.textViewBuildDetailContractDate.setText(apartment.getContractDate());
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){

            case R.id.buttonDetailToEdit:
                Intent intent=new Intent(BuildDetailActivity.this,ApartmentEditActivity.class);
                intent.putExtra("apartment", (Serializable) apartment);
                startActivity(intent);
                break;
            case R.id.buttonDetailDialogCancel:
                dialog.dismiss();
                break;
            case R.id.buttonMakeService:
                Intent intent1=new Intent(BuildDetailActivity.this, ServiceActivity.class);
                intent1.putExtra("apartment", apartment);
                startActivity(intent1);
                break;
        }

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            Service spinnerService=(Service) parent.getSelectedItem();
            Log.d(TAG, "onItemSelected: "+spinnerService);
            Log.d(TAG, "onItemSelected: IN_SPINNER");
            dialog=new Dialog(BuildDetailActivity.this);
            dialog.setContentView(R.layout.alert_dialog_detail);
            textViewDialogDate =dialog.findViewById(R.id.textViewDetailDialogDate);
            textViewDialogEmployee=dialog.findViewById(R.id.textViewDetailDialogEmployee);
            checkBoxMachineRoom=dialog.findViewById(R.id.checkboxDetailDialogElevatorMachine);
            checkBoxUp=dialog.findViewById(R.id.checkboxDetailDialogElevatorTop);
            checkBoxWell=dialog.findViewById(R.id.checkboxDetailDialogWell);

            checkBoxWell.setChecked(spinnerService.getWell());
            checkBoxUp.setChecked(spinnerService.getElevatorUp());
            checkBoxMachineRoom.setChecked(spinnerService.getMachineRoom());

            textViewDialogEmployee.setText(spinnerService.getEmployee());
            textViewDialogDate.setText(spinnerService.getDate());
            dialog.findViewById(R.id.buttonDetailDialogCancel).setOnClickListener(this);
            dialog.show();

    }
    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }
}