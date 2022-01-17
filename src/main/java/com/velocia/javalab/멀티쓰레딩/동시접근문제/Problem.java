package com.velocia.javalab.멀티쓰레딩.동시접근문제;

public class Problem {
    private static int t;
    public static void main(String[] args) {
        for (int i = 0; i < 100; i++) {
            new Thread(() -> {
                for (int j = 0; j < 1000; j++)
                    System.out.println(t++);
            }).start();
        }
    }
}
