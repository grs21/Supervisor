package com.grs21.supervisor.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.grs21.supervisor.model.Repair;

import java.util.ArrayList;

public class AdapterRepairRecyclerview extends RecyclerView.Adapter<AdapterRepairRecyclerview.RepairViewHolder> {

    private ArrayList<Repair> repairArrayList;

    public AdapterRepairRecyclerview(ArrayList<Repair> repairArrayList) {
        this.repairArrayList = repairArrayList;
    }

    @NonNull
    @Override
    public RepairViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RepairViewHolder holder, int position) {


    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public class RepairViewHolder extends RecyclerView.ViewHolder{


        public RepairViewHolder(@NonNull View itemView) {

            super(itemView);
        }
    }


}
