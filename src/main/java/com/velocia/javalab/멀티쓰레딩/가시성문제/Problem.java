package com.velocia.javalab.멀티쓰레딩.가시성문제;

import java.util.concurrent.TimeUnit;

public class Problem {
    private static boolean stopRequested;

    public static void main(String[] args) throws InterruptedException {
        Thread background = new Thread(() -> {
            for (int i = 0; !stopRequested ; i++);
            System.out.println("background 쓰레드가 종료되었습니다!");
        });
        background.start();

        TimeUnit.SECONDS.sleep(1);
        stopRequested = true;
        System.out.println("main 쓰레드가 종료되었습니다!");
    }
}