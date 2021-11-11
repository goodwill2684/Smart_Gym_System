package com.jicode.smartgymsystem.Adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.jicode.smartgymsystem.Event_Activity;
import com.jicode.smartgymsystem.R;
import com.jicode.smartgymsystem.VO.EventVO;

import java.util.ArrayList;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.ViewHolder> {
    private ArrayList<EventVO> mList = new ArrayList<EventVO>();

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

    public ArrayList<EventVO> getmList() {
        return mList;
    }

    public void setmList(ArrayList<EventVO> mList) {
        this.mList = mList;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView date;
        TextView time;
        TextView event;
        ImageView warning_icon;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            date = (TextView) itemView.findViewById(R.id.dateText);
            time = (TextView) itemView.findViewById(R.id.timeText);
            event = (TextView) itemView.findViewById(R.id.warningText);
            warning_icon = (ImageView) itemView.findViewById(R.id.warning_icon);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int pos = getAdapterPosition();
                    if (pos != RecyclerView.NO_POSITION) {
                        EventVO item = mList.get(pos);
                        Intent intent = new Intent(view.getContext(), Event_Activity.class);
                        intent.putExtra("data",item);
                        view.getContext().startActivity(intent);
                    }
                }
            });
        }

        void onBind(EventVO item){
            date.setText(item.getTime().substring(0,4)+"."+item.getTime().substring(4,6)+"."+item.getTime().substring(6,8));
            time.setText(item.getTime().substring(8,10)+":"+item.getTime().substring(10,12)+":"+item.getTime().substring(12));
            event.setText(item.getEvent());
        }
    }
}
