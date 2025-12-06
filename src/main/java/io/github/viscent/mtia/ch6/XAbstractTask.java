package io.github.viscent.mtia.ch6;

import java.util.HashMap;

/**
 * 清单 6-9 避免 ThreadLocal 可能导致的数据错乱
 */
public abstract class XAbstractTask implements Runnable {
    static ThreadLocal<HashMap<String, String>> configHolder = new
            ThreadLocal<HashMap<String, String>>() {
                protected HashMap<String, String> initialValue() {
                    return new HashMap<String, String>();
                }
            };

    // 该方法总是会在任务处理逻辑被执行前执行
    protected void preRun() {
        // 清空线程特有对象HashMap实例，以保证每个任务执行前HashMap的内容是“干净”的
        configHolder.get().clear();
    }

    protected void postRun() {
        // 什么也不做
    }

    // 暴露给子类用于实现任务处理逻辑
    protected abstract void doRun();

    @Override
    public final void run() {
        try {
            preRun();
            doRun();
        } finally {
            postRun();
        }
    }
}