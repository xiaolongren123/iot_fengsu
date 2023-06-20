package com.xiaolongrenzz.xyz.fengsubiao.fragment.page4;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.xiaolongrenzz.xyz.fengsubiao.R;

import java.util.ArrayList;
import java.util.List;


public class ListAdapter extends RecyclerView.Adapter<ListAdapter.MyViewHolder> {

    List<ListData> listData =new ArrayList<>();

    public void setAllWords(List<ListData> allWords) {
        this.listData = allWords;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater=LayoutInflater.from(parent.getContext());
        View itemView=layoutInflater.inflate(R.layout.list_item,parent,false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        ListData listData= this.listData.get(position);
        holder.daytime.setText(listData.getTime());
        holder.nowData.setText(String.valueOf(listData.getPress()));
    }

    @Override
    public int getItemCount() {
        return listData.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder{
        TextView daytime,nowData;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            daytime=itemView.findViewById(R.id.daytime);
            nowData=itemView.findViewById(R.id.nowdata);

        }
    }
}
