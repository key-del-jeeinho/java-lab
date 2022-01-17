package com.velocia.javalab.멀티쓰레딩.가시성문제;

import java.util.concurrent.TimeUnit;

public class Solution {
    private static volatile boolean stopRequested; //무조건 메인 메모리에서 가져온다.

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
