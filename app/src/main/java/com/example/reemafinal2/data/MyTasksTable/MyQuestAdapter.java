package com.example.reemafinal2.data.MyTasksTable;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.reemafinal2.R;

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
        }
            ImageView imageview = vitem.findViewById(R.id.gameImage);
            TextView tvtitle = vitem.findViewById(R.id.TV_taskType);
            TextView TV_time = vitem.findViewById(R.id.TV_time);
            TextView TV_score = vitem.findViewById(R.id.TV_score);
            TextView TV_gameId = vitem.findViewById(R.id.TV_gameId);
            TextView TV_titleGame = vitem.findViewById(R.id.TV_titleGame);
            Button btnstart = vitem.findViewById(R.id.btnStart);


            MyQuest current = getItem(position);
            tvtitle.setText(current.getTitle());
            TV_time.setText(current.getTime());
            TV_gameId.setText(current.getGameId());
            TV_score.setText(current.getRewardpoints());

            return vitem;

        }
    }









