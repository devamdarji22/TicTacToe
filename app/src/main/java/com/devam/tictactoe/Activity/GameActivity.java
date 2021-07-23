package com.devam.tictactoe.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.devam.tictactoe.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

public class GameActivity extends AppCompatActivity implements View.OnClickListener {

    private Button[][] buttons = new Button[3][3];
    private boolean player1Turn = true,order = true;
    private int roundCount;
    private int player1Points;
    private int player2Points;
    private TextView textViewPlayer1;
    private TextView textViewPlayer2;
    private TextView drawTextView;
    private TextView gameCountTextView;

    private String code,name;

    private String winText;

    private boolean leader;

    String player1="",player2="";

    private int drawCount = 0;
    private int gameCount = 1;

    private DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Game");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        code = getIntent().getStringExtra("code");
        name = getIntent().getStringExtra("name");
        leader = getIntent().getBooleanExtra("leader",true);

        drawTextView = findViewById(R.id.text_view_p3);
        gameCountTextView = findViewById(R.id.round_count);

        ref.child(code).child("data").child("turn").child("player1turn").setValue(player1Turn);
        ref.child(code).child("data").child("turn").child("order").setValue(order);
        ref.child(code).child("data").child("button").child("button_00").setValue("");
        ref.child(code).child("data").child("button").child("button_01").setValue("");
        ref.child(code).child("data").child("button").child("button_02").setValue("");
        ref.child(code).child("data").child("button").child("button_10").setValue("");
        ref.child(code).child("data").child("button").child("button_11").setValue("");
        ref.child(code).child("data").child("button").child("button_12").setValue("");
        ref.child(code).child("data").child("button").child("button_20").setValue("");
        ref.child(code).child("data").child("button").child("button_21").setValue("");
        ref.child(code).child("data").child("button").child("button_22").setValue("");
        int tempScore = 0;
        ref.child(code).child("data").child("roundcount").setValue(roundCount);
        ref.child(code).child("data").child("score").child("player1points").setValue(tempScore);
        ref.child(code).child("data").child("score").child("player2points").setValue(tempScore);
        ref.child(code).child("data").child("score").child("draw").setValue(drawCount);
        ref.child(code).child("data").child("score").child("gamecount").setValue(gameCount);
        //ref.child(code).child("data").child("score").child("text").setValue("");


        ref.child(code).child("player").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                player1 = snapshot.child("player1").child("name").getValue(String.class);
                player2 = snapshot.child("player2").child("name").getValue(String.class);
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });

        textViewPlayer1 = findViewById(R.id.text_view_p1);
        textViewPlayer2 = findViewById(R.id.text_view_p2);
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                String buttonID = "button_" + i + j;
                int resID = getResources().getIdentifier(buttonID, "id", getPackageName());
                buttons[i][j] = findViewById(resID);
                buttons[i][j].setOnClickListener(this);
            }
        }
        Button buttonReset = findViewById(R.id.button_reset);
        if(!leader){
            buttonReset.setVisibility(View.GONE);
        }
        buttonReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetGame();
            }
        });

        ref.child(code).child("player").child("player2").child("name").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                if(!snapshot.exists()){
                    Intent intent = new Intent(GameActivity.this,RoomActivity.class);
                    Toast.makeText(GameActivity.this, "Player 2 left", Toast.LENGTH_SHORT).show();
                    intent.putExtra("name", name);
                    intent.putExtra("code",code);
                    intent.putExtra("leader" ,true);
                    GameActivity.this.finish();

                    startActivity(intent);
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });

        ref.child(code).child("data").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {

                buttons[0][0].setText(snapshot.child("button").child("button_00").getValue(String.class));
                buttons[0][1].setText(snapshot.child("button").child("button_01").getValue(String.class));
                buttons[0][2].setText(snapshot.child("button").child("button_02").getValue(String.class));
                buttons[1][0].setText(snapshot.child("button").child("button_10").getValue(String.class));
                buttons[1][1].setText(snapshot.child("button").child("button_11").getValue(String.class));
                buttons[1][2].setText(snapshot.child("button").child("button_12").getValue(String.class));
                buttons[2][0].setText(snapshot.child("button").child("button_20").getValue(String.class));
                buttons[2][1].setText(snapshot.child("button").child("button_21").getValue(String.class));
                buttons[2][2].setText(snapshot.child("button").child("button_22").getValue(String.class));
                roundCount = snapshot.child("roundcount").getValue(Integer.class);
                drawCount = snapshot.child("score").child("draw").getValue(Integer.class);
                if(roundCount == 9 ){
                    //Toast.makeText(GameActivity.this, "Draw!", Toast.LENGTH_SHORT).show();
                    drawTextView.setText("Draw - " + drawCount);
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });

        ref.child(code).child("data").child("score").child("text").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                Toast.makeText(GameActivity.this, snapshot.getValue(String.class), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });

        ref.child(code).child("data").child("score").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                player1Points = snapshot.child("player1points").getValue(Integer.class);
                player2Points = snapshot.child("player2points").getValue(Integer.class);
                textViewPlayer1.setText(player1 +" - "  + String.valueOf(snapshot.child("player1points").getValue(Integer.class)));
                textViewPlayer2.setText(player2 +" - "  + String.valueOf(
                        snapshot.child("player2points").getValue(Integer.class)));
                gameCount = snapshot.child("gamecount").getValue(Integer.class);
                gameCountTextView.setText("Round - " + snapshot.child("gamecount").getValue(Integer.class));
                drawCount = snapshot.child("draw").getValue(Integer.class);
                drawTextView.setText("Draw - " + drawCount);
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });

        ref.child(code).child("data").child("turn").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                player1Turn = snapshot.child("player1turn").getValue(Boolean.class);
                order = snapshot.child("order").getValue(Boolean.class);
                if((player1Turn == true && leader == true && order == true)
                        || (!player1Turn && leader && !order ) ||
                        (player1Turn == false && leader == false && order == true)
                        || (player1Turn == true && leader == false && order == false)){
                    for (int i = 0; i < 3; i++) {
                        for (int j = 0; j < 3; j++) {
                            buttons[i][j].setEnabled(true);
                        }
                    }
                }
                else if((player1Turn == false && leader == true && order == true)
                        || (player1Turn && leader && !order) ||
                        (player1Turn == true && leader == false && order == true)
                        || (player1Turn == false && leader == false && order == false)){
                    for (int i = 0; i < 3; i++) {
                        for (int j = 0; j < 3; j++) {
                            buttons[i][j].setEnabled(false);
                        }
                    }
                }

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
            Toast.makeText(this, "Leave", Toast.LENGTH_SHORT).show();

            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if(leader){
            //Toast.makeText(this, code, Toast.LENGTH_SHORT).show();
            ref.child(code).removeValue();
        }
        else {
            ref.child(code).child("player").child("player2").removeValue();
        }
        Intent intent = new Intent(this,MainActivity.class);
        Toast.makeText(this, "Leave", Toast.LENGTH_SHORT).show();
        startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        if (!((Button) v).getText().toString().equals("")) {
            return;
        }
        int id = v.getId();
        String button = "";
        switch (id){
            case R.id.button_00:
                button = "button_00";
                break;
            case R.id.button_01:
                button = "button_01";
                break;
            case R.id.button_02:
                button = "button_02";
                break;
            case R.id.button_10:
                button = "button_10";
                break;
            case R.id.button_11:
                button = "button_11";
                break;
            case R.id.button_12:
                button = "button_12";
                break;
            case R.id.button_20:
                button = "button_20";
                break;
            case R.id.button_21:
                button = "button_21";
                break;
            case R.id.button_22:
                button = "button_22";
                break;
        }
        if (player1Turn && order) {
            ((Button) v).setText("X");
            ref.child(code).child("data").child("button").child(button).setValue("X");
        } else if(!player1Turn && order) {
            ((Button) v).setText("O");
            ref.child(code).child("data").child("button").child(button).setValue("O");
        }
        else if(player1Turn && !order){
            ((Button) v).setText("O");
            ref.child(code).child("data").child("button").child(button).setValue("O");
        }
        else {
            ((Button) v).setText("X");
            ref.child(code).child("data").child("button").child(button).setValue("X");
        }
        roundCount++;
        ref.child(code).child("data").child("roundcount").setValue(roundCount);
        if (checkForWin()) {
            if (player1Turn && order) {
                player1Wins();
            } else if(!player1Turn && order) {
                player2Wins();
            }
            else if (player1Turn && !order){
                player2Wins();
            }
            else {
                player1Wins();
            }
        } else if (roundCount == 9) {
            draw();
        } else {
            player1Turn = !player1Turn;
            ref.child(code).child("data").child("turn").child("player1turn").setValue(player1Turn);
        }
    }

    private boolean checkForWin() {
        String[][] field = new String[3][3];
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                field[i][j] = buttons[i][j].getText().toString();
            }
        }
        for (int i = 0; i < 3; i++) {
            if (field[i][0].equals(field[i][1])
                    && field[i][0].equals(field[i][2])
                    && !field[i][0].equals("")) {
                return true;
            }
        }
        for (int i = 0; i < 3; i++) {
            if (field[0][i].equals(field[1][i])
                    && field[0][i].equals(field[2][i])
                    && !field[0][i].equals("")) {
                return true;
            }
        }
        if (field[0][0].equals(field[1][1])
                && field[0][0].equals(field[2][2])
                && !field[0][0].equals("")) {
            return true;
        }
        if (field[0][2].equals(field[1][1])
                && field[0][2].equals(field[2][0])
                && !field[0][2].equals("")) {
            return true;
        }
        return false;
    }
    private void player1Wins() {
        player1Points++;
        player1Turn = true;
        winText = player1 + " Wins!";
        //Toast.makeText(this, "Player 1 wins!", Toast.LENGTH_SHORT).show();
        updatePointsText();
        resetBoard();
    }
    private void player2Wins() {
        player2Points++;
        player1Turn = true;
        winText = player2 + " Wins!";
        //Toast.makeText(this, "Player 2 wins!", Toast.LENGTH_SHORT).show();
        updatePointsText();
        resetBoard();
    }
    private void draw() {
        winText = "Draw!";
        //Toast.makeText(this, "Draw!", Toast.LENGTH_SHORT).show();
        drawCount++;
        updatePointsText();
        resetBoard();
    }
    private void updatePointsText() {
        gameCount++;
        //Toast.makeText(this, String.valueOf(gameCount), Toast.LENGTH_SHORT).show();
        ref.child(code).child("data").child("score").child("draw").setValue(drawCount);
        ref.child(code).child("data").child("score").child("gamecount").setValue(gameCount);
        gameCountTextView.setText("Round - "+gameCount);
        ref.child(code).child("data").child("score").child("player1points").setValue(player1Points);
        ref.child(code).child("data").child("score").child("player2points").setValue(player2Points);
        ref.child(code).child("data").child("score").child("text").setValue(winText);
        ref.child(code).child("data").child("turn").child("player1turn").setValue(true);
    }
    private void resetBoard() {
        order = !order;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                buttons[i][j].setText("");
            }
        }
        ref.child(code).child("data").child("button").child("button_00").setValue(buttons[0][0].getText().toString());
        ref.child(code).child("data").child("button").child("button_01").setValue(buttons[0][1].getText().toString());
        ref.child(code).child("data").child("button").child("button_02").setValue(buttons[0][2].getText().toString());
        ref.child(code).child("data").child("button").child("button_10").setValue(buttons[1][0].getText().toString());
        ref.child(code).child("data").child("button").child("button_11").setValue(buttons[1][1].getText().toString());
        ref.child(code).child("data").child("button").child("button_12").setValue(buttons[1][2].getText().toString());
        ref.child(code).child("data").child("button").child("button_20").setValue(buttons[2][0].getText().toString());
        ref.child(code).child("data").child("button").child("button_21").setValue(buttons[2][1].getText().toString());
        ref.child(code).child("data").child("button").child("button_22").setValue(buttons[2][2].getText().toString());

        roundCount = 0;
        ref.child(code).child("data").child("roundcount").setValue(roundCount);
        ref.child(code).child("data").child("score").child("draw").setValue(drawCount);
        ref.child(code).child("data").child("turn").child("order").setValue(order);
        player1Turn = true;
    }
    private void resetGame() {
        order = false;
        player1Points = 0;
        player2Points = 0;
        drawCount = 0;
        gameCount = 0;
        updatePointsText();
        resetBoard();
    }

}