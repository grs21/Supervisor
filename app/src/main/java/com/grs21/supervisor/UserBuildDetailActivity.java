package com.grs21.supervisor;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
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
import com.grs21.supervisor.databinding.ActivityUserBuildDetailBinding;
import com.grs21.supervisor.model.Apartment;
import com.grs21.supervisor.model.Service;
import com.grs21.supervisor.model.User;

import java.util.ArrayList;
import java.util.HashMap;

public class UserBuildDetailActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {
    private static final String TAG = "UserBuildDetailActivity";
    private ActivityUserBuildDetailBinding binding;
    private Intent intent;
    private ArrayList<Service> serviceArrayList=new ArrayList<>();
    private Apartment apartment;
    private User currentUser;
    private Dialog dialog;
    private Spinner spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityUserBuildDetailBinding.inflate(getLayoutInflater());
        View view=binding.getRoot();
        setContentView(view);
        getIntentData();
        initializeSpinner();
        initializeData(apartment);
        setToolBar();
        binding.buttonUserMakeService.setOnClickListener(this);




    }

    private void initializeSpinner() {
        spinner=binding.spinnerUserDate;
        spinner.setOnItemSelectedListener(this);
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

    }

    private void setToolBar() {
        Toolbar toolbar=binding.toolbarUserBuildDetail;
        toolbar.setTitle(apartment.getApartmentName());
        toolbar.inflateMenu(R.menu.service_to_detailback_button);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(apartment.getApartmentName());
    }

    private void getIntentData() {
        intent=getIntent();
        apartment=(Apartment) intent.getSerializableExtra("apartment");
        currentUser=(User)intent.getSerializableExtra("currentUser");
    }

    @Override
    public void onClick(View v) {
       final int makeService=R.id.buttonUserMakeService;
       final int dialogCancel=R.id.buttonDetailDialogCancel;
        switch (v.getId()){
            case makeService:
                Intent intent=new Intent(UserBuildDetailActivity.this, ServiceActivity.class);
                String senderClassName=this.getLocalClassName();
                intent.putExtra("apartment", apartment);
                intent.putExtra("currentUser",currentUser);
                intent.putExtra("className", senderClassName);
                startActivity(intent);
                finish();
                break;
            case dialogCancel:
                dialog.dismiss();
                break;
        }
    }
    @Override
    public void onBackPressed() {
        Intent intent=new Intent(UserBuildDetailActivity.this, UserActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("currentUser", currentUser);
        startActivity(intent);
        super.onBackPressed();
    }

    private void initializeData(Apartment apartment) {
        ArrayAdapter<Service> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, serviceArrayList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        binding.textViewUserBuildDetailBuildName.setText(apartment.getApartmentName());
        binding.textViewUserBuildDetailBuildAddress.setText(apartment.getApartmentAddress());
        binding.textViewUserBuildDetailCost.setText(apartment.getCost());
        binding.textViewUserBuildDetailManagerName.setText(apartment.getManagerName());
        binding.textViewUserBuildDetailManagerNumber.setText(apartment.getManagerNumber());
        binding.textViewUserBuildDetailManagerAddress.setText(apartment.getManagerAddress());
        binding.textViewUserBuildDetailEmployeeName.setText(apartment.getEmployeeName());
        binding.textViewUserBuildDetailEmployeeNumber.setText(apartment.getEmployeeNumber());
        binding.textViewUserBuildDetailContractDate.setText(apartment.getContractDate());
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (position>0) {
            TextView textViewDialogDate,textViewDialogEmployee,textViewDialogCost;
            CheckBox checkBoxWell,checkBoxUp,checkBoxMachineRoom;
            Service spinnerService = (Service) parent.getSelectedItem();
            dialog = new Dialog(UserBuildDetailActivity.this);
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
}