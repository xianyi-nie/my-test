package com.kob.botrunningsystem.service.impl.utils;

import org.joor.Reflect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.UUID;
import java.util.function.Supplier;

@Component
public class Consumer extends Thread{
    private Bot bot;
    private static RestTemplate restTemplate;

    @Autowired
    public void setRestTemplate (RestTemplate restTemplate){
        Consumer.restTemplate = restTemplate;
    }

    public void startTimeout(long timeout, Bot bot){
        this.bot = bot;
        this.start();//调用start()会开辟一个新线程来执行run()
        //当前线程会继续执行
        try {
            this.join(timeout);//最多等待timeout秒
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            this.interrupt();//一旦线程执行完毕 或者超过timeout秒 终端当前线程
        }
    }

    private String addUid(String code, String uid) {  // 在code中的Bot类名后添加uid
        int k = code.indexOf(" implements java.util.function.Supplier<Integer>");
        return code.substring(0, k) + uid + code.substring(k);
    }
    private final static String receiveBotMoveUrl = "http://127.0.0.1:3000/pk/receive/bot/move/";
    @Override
    public void run() {
        UUID uuid = UUID.randomUUID();
        //类名之前添加一个随机字符串
        String uid = uuid.toString().substring(0, 8);
        Supplier<Integer> botInterface = Reflect.compile(//用反射思想，在运行时动态编译代码
                "com.kob.botrunningsystem.utils.Bot" + uid,//参数1 类名
                addUid(bot.getBotCode(), uid)// 参数2 代码
        ).create().get();//创建一个Bot的实例

        // 将输入写入文件
        File file = new File("input.txt");
        try (PrintWriter fout = new PrintWriter(file)){// 创建文件输出流
            fout.println(bot.getInput());// 将输入写入文件
            fout.flush();// 刷新缓冲区
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        Integer direction = botInterface.get();//调用bot的get()方法

        System.out.println("bot-userId " + bot.getUserId() + "  move direction " + direction);

        MultiValueMap<String, String> data = new LinkedMultiValueMap<>();
        data.add("user_id", bot.getUserId().toString());// 将bot对应的用户id传给后端
        data.add("direction", direction.toString()); // 将bot的移动方向传给后端
        restTemplate.postForObject(receiveBotMoveUrl, data, String.class);
    }
}
