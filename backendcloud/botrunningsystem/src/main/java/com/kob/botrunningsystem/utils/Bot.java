//Bot代码的编写 不在此处执行 只是为了方便书写
package com.kob.botrunningsystem.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Bot implements java.util.function.Supplier<Integer>{
    @Override
    public Integer get() {
        File file = new File("input.txt");
        try {
            Scanner sc = new Scanner(file);
            return nextMove(sc.next());
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static class Cell{
        private final int x;
        private final int y;
        public Cell(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }

    //检验当前回合 蛇的长度是否增加
    private  boolean check_tail_increasing(int step){
        if(step <= 10) return true;
        else return step % 3 == 1;
    }
    //返回蛇的身体
    public List<Cell> getCells(int sx, int sy, String steps){
        steps = steps.substring(1, steps.length() - 1);
        List<Cell> res = new ArrayList<>();
        //对于四种操作0(w), 1(d), 2(s), 3(a)
        // 在行和列方向上的计算偏移量
        int[] dx = {-1, 0, 1, 0};
        int[] dy = {0, 1, 0, -1};
        int x = sx;
        int y = sy;
        int step = 0;//回合数
        char[] snacksteps = steps.toCharArray();
        res.add(new Cell(x,y));//添加起点
        //不断根据steps计算出整个蛇身体
        for (Character d : snacksteps) {
            x += dx[d - '0'];
            y += dy[d - '0'];
            res.add(new Cell(x,y));
            if(!check_tail_increasing(++step)){
                //如果蛇尾不增加 就删掉蛇尾
                res.remove(0);//O(N)
            }
        }
        return res;
    }

    public Integer nextMove(String input) {
        // 对input解码
        String[] str = input.split("#");
        String map = str[0];// 取出地图
        int aSx = Integer.parseInt(str[1]), aSy = Integer.parseInt(str[2]);//取出我方起点坐标
        String aSteps = str[3];// 取出我方操作
        int bSx = Integer.parseInt(str[4]), bSy = Integer.parseInt(str[5]);//取出对手起点坐标
        String bSteps = str[6];// 取出对手操作
        // 取出地图
        int[][] g = new int[13][14];
        int k = 0;
        for (int i = 0; i < 13; i++) {
            for (int j = 0; j < 14; j++) {
                if(map.charAt(k) == '1')
                    g[i][j] = 1;
                k++;
            }
        }
        //取出蛇的轨迹
        List<Cell> aCells = getCells(aSx, aSy, aSteps);
        List<Cell> bCells = getCells(bSx, bSy, bSteps);

        for(Cell c : aCells) g[c.x][c.y] = 1;
        for (Cell c : bCells) g[c.x][c.y] = 1;

        // 判断可行的移动方向
        // 对于四种方向0(↑), 1(→), 2(↓), 3(←)
        // 在行和列方向上的计算偏移量
        int[] dx = {-1, 0, 1, 0};
        int[] dy = {0, 1, 0, -1};

        for (int i = 0; i < 4; i++) {
            int x = aCells.get(aCells.size() - 1).x + dx[i];//下一处x
            int y = aCells.get(aCells.size() - 1).y + dy[i];//下一处y
            if(x >= 0 && x < 13 && y >= 0 && y < 14 && g[x][y] == 0)
                return i;
        }
        return 0;//如果没有可行的方向 向上走--灭亡
    }
}
