package com.devam.tictactoe.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
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

import java.security.SecureRandom;

public class MainActivity extends AppCompatActivity {

    EditText codeBox,nameBox;
    Button joinButton, createButton;

    static final String AB = "0123456789abcdefghijklmnopqrstuvwxyz";
    static SecureRandom rnd = new SecureRandom();

    String randomString(int len){
        StringBuilder sb = new StringBuilder(len);
        for(int i = 0; i < len; i++)
            sb.append(AB.charAt(rnd.nextInt(AB.length())));
        return sb.toString();
    }

    DatabaseReference gameReference = FirebaseDatabase.getInstance().getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        joinButton = findViewById(R.id.join_button);
        createButton = findViewById(R.id.create_room_button);
        codeBox = findViewById(R.id.code_box);
        nameBox = findViewById(R.id.name_box);

        //gameReference.child("Game").setValue("hello");

        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = nameBox.getText().toString();
                if (name.isEmpty()){
                    //Toast.makeText(MainActivity.this, "Please Enter a Name!", Toast.LENGTH_SHORT).show();
                    nameBox.requestFocus();
                }
                else {
                    String code = randomString(5);
                    //Toast.makeText(MainActivity.this, "Name - " + name + "\n Code - " + code,
                            //Toast.LENGTH_SHORT).show();
                    Player newPlayer = new Player(name,0,false,true,true);
                    gameReference.child("Game").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull @org.jetbrains.annotations.NotNull DataSnapshot snapshot) {
                            boolean flag = false;
                            for(DataSnapshot d:snapshot.getChildren()){
                                if(d.equals(code)){
                                    flag = true;
                                    //Toast.makeText(MainActivity.this, "Try Again!", Toast.LENGTH_SHORT).show();
                                }

                            }
                            if(!flag) {
                                gameReference.child("Game").child(code).child("player").child("player1").setValue(newPlayer);
                                Intent intent = new Intent(MainActivity.this,RoomActivity.class);
                                intent.putExtra("code", code);
                                intent.putExtra("name",name);
                                intent.putExtra("leader",true);
                                startActivity(intent);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull @org.jetbrains.annotations.NotNull DatabaseError error) {

                        }
                    });


                }
            }
        });

        joinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = nameBox.getText().toString();
                if(name.isEmpty()){
                    //Toast.makeText(MainActivity.this, "Please Enter Name!", Toast.LENGTH_SHORT).show();
                    nameBox.requestFocus();
                }
                else {
                    Intent intent = new Intent(MainActivity.this,JoinActivity.class);
                    //Toast.makeText(MainActivity.this, name, Toast.LENGTH_SHORT).show();
                    intent.putExtra("name",name);
                    startActivity(intent);
                }

            }
        });
    }
}