package com.kob.backend.consumer;

import com.alibaba.fastjson.JSONObject;
import com.kob.backend.consumer.utils.Game;
import com.kob.backend.consumer.utils.JwtAuthentication;
import com.kob.backend.mapper.BotMapper;
import com.kob.backend.mapper.RecordMapper;
import com.kob.backend.mapper.UserMapper;
import com.kob.backend.pojo.Bot;
import com.kob.backend.pojo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.LinkedBlockingQueue;
@Component
@ServerEndpoint("/websocket/{token}")  // 注意不要以'/'结尾
public class WebSocketServer {
    private User user;
    // 用来存放每个用户对应的WebSocketServer对象
    public final static ConcurrentHashMap<Integer,WebSocketServer>
            userConnectionInfo = new ConcurrentHashMap<>();

    // 创建一个线程池
    private static ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(
            10, // 核心线程数
            20, // 最大线程数
            60, // 空闲线程存活时间
            TimeUnit.SECONDS,
            new LinkedBlockingQueue<Runnable>(100) // 任务队列
    );

    private Session session = null;// 这里的Session是websocket的一个组件

    public static UserMapper userMapper;
    private static BotMapper botMapper;
    public static RecordMapper recordMapper;
    public static RestTemplate restTemplate;

    public Game game = null;
    @Autowired
    public void setUserMapper(UserMapper userMapper){
        WebSocketServer.userMapper = userMapper;
    }

    @Autowired
    public void setBotMapper(BotMapper botMapper){
        WebSocketServer.botMapper = botMapper;
    }
    @Autowired
    public void setRecordMapper(RecordMapper recordMapper){
        WebSocketServer.recordMapper = recordMapper;
    }

    @Autowired
    public void setRestTemplate(RestTemplate restTemplate){
        WebSocketServer.restTemplate = restTemplate;
    }

    /**
     * 收到客户端消息后调用的方法
     * @param session 客户端的session
     * @param token token
     * @throws IOException 抛出异常
     */
    @OnOpen
    public void onOpen(Session session, @PathParam("token") String token) throws IOException {
        // 建立连接时自动调用
        this.session = session;
        System.out.println("Connected!");
        int userId = JwtAuthentication.getUserId(token);// 从token中获取用户id
        this.user = userMapper.selectById(userId);// 从数据库中获取用户信息
        if(this.user != null){
            userConnectionInfo.put(userId, this);// 将用户id和连接信息存入map中
        }
        else{
            this.session.close();// 如果用户不存在 则关闭连接
        }
        //System.out.println(this.user);
    }

    @OnClose
    public void onClose() {
        // 关闭链接时自动调用
        System.out.println("Disconnected!");
        if(this.user != null){
            userConnectionInfo.remove(this.user.getId());
        }
    }

    public static void startGame(Integer aId, Integer aBotId, Integer bId, Integer bBotId){
        User userA = userMapper.selectById(aId);
        User userB = userMapper.selectById(bId);
        Bot botA = botMapper.selectById(aBotId);
        Bot botB = botMapper.selectById(bBotId);
        Game game = new Game(
                13,
                14,
                20,
                userA.getId(),
                botA,
                userB.getId(),
                botB
                );
        game.createMap();

        //game是属于A和B两个玩家 因此需要赋值给A和B两名玩家对应的连接上
        if(userConnectionInfo.get(userA.getId()) != null)
            userConnectionInfo.get(userA.getId()).game = game;
        if(userConnectionInfo.get(userB.getId()) != null)
            userConnectionInfo.get(userB.getId()).game = game;

        // 在线程池中执行游戏
        threadPoolExecutor.execute(game);// 之前game.start();//开辟一个新的线程

        JSONObject respGame = new JSONObject();
        respGame.put("a_id",game.getPlayerA().getId());
        respGame.put("a_sx",game.getPlayerA().getSx());
        respGame.put("a_sy",game.getPlayerA().getSy());
        respGame.put("b_id",game.getPlayerB().getId());
        respGame.put("b_sx",game.getPlayerB().getSx());
        respGame.put("b_sy",game.getPlayerB().getSy());
        respGame.put("map",game.getG());//两名玩家的地图一致

        //分别给userA和userB传送消息告诉他们匹配成功了
        //通过userA的连接向userA发消息
        JSONObject respA = new JSONObject();
        respA.put("event","start-matching");// 事件类型
        respA.put("opponent_username",userB.getUsername());//对手的用户名
        respA.put("opponent_photo",userB.getPhoto());//对手的头像
        respA.put("game",respGame);//游戏信息
        WebSocketServer webSocketServer1 = userConnectionInfo.get(userA.getId());//获取user1的连接
        if(webSocketServer1 != null)
            webSocketServer1.sendMessage(respA.toJSONString());//向user1发送消息

        //通过userB的连接向userB发消息
        JSONObject respB = new JSONObject();
        respB.put("event","start-matching");
        respB.put("opponent_username",userA.getUsername());
        respB.put("opponent_photo",userA.getPhoto());
        respB.put("game",respGame);
        WebSocketServer webSocketServer2 = userConnectionInfo.get(userB.getId());
        if(webSocketServer2 != null)
            webSocketServer2.sendMessage(respB.toJSONString());
    }

    private final static String addPlayerUrl = "http://127.0.0.1:3001/player/add/";
    private final static String removePlayerUrl = "http://127.0.0.1:3001/player/remove/";

    private void startMatching(Integer botId){
        System.out.println("start matching!");
        MultiValueMap<String, String> data = new LinkedMultiValueMap<>();
        data.add("user_id",this.user.getId().toString());
        data.add("rating",this.user.getRating().toString());
        data.add("bot_id", botId.toString());
        //向MatchingSystem发请求
        restTemplate.postForObject(addPlayerUrl,data,String.class);
    }
    private void stopMatching(){
        System.out.println("stop matching!");
        MultiValueMap<String, String> data = new LinkedMultiValueMap<>();
        data.add("user_id",this.user.getId().toString());
        restTemplate.postForObject(removePlayerUrl,data,String.class);
    }

    private void move(Integer direction) {
        //判断是A玩家还是B玩家在操作
        if(game.getPlayerA().getId().equals(user.getId())){
            if(game.getPlayerA().getBotId().equals(-1))
                game.setNextStepA(direction);
        }else if (game.getPlayerB().getId().equals(user.getId())) {
            if(game.getPlayerB().getBotId().equals(-1))
                game.setNextStepB(direction);
        }
    }
    @OnMessage
    public void onMessage(String message, Session session) {//当做路由 分配任务
        // Server从Client接收消息时触发
        System.out.println("Receive message!");
        JSONObject data = JSONObject.parseObject(message);//将字符串解析成JSON
        System.out.println(data);
        String event = data.getString("event");
        if("start-matching".equals(event)){//防止event为空的异常
            startMatching(data.getInteger("bot_id"));
        } else if ("stop-matching".equals(event)) {
            stopMatching();
        } else if ("move".equals(event)) {
            Integer direction = data.getInteger("direction");
            System.out.println(direction);
            move(direction);
        }
    }

    @OnError
    public void onError(Session session, Throwable error) {
        error.printStackTrace();
    }

    // 向客户端发送消息
    public void sendMessage(String message){
        synchronized (this.session){//同步
            try{
                this.session.getBasicRemote().sendText(message);//发送消息
            }catch (IOException e){
                e.printStackTrace();
            }
        }
    }
}