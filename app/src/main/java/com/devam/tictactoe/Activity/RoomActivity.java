package com.devam.tictactoe.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.PlatformVpnProfile;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.devam.tictactoe.Adapter.PlayerListRecyclerViewAdapter;
import com.devam.tictactoe.Class.Player;
import com.devam.tictactoe.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;


public class RoomActivity extends AppCompatActivity
        implements PlayerListRecyclerViewAdapter.OnPlayerKickClickListner,
            PlayerListRecyclerViewAdapter.OnPlayerReadyClickListner {

    DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Game");
    String name, code;
    boolean leader;
    TextView code_view,memberCount;
    ImageButton shareButton;
    RecyclerView recyclerView;
    PlayerListRecyclerViewAdapter playerListRecyclerView;
    ArrayList<Player> list = new ArrayList<>();
    String tag = "LifeCycleEvents";
    String TAG = "Tag";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room);
        name = getIntent().getStringExtra("name");
        //Toast.makeText(this, "In Room " + name, Toast.LENGTH_SHORT).show();
        Log.d(TAG, "onCreate: In room");
        code = getIntent().getStringExtra("code");
        leader = getIntent().getBooleanExtra("leader",true);
        Player curr = new Player(name,0,false,leader,false);
        code_view = findViewById(R.id.code_view);
        memberCount = findViewById(R.id.member_count);
        shareButton = findViewById(R.id.share);
        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent whatsappIntent = new Intent(Intent.ACTION_SEND);
                whatsappIntent.setType("text/plain");
                //whatsappIntent.setPackage("com.whatsapp");
                whatsappIntent.putExtra(Intent.EXTRA_TEXT, "Code to join TicTacToe is "+code+"");
                try {
                    startActivity(whatsappIntent);
                } catch (android.content.ActivityNotFoundException ex) {
                    //Toast.makeText(RoomActivity.this, "Please Install Whatsapp", Toast.LENGTH_SHORT).show();
                }
            }
        });
        code_view.setText(code);
        recyclerView = findViewById(R.id.player_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(RoomActivity.this));
        ref.child(code).child("player").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                //Toast.makeText(RoomActivity.this, "OnDataChange", Toast.LENGTH_SHORT).show();
                list.clear();
                boolean flag = true,ready = true;
                name = getIntent().getStringExtra("name");
                for(DataSnapshot d:snapshot.getChildren()){
                    //Player player = (Player)d.getValue(Player.class);

                    Player player = new Player();
                    player.setLeader(d.child("leader").getValue(Boolean.class));
                    player.setName(d.child("name").getValue(String.class));
                    player.setReady(d.child("ready").getValue(Boolean.class));
                    player.setScore(d.child("score").getValue(Integer.class));
                    player.setTurn(d.child("turn").getValue(Boolean.class));

                    //Toast.makeText(RoomActivity.this, "This" + name, Toast.LENGTH_SHORT).show();
                    if(name.equals(player.getName())){
                        Log.d("Here", "onDataChange: "+ player.getName());
                        flag = false;
                    }
                    if(!player.isReady()){
                        ready = false;
                    }
                    list.add(player);
                }
                memberCount.setText(list.size()+"/2");
                //Toast.makeText(RoomActivity.this, String.valueOf(flag), Toast.LENGTH_SHORT).show();
                if(flag){
                    Intent i = new Intent(RoomActivity.this,MainActivity.class);
                    //Toast.makeText(RoomActivity.this, "Vaidehi", Toast.LENGTH_SHORT).show();
                    flag = false;
                    int mPendingIntentId = 10;
                    PendingIntent mPendingIntent = PendingIntent.getActivity(getApplicationContext(),
                            mPendingIntentId, i, PendingIntent.FLAG_CANCEL_CURRENT);
                    AlarmManager mgr = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
                    mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 100, mPendingIntent);
                    System.exit(0);

                }
                if(ready && list.size()==2){
                    Intent intent = new Intent(RoomActivity.this,GameActivity.class);
                    intent.putExtra("code", code);
                    intent.putExtra("name",name);
                    intent.putExtra("leader",leader);
                    RoomActivity.this.finish();
                    startActivity(intent);
                }
                playerListRecyclerView = new PlayerListRecyclerViewAdapter(RoomActivity.this,list
                        ,RoomActivity.this,RoomActivity.this,curr);
                recyclerView.setAdapter(playerListRecyclerView);


            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.leave_lobby, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        //i have changed
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_leave) {
            if(leader){
                //Toast.makeText(this, code, Toast.LENGTH_SHORT).show();
                ref.child(code).removeValue();
            }
            else {
                ref.child(code).child("player").child("player2").removeValue();
            }
            Intent intent = new Intent(this,MainActivity.class);
            //Toast.makeText(this, "Leave", Toast.LENGTH_SHORT).show();
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPlayerKickClick(int pos) {
        ref.child(code).child("player").child("player2").removeValue();
    }

    @Override
    public void onPlayerReadyClick(int pos) {
        String temp;
        if(pos == 0){
            temp = "player1";
        }
        else {
            temp = "player2";
        }
        ref.child(code).child("player").child(temp).child("ready").setValue(true);
    }
    public void onStart()
    {
        super.onStart();
        Log.d(tag, "In the onStart() event");
        //Toast.makeText(this, "Start", Toast.LENGTH_SHORT).show();
    }
    public void onRestart()
    {
        super.onRestart();
        Log.d(tag, "In the onRestart() event");
        //Toast.makeText(this, "Restart", Toast.LENGTH_SHORT).show();
    }
    public void onResume()
    {
        super.onResume();
        Log.d(tag, "In the onResume() event");
        name = getIntent().getStringExtra("name");
        //Toast.makeText(this, "Resume in room", Toast.LENGTH_SHORT).show();
    }
    public void onPause()
    {
        super.onPause();
        Log.d(tag, "In the onPause() event");
        //Toast.makeText(this, "Pause", Toast.LENGTH_SHORT).show();
    }
    public void onStop()
    {
        super.onStop();
        Log.d(tag, "In the onStop() event");
        //Toast.makeText(this, "Stop", Toast.LENGTH_SHORT).show();
    }
    public void onDestroy()
    {
        super.onDestroy();
        Log.d(tag, "In the onDestroy() event");
        //Toast.makeText(this, "Destroy", Toast.LENGTH_SHORT).show();
    }
}