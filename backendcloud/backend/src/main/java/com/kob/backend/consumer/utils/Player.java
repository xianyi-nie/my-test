package com.kob.backend.consumer.utils;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Player {
    private Integer id;
    private Integer botId;//-1表示人工操作 否则表示用AI打
    private String botCode;
    private Integer sx;//起始x坐标
    private Integer sy;//起始y坐标
    private List<Integer> steps;//保存每一步操作---决定了蛇当前的形状

    //将steps转换为字符串
    public String getStepsString(){
        StringBuilder res = new StringBuilder();
        for (Integer d : steps) {
            res.append(d);
        }
        return res.toString();
    }
    //检验当前回合 蛇的长度是否增加
    private  boolean check_tail_increasing(int step){
        if(step <= 10) return true;
        else return step % 3 == 1;
    }
    //返回蛇的身体
    public List<Cell> getCells(){
        List<Cell> res = new ArrayList<>();
        //对于四种操作0(w), 1(d), 2(s), 3(a)
        // 在行和列方向上的计算偏移量
        int[] dx = {-1, 0, 1, 0};
        int[] dy = {0, 1, 0, -1};
        int x = sx;
        int y = sy;
        int step = 0;//回合数
        res.add(new Cell(x,y));//添加起点
        //不断根据steps计算出整个蛇身体
        for (Integer d : steps) {
            x += dx[d];
            y += dy[d];
            res.add(new Cell(x,y));
            if(!check_tail_increasing(++step)){
                //如果蛇尾不增加 就删掉蛇尾
                res.remove(0);//O(N)
            }
        }
        return res;
    }
}
