package com.grs21.supervisor;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.TextView;

import com.grs21.supervisor.activity.ServiceActivity;
import com.grs21.supervisor.databinding.ActivityAdminBuildDetailBinding;
import com.grs21.supervisor.model.Apartment;
import com.grs21.supervisor.model.Service;
import com.grs21.supervisor.model.User;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

public class BuildDetailActivity extends AppCompatActivity implements View.OnClickListener,AdapterView.OnItemSelectedListener {
    private ActivityAdminBuildDetailBinding binding;
    private Intent intent;
    private Apartment apartment;
    private Dialog dialog;
    private Spinner spinner;
    private ArrayList<Service> serviceArrayList;

    private static final String TAG = "BuildDetailActivity";
    private User currentUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityAdminBuildDetailBinding.inflate(getLayoutInflater());
        View view=binding.getRoot();
        setContentView(view);
        spinner=binding.spinnerDate;
        spinner.setOnItemSelectedListener(this);
        binding.buttonDetailToEdit.setOnClickListener(this);
        binding.buttonMakeService.setOnClickListener(this);
        serviceArrayList=new ArrayList<>();


        intent=getIntent();
        apartment=(Apartment) intent.getSerializableExtra("apartment");
        currentUser=(User)intent.getSerializableExtra("currentUser");
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
                    ,(String) service.get("employee")
                    ,(String)service.get("cost"));
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
        final int buttonGotoEdit=R.id.buttonDetailToEdit;
        final int buttonDialogCancel=R.id.buttonDetailDialogCancel;
        final int buttonMakeService=R.id.buttonMakeService;
        switch (v.getId()){
            case buttonGotoEdit:
                Intent intent=new Intent(BuildDetailActivity.this, ApartmentEditActivity.class);
                intent.putExtra("apartment", (Serializable) apartment);
                intent.putExtra("currentUser", currentUser);
                startActivity(intent);
                finish();
                break;
            case buttonDialogCancel:
                dialog.dismiss();
                break;
            case buttonMakeService:
                Intent intent1=new Intent(BuildDetailActivity.this, ServiceActivity.class);
                intent1.putExtra("apartment", apartment);
                intent1.putExtra("currentUser",currentUser);
                startActivity(intent1);
                finish();
                break;
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (position>0) {
            TextView textViewDialogDate,textViewDialogEmployee,textViewDialogCost;
            CheckBox checkBoxWell,checkBoxUp,checkBoxMachineRoom;
            Service spinnerService = (Service) parent.getSelectedItem();
            dialog = new Dialog(BuildDetailActivity.this);
            dialog.setContentView(R.layout.alert_dialog_detail);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            textViewDialogDate = dialog.findViewById(R.id.textViewDetailDialogDate);
            textViewDialogEmployee = dialog.findViewById(R.id.textViewDetailDialogEmployee);
            textViewDialogCost=dialog.findViewById(R.id.textViewDetailDialogCost);
            checkBoxMachineRoom = dialog.findViewById(R.id.checkboxDetailDialogElevatorMachine);
            checkBoxUp = dialog.findViewById(R.id.checkboxDetailDialogElevatorTop);
            checkBoxWell = dialog.findViewById(R.id.checkboxDetailDialogWell);

            checkBoxWell.setChecked(spinnerService.getWell());
            checkBoxUp.setChecked(spinnerService.getElevatorUp());
            checkBoxMachineRoom.setChecked(spinnerService.getMachineRoom());

            textViewDialogEmployee.setText(spinnerService.getEmployee());
            textViewDialogDate.setText(spinnerService.getDate());
            textViewDialogCost.setText(spinnerService.getCost());
            dialog.findViewById(R.id.buttonDetailDialogCancel).setOnClickListener(this);
            dialog.show();
        }
    }
    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }

    @Override
    public void onBackPressed() {
        Intent intent=new Intent(BuildDetailActivity.this, AdminActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("currentUser", currentUser);
        startActivity(intent);
        super.onBackPressed();
    }
}