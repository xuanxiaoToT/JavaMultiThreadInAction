package io.github.viscent.mtia.ch1;

import io.github.viscent.mtia.util.Tools;

/**
 * @author 80296347/高登峰
 * @CreateDate 2022/11/30
 * <p>
 * 线程的两种创建方式的区别
 */
public class ThreadCreationCmp {

    public static void main(String[] args) {

        Thread t;
        CountingTask ct = new CountingTask();
        final int numberOfProceesors = Runtime.getRuntime().availableProcessors();

        System.out.println("numberOfProceesors: " + numberOfProceesors);
        for (int i = 0; i < 2 * numberOfProceesors; i++) {
            // 直接创建线程
            // 注意：ct是共用的，这意味着内部的count也是共用的。
            // t = new Thread(ct);
            t = new Thread(new CountingTask());
            t.start();
        }
        // for (int i = 0; i < 2 * numberOfProceesors; i++) {
        //     // 以子类的方式创建线程
        //     t = new CountingThread();
        //     t.start();
        // }
    }

    static class Counter {
        private int count = 0;

        public void increment() {
            count++;
        }

        public int value() {
            return count;
        }
    }

    static class CountingTask implements Runnable {
        private Counter counter = new Counter();

        @Override
        public void run() {
            for (int i = 0; i < 100; i++) {
                doSomething();
                counter.increment();
            }
            System.out.println("CountingTask:" + counter.value());
        }

        private void doSomething() {
            // 使当前线程休眠随机时间
            Tools.randomPause(80);
        }

    }

    static class CountingThread extends Thread {
        private Counter counter = new Counter();

        @Override
        public void run() {
            for (int i = 0; i < 100; i++) {
                doSomething();
                counter.increment();
            }
            System.out.println("CountingThread:" + counter.value());
        }

        private void doSomething() {
            // 使当前线程休眠随机时间
            Tools.randomPause(80);
        }
    }
}
