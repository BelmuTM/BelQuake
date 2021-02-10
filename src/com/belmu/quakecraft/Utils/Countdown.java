package com.belmu.quakecraft.Utils;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.function.Consumer;

public class Countdown implements Runnable {

    // I DID NOT MAKE THIS CLASS.

    private JavaPlugin plugin;

    private Integer assignedTaskId;

    private double seconds;
    private double secondsLeft;

    private Consumer<Countdown> everySecond;
    private Runnable beforeTimer;
    private Runnable afterTimer;

    public Countdown(JavaPlugin plugin, double seconds,
                            Runnable beforeTimer, Runnable afterTimer,
                            Consumer<Countdown> everySecond) {

        this.plugin = plugin;

        this.seconds = seconds;
        this.secondsLeft = seconds;

        this.beforeTimer = beforeTimer;
        this.afterTimer = afterTimer;
        this.everySecond = everySecond;
    }

    @Override
    public void run() {

        if(isInterrupted()) return;

        if(secondsLeft < 1) {
            afterTimer.run();

            if(assignedTaskId != null) Bukkit.getScheduler().cancelTask(assignedTaskId);
            return;
        }
        if(secondsLeft == seconds) beforeTimer.run();
        everySecond.accept(this);

        secondsLeft--;
    }

    public double getTotalSeconds() { return seconds; }
    public double getSecondsLeft() { return secondsLeft; }
    public void setSecondsLeft(double seconds) { secondsLeft = seconds; }

    public void scheduleTimer() {
        this.assignedTaskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, this, 0L, 20L);
    }

    public boolean isInterrupted() {
        return Bukkit.getScheduler().isCurrentlyRunning(assignedTaskId);
    }

    public void interrupt(boolean bool) {
        if(bool) Bukkit.getScheduler().cancelTask(assignedTaskId);
    }

}
