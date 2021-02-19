package com.grs21.supervisor;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.grs21.supervisor.databinding.ActivityBuildDetailBinding;
import com.grs21.supervisor.model.Apartment;

import java.io.Serializable;

public class BuildDetailActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener{
    private ActivityBuildDetailBinding binding;
    private Intent intent;
    private Apartment apartment;
    private Dialog dialog;
    private TextView textViewName;
    private static final String TAG = "BuildDetailActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityBuildDetailBinding.inflate(getLayoutInflater());
        View view=binding.getRoot();
        setContentView(view);
        binding.buttonDetailToEdit.setOnClickListener(this);
        binding.spinnerDate.setOnItemSelectedListener(this);

        intent=getIntent();
        apartment=(Apartment) intent.getSerializableExtra("apartment");

        Toolbar toolbar=findViewById(R.id.toolbarBuildDetail);
        toolbar.setTitle(apartment.getApartmentName());

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(apartment.getApartmentName());
        initializeData(apartment);
    }

    private void initializeData(Apartment apartment) {
        ArrayAdapter spinnerAdapter=ArrayAdapter.createFromResource(BuildDetailActivity.this
                ,R.array.month,android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerDate.setAdapter(spinnerAdapter);
        binding.spinnerDate.setSelected(false);

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
            case R.id.buttonDialogCancel  :
                dialog.dismiss();
                break;
        }

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (position>0){
            String text=parent.getItemAtPosition(position).toString();
            dialog=new Dialog(BuildDetailActivity.this);
            dialog.setContentView(R.layout.alert_dialog);
            textViewName =dialog.findViewById(R.id.textViewServiceDialogName);
            textViewName.setText(text);
            dialog.findViewById(R.id.buttonDialogCancel).setOnClickListener(this);
            dialog.show();
        }
    }
    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }
}