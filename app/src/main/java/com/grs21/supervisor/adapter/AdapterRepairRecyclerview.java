package com.grs21.supervisor.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.grs21.supervisor.R;
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
       View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_repair_recyclerview,parent);

        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RepairViewHolder holder, int position) {


    }

    @Override
    public int getItemCount() {
        return repairArrayList.size();
    }

    public class RepairViewHolder extends RecyclerView.ViewHolder{


        public RepairViewHolder(@NonNull View itemView) {

            super(itemView);
        }
    }


}
