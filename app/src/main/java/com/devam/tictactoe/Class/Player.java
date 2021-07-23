package com.devam.tictactoe.Class;

public class Player {
    String name;
    int score;
    boolean ready,leader,turn;

    public Player(String name, int score) {
        this.name = name;
        this.score = score;
    }

    public Player(String name, int score, boolean ready, boolean leader,boolean turn) {
        this.name = name;
        this.score = score;
        this.turn = turn;
        this.ready = ready;
        this.leader = leader;
    }
    public Player(){

    }

    public boolean isReady() {
        return ready;
    }

    public boolean isTurn() {
        return turn;
    }

    public void setTurn(boolean turn) {
        this.turn = turn;
    }

    public void setReady(boolean ready) {
        this.ready = ready;
    }

    public boolean isLeader() {
        return leader;
    }

    public void setLeader(boolean leader) {
        this.leader = leader;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }
}
