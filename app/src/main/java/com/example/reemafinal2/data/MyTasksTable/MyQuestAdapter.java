package com.example.reemafinal2.data.MyTasksTable;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.reemafinal2.PlayQuestActivity;
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

        TextView tvtitle = vitem.findViewById(R.id.TV_taskType);
        TextView TV_time = vitem.findViewById(R.id.TV_time);
        TextView TV_score = vitem.findViewById(R.id.TV_score);
        TextView TV_gameId = vitem.findViewById(R.id.TV_gameId);
        Button btnstart = vitem.findViewById(R.id.btnStart);

        MyQuest current = getItem(position);
        if (current != null) {
            tvtitle.setText(current.getTitle());
            TV_time.setText("Time: " + current.getTime());
            TV_gameId.setText("ID: " + current.getGameId());
            TV_score.setText("Points: " + current.getRewardpoints());

            btnstart.setOnClickListener(v -> {
                Intent intent = new Intent(getContext(), PlayQuestActivity.class);
                intent.putExtra("QUEST_TITLE", current.getTitle());
                getContext().startActivity(intent);
            });
        }

        return vitem;
    }
}
