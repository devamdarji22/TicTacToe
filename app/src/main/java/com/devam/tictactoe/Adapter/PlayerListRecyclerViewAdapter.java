package com.devam.tictactoe.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.devam.tictactoe.Class.Player;
import com.devam.tictactoe.R;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class PlayerListRecyclerViewAdapter extends RecyclerView.Adapter<PlayerListRecyclerViewAdapter.MyViewHolder>{

    Context context;
    ArrayList<Player> names;
    OnPlayerKickClickListner onPlayerKickClickListner;
    OnPlayerReadyClickListner onPlayerReadyClickListner;
    Player player;

    public PlayerListRecyclerViewAdapter(Context context, ArrayList<Player> names,
                                         OnPlayerKickClickListner onPlayerKickClickListner,
                                         OnPlayerReadyClickListner onPlayerReadyClickListner,Player player) {
        this.context = context;
        this.names = names;
        this.onPlayerKickClickListner = onPlayerKickClickListner;
        this.onPlayerReadyClickListner = onPlayerReadyClickListner;
        this.player = player;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.each_player_lobby,parent,false);
        MyViewHolder viewHolder = new MyViewHolder(view, onPlayerKickClickListner,onPlayerReadyClickListner);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        if(!player.isLeader()){
            holder.kickButton.setVisibility(View.GONE);
        }
        else {
            holder.kickButton.setVisibility(View.VISIBLE);
        }
        if(names.get(position).isLeader()){
            holder.kickButton.setVisibility(View.GONE);
        }
        if(player.getName().equals(names.get(position).getName())){
            holder.readyButton.setVisibility(View.VISIBLE);
        }
        else {
            holder.readyButton.setVisibility(View.GONE);
        }
        if(names.get(position).isReady()){
            holder.ready.setText("Ready");
            holder.readyButton.setVisibility(View.GONE);
        }
        else {
            holder.ready.setText("Not Ready");
        }
        holder.name.setText(names.get(position).getName());
        holder.pointView.setText(String.valueOf(names.get(position).getScore()));

    }

    @Override
    public int getItemCount() {
        return names.size();
    }

    //implements View.OnClickListener
    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        OnPlayerReadyClickListner onPlayerReadyClickListner;
        OnPlayerKickClickListner onPlayerKickClickListner;
        TextView name,ready,pointView;
        Button kickButton,readyButton;

        public MyViewHolder(@NonNull View itemView, final OnPlayerKickClickListner onPlayerKickClickListner,
                            final OnPlayerReadyClickListner onPlayerReadyClickListner) {
            super(itemView);
            name = itemView.findViewById(R.id.lobby_name_view);
            readyButton = itemView.findViewById(R.id.lobby_ready_button);
            ready = itemView.findViewById(R.id.lobby_ready_view);
            kickButton = itemView.findViewById(R.id.lobby_kick_button);
            pointView = itemView.findViewById(R.id.point_view);
            this.onPlayerKickClickListner = onPlayerKickClickListner;
            this.onPlayerReadyClickListner = onPlayerReadyClickListner;
            kickButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onPlayerKickClickListner.onPlayerKickClick(getAdapterPosition());
                }
            });
            readyButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onPlayerReadyClickListner.onPlayerReadyClick(getAdapterPosition());
                }
            });
        }

        @Override
        public void onClick(View view) {
            onPlayerKickClickListner.onPlayerKickClick(getAdapterPosition());
            onPlayerReadyClickListner.onPlayerReadyClick(getAdapterPosition());
        }
    }

    public interface OnPlayerKickClickListner{
        void onPlayerKickClick(int pos);
    }
    public interface OnPlayerReadyClickListner {
        void onPlayerReadyClick(int pos);
    }

}
