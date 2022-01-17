package com.velocia.javalab.멀티쓰레딩.동시접근문제;

import java.util.concurrent.atomic.AtomicInteger;

public class Solution {
    private static final AtomicInteger t = new AtomicInteger();
    public static void main(String[] args) {
        for (int i = 0; i < 100; i++) {
            new Thread(() -> {
                for (int j = 0; j < 1000; j++)
                    System.out.println(t.incrementAndGet());
            }).start();
        }
    }
}
