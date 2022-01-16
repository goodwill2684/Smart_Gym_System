package com.jicode.smartgymsystem.Adapter;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.jicode.smartgymsystem.LogDetailActivity;
import com.jicode.smartgymsystem.R;
import com.jicode.smartgymsystem.VO.TrainingLogVO;

import java.util.ArrayList;

public class TrainingLogAdapter extends RecyclerView.Adapter<TrainingLogAdapter.ViewHolder> {
    private ArrayList<TrainingLogVO> mList = new ArrayList<TrainingLogVO>();
    Activity activity;

    public TrainingLogAdapter(Activity activity) {
        this.activity = activity;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_event,parent,false);
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

    public ArrayList<TrainingLogVO> getmList() {
        return mList;
    }

    public void setmList(ArrayList<TrainingLogVO> mList) {
        this.mList = mList;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView date;
        TextView time;
        TextView deviceName;
        ImageView warning_icon;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            date = (TextView) itemView.findViewById(R.id.dateText);
            time = (TextView) itemView.findViewById(R.id.timeText);
            deviceName = (TextView) itemView.findViewById(R.id.device_name);
            warning_icon = (ImageView) itemView.findViewById(R.id.warning_icon);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int pos = getAdapterPosition();
                    if (pos != RecyclerView.NO_POSITION) {
                        TrainingLogVO item = mList.get(pos);
                        Intent intent = new Intent(view.getContext(), LogDetailActivity.class);
                        intent.putExtra("data",item);
                        activity.startActivity(intent);
                    }
                }
            });
        }

        void onBind(TrainingLogVO item){
            date.setText(item.getTime().substring(0,4)+"."+item.getTime().substring(4,6)+"."+item.getTime().substring(6,8));
            time.setText(item.getTime().substring(8,10)+":"+item.getTime().substring(10,12)+":"+item.getTime().substring(12,14));
            deviceName.setText(item.getDevicecName());
        }
    }
}
