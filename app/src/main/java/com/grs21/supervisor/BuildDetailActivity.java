package com.grs21.supervisor;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.TextView;

import com.grs21.supervisor.activity.ServiceActivity;
import com.grs21.supervisor.databinding.ActivityBuildDetailBinding;
import com.grs21.supervisor.model.Apartment;
import com.grs21.supervisor.model.Service;
import com.grs21.supervisor.model.User;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class BuildDetailActivity extends AppCompatActivity implements View.OnClickListener,AdapterView.OnItemSelectedListener {
    private ActivityBuildDetailBinding binding;
    private Intent intent;
    private Apartment apartment;
    private Dialog dialog;
    private Spinner spinner;
    ArrayList<Service> serviceArrayList;
    private TextView textViewDialogDate,textViewDialogEmployee;
    private CheckBox checkBoxWell,checkBoxUp,checkBoxMachineRoom;
    private static final String TAG = "BuildDetailActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityBuildDetailBinding.inflate(getLayoutInflater());
        View view=binding.getRoot();
        setContentView(view);
        spinner=binding.spinnerDate;
        spinner.setOnItemSelectedListener(this);
        binding.buttonDetailToEdit.setOnClickListener(this);
        binding.buttonMakeService.setOnClickListener(this);
        serviceArrayList=new ArrayList<>();

        intent=getIntent();
        apartment=(Apartment) intent.getSerializableExtra("apartment");

        Toolbar toolbar=binding.toolbarBuildDetail;
        toolbar.setTitle(apartment.getApartmentName());
        toolbar.inflateMenu(R.menu.service_to_detailback_button);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(apartment.getApartmentName());
        
        for (HashMap service:apartment.getServiceArrayList()) {
            Service generateService=new Service(
                     (boolean)service.get("well")
                    ,(boolean)service.get("elevatorUp")
                    ,(boolean)service.get("machineRoom")
                    ,(String) service.get("date")
                    ,(String) service.get("employee"));
            serviceArrayList.add(generateService);
        }
        initializeData(apartment);
    }


    private void initializeData(Apartment apartment) {
        ArrayAdapter<Service> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, serviceArrayList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        adapter.notifyDataSetChanged();
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
                finish();
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
        if (position>0) {
            Service spinnerService = (Service) parent.getSelectedItem();
            dialog = new Dialog(BuildDetailActivity.this);
            dialog.setContentView(R.layout.alert_dialog_detail);
            textViewDialogDate = dialog.findViewById(R.id.textViewDetailDialogDate);
            textViewDialogEmployee = dialog.findViewById(R.id.textViewDetailDialogEmployee);
            checkBoxMachineRoom = dialog.findViewById(R.id.checkboxDetailDialogElevatorMachine);
            checkBoxUp = dialog.findViewById(R.id.checkboxDetailDialogElevatorTop);
            checkBoxWell = dialog.findViewById(R.id.checkboxDetailDialogWell);

            checkBoxWell.setChecked(spinnerService.getWell());
            checkBoxUp.setChecked(spinnerService.getElevatorUp());
            checkBoxMachineRoom.setChecked(spinnerService.getMachineRoom());

            textViewDialogEmployee.setText(spinnerService.getEmployee());
            textViewDialogDate.setText(spinnerService.getDate());
            dialog.findViewById(R.id.buttonDetailDialogCancel).setOnClickListener(this);
            dialog.show();
        }
 }
    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }
}