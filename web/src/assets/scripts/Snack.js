import { GameObject } from "./GameObject";
import { Cell } from "./Cell";

export class Snake extends GameObject{
    constructor(info, gamemap){
        super();

        this.id = info.id;
        this.color = info.color;
        this.gamemap = gamemap;

        this.cells = [new Cell(info.r, info.c)]; //存放蛇的身体 cells[0]存放蛇头
        this.speed = 5;//蛇每秒走多少个格子

        this.direction = -1;//-1表示没有指令，0、1、2、3表示上右下左
        this.status = "idle";//idle表示静止 move表示正在移动 die表示死亡

        this.dr = [-1, 0, 1, 0]; //四个方向行的偏移量
        this.dc = [0, 1, 0, -1]; //四个方向列的偏移量

        this.step = 0;//表示回合数 蛇的增长与回合有关
        this.eps = 1e-2;//允许的误差

        //初始化蛇眼睛方向
        if(this.id == 0) this.eye_direction = 0;//左上角蛇眼初始化朝上
        if(this.id == 1) this.eye_direction = 2;//右下角蛇眼的初始化朝下

        this.eye_dx = [//蛇眼睛在x轴不同方向的偏移量
            [-1,1],
            [1,1],
            [1,-1],
            [-1,-1]
        ];
        this.eye_dy = [//蛇眼睛在y轴不同方向的偏移量
            [-1,-1],
            [-1,1],
            [1,1],
            [1,-1]
        ];

    }
    start(){

    }

    //设置direction
    //可以通过键盘 后续也可以通过后端给前端发信息来设置
    set_direction(d){
        this.direction = d;
    }

    check_tail_increasing(){//检测当前回合 蛇的长度是否增加
        if(this.step <= 10) return true;
        if(this.step % 3 === 1) return true;
        return false;
    }
    next_step(){//将蛇的状态变为走下一步
        const d = this.direction;
        //目标点坐标 蛇头将要更新的位置
        this.next_cell = new Cell (this.cells[0].r + this.dr[d], this.cells[0].c + this.dc[d]);
        //更新对蛇眼睛方向
        this.eye_direction = d

        this.direction = -1;//清空操作
        this.status = "move";
        this.step++;

        const k = this.cells.length;
        //蛇的前进是新增头元素（有时砍掉尾元素的过程） 而新增头元素，剩余其他的下标顺序都要往后移动
        for(let i = k; i > 0; i--){
            this.cells[i] = JSON.parse(JSON.stringify(this.cells[i-1]));
        }
    }

    update_move(){
        //考虑一种一般情况 即目标(next_cell)与蛇头(cells[0])位置任意
        const dx = this.next_cell.x - this.cells[0].x;
        const dy = this.next_cell.y - this.cells[0].y;
        const distance = Math.sqrt(dx * dx, dy * dy);

        if(distance < this.eps){
            this.cells[0] = this.next_cell;//添加一个新蛇头
            this.next_cell = null;
            this.status = "idle";//走完了 停下来

            if(!this.check_tail_increasing()){
                this.cells.pop();//如果蛇不变长 且已经移动过来了 就把蛇尾砍掉
            }
        }else{   
            //根据速度 得出每帧移动多少步=速度 * 时间(换算成second)        
            const move_distance = this.speed * this.timedelta / 1000;//两帧之间走的距离
            this.cells[0].x += move_distance * dx / distance; // x = d * cosθ
            this.cells[0].y += move_distance * dy / distance; // y = d * sinθ

            if(this.check_tail_increasing()){//如果蛇不变长 还要解决蛇尾
                const k = this.cells.length;
                //蛇尾 和 蛇尾的目标位置
                const tail = this.cells[k - 1], tail_target = this.cells[k-2];
                const tail_dx = tail_target.x - tail.x;
                const tail_dy = tail_target.y - tail.y;
                tail.x += move_distance * tail_dx / distance;
                tail.y += move_distance * tail_dy / distance;
            }
        }
    }
    //每帧执行一次
    update(){
        if(this.status === "move"){
            console.log("move");
            this.update_move();
        }
            
        this.render();
    }

    render() {
        const L = this.gamemap.L;//取出单元格边长
        const ctx = this.gamemap.ctx;//取出画布引用

        ctx.fillStyle = this.color;
        if(this.status === "die"){
            ctx.fillStyle = "white";
        }


        for(const cell of this.cells){//of表示取出对象 in表示取出下标
            ctx.beginPath();
            ctx.arc(cell.x * L, cell.y * L, L/2 * 0.8, 0, Math.PI * 2);//圆心坐标 半径 起始点角度
            ctx.fill();
        }

        for(let i = 1; i < this.cells.length; i++){
            const a = this.cells[i-1], b = this.cells[i];
            if(Math.abs(a.x - b.x) < this.eps && Math.abs(a.y, b.y) < this.eps)
                continue;
            if(Math.abs(a.x - b.x) < this.eps){
                ctx.fillRect((a.x - 0.4) * L, Math.min(a.y, b.y)*L, L*0.8, Math.abs(a.y - b.y) * L);
            }else{
                ctx.fillRect(Math.min(a.x, b.x)*L, (a.y - 0.4) * L, Math.abs(a.x - b.x) * L, L * 0.8);
            }
        }

        ctx.fillStyle = "black";
        //i从0到1表示两个眼睛
        for(let i = 0; i < 2; i++){
            const exy_x = (this.cells[0].x + this.eye_dx[this.eye_direction][i] * 0.15) * L;
            const exy_y = (this.cells[0].y + this.eye_dy[this.eye_direction][i] * 0.15) * L;

            ctx.beginPath();
            ctx.arc(exy_x, exy_y, L*0.05, 0, Math.PI*2);
            ctx.fill();
        }
    }
}