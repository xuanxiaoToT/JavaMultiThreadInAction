/*
授权声明：
本源码系《Java多线程编程实战指南（核心篇）》一书（ISBN：978-7-121-31065-2，以下称之为“原书”）的配套源码，
欲了解本代码的更多细节，请参考原书。
本代码仅为原书的配套说明之用，并不附带任何承诺（如质量保证和收益）。
以任何形式将本代码之部分或者全部用于营利性用途需经版权人书面同意。
将本代码之部分或者全部用于非营利性用途需要在代码中保留本声明。
任何对本代码的修改需在代码中以注释的形式注明修改人、修改时间以及修改内容。
本代码可以从以下网址下载：
https://github.com/Viscent/javamtia
http://www.broadview.com.cn/31065
*/
package io.github.viscent.mtia.ch5;

import io.github.viscent.mtia.util.Debug;
import io.github.viscent.mtia.util.Tools;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

/**
 * 清单 5-8 CyclicBarrie 使用示例
 * <p>
 * 该例子模拟了士兵参与打靶训练 。 所有参与训练的士兵（ Soldier)
 * 被分为若干组（ Rank ），其中每组被称为一排 。 一排中士兵的个数等于靶子的个数 。 每次
 * 只能够有一排士兵进行射击 。 一排中的士兵必须同时开始射击，并且射击完毕的士兵必须
 * 等待同排的其他所有士兵射击完毕后才能够整排地撤离射击点 。 一排中的士兵射击结束后
 * 腾出射击点和靶子，换另外一排中的士兵进行下一轮射击，如此交替进行，直到训练时间
 * 结束
 */
public class ShootPractice {
    // 参与打靶训练的全部士兵
    final Soldier[][] rank;
    // 靶的个数，即每排中士兵的个数
    final int N;
    // 打靶持续时间（单位：秒）
    final int lasting;
    // 标识是否继续打靶
    volatile boolean done = false;
    // 用来指示进行下一轮打靶的是哪一排的士兵
    volatile int nextLine = 0;
    // shiftBarrier 用于实现当前排的士兵在该排所有士兵射击完毕后同时撤离打靶位置
    final CyclicBarrier shiftBarrier;
    // startBarrier 用于实现当前排的士兵在同一时刻开始射击
    final CyclicBarrier startBarrier;

    public ShootPractice(int N, final int lineCount, int lasting) {
        this.N = N;
        this.lasting = lasting;
        this.rank = new Soldier[lineCount][N];
        for (int i = 0; i < lineCount; i++) {
            for (int j = 0; j < N; j++) {
                rank[i][j] = new Soldier(i * N + j);
            }
        }
        // barrierAction 会被最后一个线程 执行CyclicBarrier.await 方法时执行，该任务执行结束后其他等待线程才会被唤醒 。
        shiftBarrier = new CyclicBarrier(N, new Runnable() {
            @Override
            public void run() {
                // 更新下一轮打靶的排
                nextLine = (nextLine + 1) % lineCount;// 语句①
                Debug.info("Next turn is :%d", nextLine);
            }
        });
        // 语句②
        startBarrier = new CyclicBarrier(N);
    }

    public void start() throws InterruptedException {
        // 创建并启动工作者线程
        Thread[] threads = new Thread[N];
        for (int i = 0; i < N; ++i) {
            threads[i] = new Shooting(i);
            threads[i].start();
        }
        // 指定时间后停止打靶
        Thread.sleep(lasting * 1000);
        stop();
        for (Thread t : threads) {
            t.join();
        }
        Debug.info("Practice finished.");
    }

    public void stop() {
        done = true;
    }

    class Shooting extends Thread {
        final int index;

        public Shooting(int index) {
            this.index = index;
        }

        @Override
        public void run() {
            Soldier soldier;
            try {
                while (!done) {
                    soldier = rank[nextLine][index];
                    // 一排中的士兵必须同时开始射击
                    startBarrier.await();// 语句③
                    // 该士兵开始射击
                    soldier.fire();
                    // 一排中的士兵必须等待该排中的所有其他士兵射击完毕才能够离开射击点
                    shiftBarrier.await();// 语句④
                }
            } catch (InterruptedException e) {
                // 什么也不做
            } catch (BrokenBarrierException e) {
                e.printStackTrace();
            }

        }// run方法结束
    }// 类Shooting定义结束

    // 参与打靶训练的士兵
    static class Soldier {
        private final int seqNo;

        public Soldier(int seqNo) {
            this.seqNo = seqNo;
        }

        public void fire() {
            Debug.info(this + " start firing...");
            Tools.randomPause(5000);
            System.out.println(this + " fired.");
        }

        @Override
        public String toString() {
            return "Soldier-" + seqNo;
        }

    }// 类Soldier定义结束


    public static void main(String[] args) throws InterruptedException {
        ShootPractice sp = new ShootPractice(4, 5, 24);
        sp.start();
    }
}