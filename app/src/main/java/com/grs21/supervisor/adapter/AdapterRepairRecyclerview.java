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

public class AdapterRepairRecyclerview extends RecyclerView.Adapter<AdapterRepairRecyclerview.RepairViewHolder> {
    private ArrayList<Repair> repairArrayList;
    private static final String TAG = "AdapterRepairRecyclervi";
    private RepairRecyclerviewOnclickListener listener;
    public AdapterRepairRecyclerview(ArrayList<Repair> repairArrayList,RepairRecyclerviewOnclickListener listener) {
        this.repairArrayList = repairArrayList;
        this.listener=listener;
    }
    public interface RepairRecyclerviewOnclickListener{
        void  onClickListener(View view,int position);
    }
    @NonNull
    @Override
    public RepairViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       View view= LayoutInflater.from(parent.getContext())
               .inflate(R.layout.item_repair_recyclerview,parent,false);
       return new RepairViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull RepairViewHolder holder, int position) {
    holder.textViewBuildName.setText(repairArrayList.get(position).getApartmentName());
    holder.textViewDate.setText(repairArrayList.get(position).getDate());
    }
    @Override
    public int getItemCount() {
        return repairArrayList.size();
    }
    public class RepairViewHolder extends RecyclerView.ViewHolder
    implements View.OnClickListener
    {
        TextView textViewBuildName,textViewDate;
        public RepairViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewBuildName=itemView.findViewById(R.id.textViewRepairRecyclerViewItemBuildName);
            textViewDate=itemView.findViewById(R.id.textViewRepairRecyclerViewDate);
            itemView.setOnClickListener(this);
        }
        @Override
        public void onClick(View v) {
            listener.onClickListener(v, getAdapterPosition());
        }
    }
}
