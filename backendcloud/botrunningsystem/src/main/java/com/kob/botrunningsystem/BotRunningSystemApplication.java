package com.kob.botrunningsystem;

import com.kob.botrunningsystem.service.impl.BotRunningServiceImpl;
import com.kob.botrunningsystem.service.impl.utils.BotPool;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BotRunningSystemApplication {
    public static void main(String[] args) {
        BotRunningServiceImpl.botPool.start();// 启动botPool线程
        BotPool botPool = new BotPool();
        new Thread(botPool::startBotProcessing).start(); // 启动Bot处理
        SpringApplication.run(BotRunningSystemApplication.class,args);
    }
}