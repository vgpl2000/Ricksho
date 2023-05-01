package com.example.ricksho;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseReference;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {

    private List<UserList> listItems;
    private final Context context;

    public UserAdapter(List<UserList> listItems, Context context) {
        this.listItems = listItems;
        this.context = context;
    }
    @NonNull
    @Override
    public UserAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext())
                .inflate(R.layout.single_row_orders,parent,false);
        return new UserAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserAdapter.ViewHolder holder, int position) {

        UserList listItem=listItems.get(position);
        holder.u_name.setText(listItem.getUName());
        holder.address.setText(listItem.getAddress());


    }

    @Override
    public int getItemCount() {
       return listItems.size();
    }

    public void setDriverList(List<UserList> mDriverList) {
        this.listItems = mDriverList;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public TextView u_name;
        public TextView address;
        DatabaseReference mRef;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            u_name=itemView.findViewById(R.id.u_name);
            address=itemView.findViewById(R.id.address);
        }
    }
}


