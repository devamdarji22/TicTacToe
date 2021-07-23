package com.devam.tictactoe.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.devam.tictactoe.Class.Player;
import com.devam.tictactoe.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

public class JoinActivity extends AppCompatActivity {

    EditText codeBox;
    String code,name;
    Button joinButton;
    DatabaseReference gameReference = FirebaseDatabase.getInstance().getReference();
    DatabaseReference ref = FirebaseDatabase.getInstance().getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join);
        name = getIntent().getStringExtra("name");
        codeBox = findViewById(R.id.code_box);
        joinButton = findViewById(R.id.join_room_button);

        joinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                code = codeBox.getText().toString();
                if (code.isEmpty()) {
                    Toast.makeText(JoinActivity.this, "Please enter a code!", Toast.LENGTH_SHORT).show();
                    codeBox.requestFocus();
                }
                else {
                    Toast.makeText(JoinActivity.this, "Name - " + name + "\n Code - " + code,
                            Toast.LENGTH_SHORT).show();
                    Player newPlayer = new Player(name,0,false,false,false);
                    gameReference.child("Game").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull @org.jetbrains.annotations.NotNull DataSnapshot snapshot) {
                            final boolean[] flag = {false};
                            for(DataSnapshot d:snapshot.getChildren()){
                                if(d.getKey().equals(code)){

                                    ref.child("Game").child(code).child("player").addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                                            Long count = snapshot.getChildrenCount();
                                            if(count < Long.valueOf("2")){
                                                flag[0] = true;
                                                //Toast.makeText(JoinActivity.this, name, Toast.LENGTH_SHORT).show();
                                                gameReference.child("Game").child(code).child("player").child("player2").setValue(newPlayer);
                                                Intent intent = new Intent(JoinActivity.this,RoomActivity.class);
                                                intent.putExtra("code", code);
                                                intent.putExtra("name",name);
                                                intent.putExtra("leader",false);
                                                startActivity(intent);
                                            }
                                            else {
                                                Toast.makeText(JoinActivity.this, "Room is Full!", Toast.LENGTH_SHORT).show();
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull @NotNull DatabaseError error) {

                                        }
                                    });




                                }

                            }
                            if(!flag[0]) {
                                Toast.makeText(JoinActivity.this, "Try Again!", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull @org.jetbrains.annotations.NotNull DatabaseError error) {

                        }
                    });
                }
            }
        });



    }
}