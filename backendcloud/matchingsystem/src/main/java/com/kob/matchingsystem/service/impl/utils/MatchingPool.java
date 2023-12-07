package com.kob.matchingsystem.service.impl.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.concurrent.locks.ReentrantLock;
@Component
public class MatchingPool extends Thread{
    private static List<Player> players = new ArrayList<>();
    // 2023-04-07 优先队列存储玩家 按照等待时间排序
    // private static Queue<Player> players = new PriorityQueue<>((player1, player2) -> player2.getWaitingTime() - player1.getWaitingTime());
    private ReentrantLock lock = new ReentrantLock();
    private static RestTemplate restTemplate;
    @Autowired
    public void setRestTemplate(RestTemplate restTemplate){
        MatchingPool.restTemplate = restTemplate;
    }
    public void addPlayer(Integer userId, Integer rating, Integer botId){
        lock.lock();
        try{
            players.add(new Player(userId,rating,botId,0));
        }finally {
            lock.unlock();
        }
    }
    public void removePlayer (Integer userId){
        lock.lock();
        try {
            for (int i = 0; i < players.size(); i++) {
                if(players.get(i).getUserId().equals(userId)){
                    players.remove(i);
                    i--;//注意指针要回退一下 这样i++之后重新判断这一位置上
                }
            }
        }finally {
            lock.unlock();
        }
    }
    private void increaseWaitingTime(){//将所有当前玩家的等待时间+1
        for (Player player : players) {
            player.setWaitingTime(player.getWaitingTime() + 1);
        }
    }
    private boolean checkMatched(Player a, Player b){//判断两名玩家是否匹配

        int ratingDelta = Math.abs(a.getRating() - b.getRating());
        //对a来说:与a的分值差距ratingDelta <= a.getWaitingTime() * 10
        //对b来说:与b的分值差距ratingDelta <= b.getWaitingTime() * 10
        //两情相悦取最小值
        int waitingTime = Math.min(a.getWaitingTime(), b.getWaitingTime());
        return ratingDelta <= waitingTime * 10;
    }
    private final static String startGameUrl = "http://127.0.0.1:3000/pk/start/game/";
    private void sendResult(Player a, Player b){//若a和b匹配,则作为参数返回到backend
        System.out.println("send Result " + a +" " + b);
        MultiValueMap<String,String> data = new LinkedMultiValueMap<>();
        data.add("a_id",a.getUserId().toString());
        data.add("a_bot_id",a.getBotId().toString());
        data.add("b_id", b.getUserId().toString());
        data.add("b_bot_id", b.getBotId().toString());
        // 用restTemplate调用backend的接口,将匹配结果返回
        restTemplate.postForObject(startGameUrl, data, String.class);
    }

    // 时间复杂度O(n^2)
    private void matchPlayers(){//尝试匹配所有玩家
        System.out.println("match players" + players.toString());
        boolean[] used = new boolean[players.size()];//bool数组存储哪些玩家已经有匹配结果了
        // 优先匹配等待时间比较长的玩家
        // 对玩家列表按等待时间降序排序，等待时间较长的玩家排在前面
        players.sort((player1, player2) -> player2.getWaitingTime() - player1.getWaitingTime());
        // 也就是最早add的那一批开始考虑
        for (int i = 0; i < players.size(); i++) {
            if(used[i]) continue;
            for (int j = i + 1; j < players.size(); j++) {
                if(used[j]) continue;
                Player a = players.get(i);
                Player b = players.get(j);
                if(checkMatched(a,b)){
                    used[i] = used[j] = true;
                    sendResult(a,b);
                    break;
                }
            }
        }
        //更新players 删除已经匹配过的玩家
        for (int i = 0; i < players.size(); i++) {
            if(used[i]){
                players.remove(i);
                i--;
            }
        }
    }
//    // 2023-04-07 时间复杂度O(nlogn)
//    private void matchPlayers() {//尝试匹配所有玩家
//        System.out.println("match players" + players.toString());
//
//        Queue<Player> matchedPlayers = new LinkedList<>();
//
//        while (!players.isEmpty()) {
//            Player a = players.poll();
//            Player b = null;
//            Iterator<Player> iterator = players.iterator();
//
//            while (iterator.hasNext()) {
//                Player candidate = iterator.next();
//                if (checkMatched(a, candidate)) {
//                    b = candidate;
//                    iterator.remove();
//                    break;
//                }
//            }
//
//            if (b != null) {
//                sendResult(a, b);
//            } else {
//                a.setWaitingTime(a.getWaitingTime() + 1);
//                matchedPlayers.offer(a);
//            }
//        }
//
//        players = matchedPlayers;
//    }


    @Override
    public void run() {
        while (true){
            try {
                Thread.sleep(1000);//每隔一秒钟
                //increaseWaitingTime和matchPlayers都会操纵players
                //可能会产生读写冲突 因此加锁
                lock.lock();
                try {
                    increaseWaitingTime();
                    matchPlayers();
                }finally {
                    lock.unlock();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
                break;
            }
        }
    }
}
