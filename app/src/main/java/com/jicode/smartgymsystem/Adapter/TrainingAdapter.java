package com.jicode.smartgymsystem.Adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.jicode.smartgymsystem.LogDetailActivity;
import com.jicode.smartgymsystem.R;
import com.jicode.smartgymsystem.VO.TrainingLogVO;
import com.jicode.smartgymsystem.VO.TrainingVO;

import java.util.ArrayList;

public class TrainingAdapter extends RecyclerView.Adapter<TrainingAdapter.ViewHolder> {
    private ArrayList<TrainingVO> mList = new ArrayList<TrainingVO>();

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_datalist,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.onBind(mList.get(position));
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public ArrayList<TrainingVO> getmList() {
        return mList;
    }

    public void setmList(ArrayList<TrainingVO> mList) {
        this.mList = mList;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView count;
        TextView set;
        TextView weight;
        TextView runtime;
        TextView resttime;
        LinearLayoutCompat valueLayout;
        RelativeLayout setLayout;
        TextView titleSet;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            count = itemView.findViewById(R.id.count);
            set = itemView.findViewById(R.id.set);
            weight = itemView.findViewById(R.id.weight);
            runtime = itemView.findViewById(R.id.runtime);
            resttime = itemView.findViewById(R.id.resttime);
            valueLayout = itemView.findViewById(R.id.value_layout);
            setLayout = itemView.findViewById(R.id.set_layout);
            titleSet = itemView.findViewById(R.id.title_set);
        }

        void onBind(TrainingVO item){
            if(item.isSetlayout())
            {
                setLayout.setVisibility(View.VISIBLE);
                valueLayout.setVisibility(View.INVISIBLE);
                titleSet.setText(item.getSetcount());
            }
            else {
                setLayout.setVisibility(View.INVISIBLE);
                valueLayout.setVisibility(View.VISIBLE);
                count.setText(String.valueOf(item.getCount()));
                set.setText(String.valueOf(item.getSetcount()));
                weight.setText(String.valueOf(item.getWeight()));

                int rMin = (int)(item.getRunningTime()/600);
                int rSec = (int)((item.getRunningTime()%600)/10);
                int reMin = (int)(item.getRestTime()/600);
                int reSec = (int)((item.getRestTime()%600)/10);
                int redSec = (int)((item.getRestTime()%600)%10);

                runtime.setText(String.format("%02d:%02d",rMin,rSec));
                resttime.setText(String.format("%02d:%02d.%d",reMin,reSec,redSec));

            }
        }
    }
}
