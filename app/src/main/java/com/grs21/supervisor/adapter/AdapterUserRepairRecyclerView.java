package com.grs21.supervisor.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.grs21.supervisor.R;
import com.grs21.supervisor.model.Repair;

import java.util.ArrayList;

public class AdapterUserRepairRecyclerView extends RecyclerView.Adapter<AdapterUserRepairRecyclerView.UserRepairVİewHolder> {

    private ArrayList<Repair> repairArrayList;
    private UserRepairListener listener;

    public AdapterUserRepairRecyclerView(ArrayList<Repair> repairArrayList, UserRepairListener listener) {
        this.repairArrayList = repairArrayList;
        this.listener=listener;
    }

    public interface UserRepairListener{
        void  onClickListener(View view,int position);
    }

    @NonNull
    @Override
    public UserRepairVİewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_repair_recyclerview
                , parent,false);
        return new UserRepairVİewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserRepairVİewHolder holder, int position) {
        String buildName=repairArrayList.get(position).getApartmentName();
        String repairDate=repairArrayList.get(position).getDate();
        holder.textViewBuildName.setText(buildName);
        holder.textViewDate.setText(repairDate);

    }

    @Override
    public int getItemCount() {
        return repairArrayList.size();
    }

    class UserRepairVİewHolder extends RecyclerView.ViewHolder
    implements View.OnClickListener
    {
        TextView textViewBuildName,textViewDate;

        public UserRepairVİewHolder(@NonNull View itemView) {
            super(itemView);
            textViewBuildName=itemView.findViewById(R.id.textViewRepairRecyclerViewItemBuildName);
            textViewDate=itemView.findViewById(R.id.textViewRepairRecyclerViewDate);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            listener.onClickListener(v, getAdapterPosition() );
        }
    }


}
