package com.chs.pedometer;

public class Stopwatch {

    private long startTime = 0;
    private long stopTime = 0;
    private boolean running = false;


    public void start() {
        this.startTime = System.currentTimeMillis();
        this.running = true;
    }


    public void stop() {
        this.stopTime = System.currentTimeMillis();
        this.running = false;
    }


    // elaspsed time in seconds
    public long getElapsedTime() {
        if (running) {
            return ((System.currentTimeMillis() - startTime) / 1000);
        }
        return ((stopTime - startTime) / 1000);
    }

    public String ToString()
    {
        return this.getElapsedTime()/3600 + " h  " + (this.getElapsedTime()%3600)/60 + " m  " + this.getElapsedTime()%60 + " s";
    }
}