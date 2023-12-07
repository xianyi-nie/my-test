package com.kob.botrunningsystem.service.impl.utils;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class BotPool extends Thread{
    private static final ReentrantLock lock = new ReentrantLock();
    private final Condition condition = lock.newCondition();
    private final Queue<Bot> bots = new LinkedList<>();
    private final ExecutorService botProcessingExecutor = Executors.newFixedThreadPool(10); // 创建一个固定大小的线程池

    public void addBot(Integer userId, String botCode, String input){
        lock.lock();
        try{
            bots.add(new Bot(userId, botCode, input));
            condition.signalAll();
        }finally {
            lock.unlock();
        }
    }
    private void consum(Bot bot){
        Consumer consumer = new Consumer();
        consumer.startTimeout(2000,bot);//最多执行2s
    }
    // 之前这里是run方法
    public void startBotProcessing() {
        while (true){ //一直循环
            lock.lock();
            if(bots.isEmpty()){
                try {
                    condition.await();//阻塞线程 锁自动释放
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    lock.unlock();
                    break;
                }
            }else {
                Bot bot = bots.remove();//返回并删除队头
                lock.unlock();
                // consum(bot);//编译并执行代码 比较耗时 可能会执行几秒钟
                // 使用线程池来执行
                botProcessingExecutor.submit(() -> consum(bot)); // 将任务提交到线程池中执行
            }
        }
    }

}
