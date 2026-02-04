package com.example.reemafinal2.data.MyTasksTable;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class MyQuestAdapter extends ArrayAdapter<MyQuest> {
    private final int itemLayout;

    public MyQuestAdapter(@NonNull Context context, int resource) {
        super(context, resource);
        this.itemLayout = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View vitem = convertView;
        if (vitem == null) {
            vitem = LayoutInflater.from(getContext()).inflate(itemLayout, parent, false);
       ImageView imageview = vitem.findViewById(R.id.imgvitm);
            TextView tvtitle = vitem.findViewById(R.id.TV_title);
            TextView tvtime = vitem.findViewById(R.id.TV_time);
            TextView tvgameid = vitem.findViewById(R.id.TV_gameID);
            TextView tvRewardpoints = vitem.findViewById(R.id.TV_rewardp);
            ImageButton btnstart = vitem.findViewById(R.id.btn_start);
            ImageButton btnretry = vitem.findViewById(R.id.btn_retry);
            ImageButton btnstop = vitem.findViewById(R.id.btn_stop);
            MyQuest current=getItem(position);
            tvtitle.setText(current.getTitle());
            tvtime.setText(current.getTime());
            tvgameid.setText(current.getGameId());
            tvRewardpoints.setText(current.getRewardpoints());
            return vitem;

        }
    }
}






