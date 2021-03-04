package com.grs21.supervisor.adapter;

import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.grs21.supervisor.BuildDetailActivity;
import com.grs21.supervisor.R;
import com.grs21.supervisor.model.Apartment;
import com.grs21.supervisor.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AdapterApartmentRecyclerView extends RecyclerView.Adapter<AdapterApartmentRecyclerView.ViewHolder> implements Filterable {

    private ArrayList<Apartment> apartments;
    private ArrayList<Apartment> apartmentFull;
    private static final String TAG = "AdapterApartmentRecycle";
    private User currentUser;

    public AdapterApartmentRecyclerView(ArrayList<Apartment> apartments,User currentUser) {
        this.apartments = apartments;
        apartmentFull = new ArrayList<>();
        apartmentFull.addAll(apartments);
        this.currentUser=currentUser;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_build_recyclerview, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        int lastServiceIndex=apartments.get(position).getServiceArrayList().size()-1;
        HashMap lastService=apartments.get(position).getServiceArrayList().get(lastServiceIndex);

        holder.buildName.setText(apartments.get(position).getApartmentName());
        holder.contractDate.setText((String)lastService.get("date"));
        holder.checkBoxWell.setChecked((boolean) lastService.get("well"));
        holder.checkBoxMachineRoom.setChecked((boolean)lastService.get("machineRoom"));
        holder.checkBoxElevatorUp.setChecked((boolean)lastService.get("elevatorUp"));
        holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(v.getContext(), BuildDetailActivity.class);
                intent.putExtra("apartment",apartments.get(position));
                intent.putExtra("currentUser",currentUser);
                v.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return apartments.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder  {
        TextView buildName, contractDate;
        RelativeLayout relativeLayout;
        CheckBox checkBoxWell,checkBoxElevatorUp,checkBoxMachineRoom;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            checkBoxElevatorUp=itemView.findViewById(R.id.checkboxRecyclerViewElevatorTop);
            checkBoxMachineRoom=itemView.findViewById(R.id.checkboxRecyclerViewElevatorMachine);
            checkBoxWell=itemView.findViewById(R.id.checkboxRecyclerViewWell);
            buildName = itemView.findViewById(R.id.textViewRecyclerViewBuildName);
            contractDate = itemView.findViewById(R.id.textViewRecyclerViewMostLastServiceDate);
            relativeLayout=itemView.findViewById(R.id.relativeLayout);

        }
    }

    @Override
    public Filter getFilter() {
        return filter;
    }

    private Filter filter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            List<Apartment> filteredList = new ArrayList<>();
            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(apartmentFull);
            }
            else {
                String filterPattern = constraint.toString().toLowerCase().trim();
                for (Apartment item : apartmentFull) {
                    if (item.getApartmentName().toLowerCase().contains(filterPattern)) {
                        filteredList.add(item);
                    }
                }
            }
            FilterResults results = new FilterResults();
            results.values = filteredList;
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            apartments.clear();
            apartments.addAll((List)results.values);
            notifyDataSetChanged();
        }
    };

}
