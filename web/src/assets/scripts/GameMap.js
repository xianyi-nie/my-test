//GameMap.js
import { GameObject } from "./GameObject";
import { Snake } from "./Snack";
import { Wall } from "./Wall"
export class GameMap extends GameObject {
    constructor(ctx, parent, store) {
        super();

        this.ctx = ctx;
        this.parent = parent;
        this.store = store;
        this.L = 0;
        this.rows = 13;
        this.cols = 14;
        this.inner_walls_count = 10;//定义内部障碍物数量
        this.walls = [];//用于保存障碍物,属于对象数组

        this.snakes = [
            new Snake({ id: 0, color: "#4876EC", r: this.rows - 2, c: 1 }, this),
            new Snake({ id: 1, color: "#F94848", r: 1, c: this.cols - 2 }, this),
        ];
    }



    //画地图:创建障碍物
    create_walls() {
        //直接将地图取出--后端传过来
        console.log(this.store)
        const g = this.store.state.pk.gamemap;
        //创建障碍物对象 并添加到this.walls数组
        for (let r = 0; r < this.rows; r++) {
            for (let c = 0; c < this.cols; c++) {
                if (g[r][c]) {
                    this.walls.push(new Wall(r, c, this));
                }
            }
        }
    }

    add_listening_events() {
        if (this.store.state.record.is_record) {
            let k = 0;
            const a_steps = this.store.state.record.a_steps;
            const b_steps = this.store.state.record.b_steps;
            const loser = this.store.state.record.record_loser;
            const [snake0, snake1] = this.snakes;
            const interval_id = setInterval(() => {
                if(k >= a_steps.length-1){
                    if(loser === "all" || loser === "A"){
                        snake0.status = "die";
                    }
                    if(loser === "all" || loser === "B"){
                        snake1.status = "die";
                    }
                    clearInterval(interval_id);
                }else{
                    snake0.set_direction(parseInt(a_steps[k]));
                    snake1.set_direction(parseInt(b_steps[k]));
                }
                k++;
           }, 300);//300ms执行一次
        } else {
            this.ctx.canvas.focus();//聚焦
            //const [snake0, snake1] = this.snakes;
            this.ctx.canvas.addEventListener("keydown", e => {
                //console.log(e.key);
                //wasd控制移动
                let d = -1;
                if (e.key === 'w') d = 0;
                else if (e.key === 'd') d = 1;
                else if (e.key === 's') d = 2;
                else if (e.key === 'a') d = 3;

                if (d >= 0) {//有效输入
                    this.store.state.pk.socket.send(JSON.stringify({//将JSON转换为字符串
                        event: "move",
                        direction: d,
                    }))
                }
            });
        }
    }
    start() {
        this.create_walls();
        this.add_listening_events();
    }
    update_size() {
        this.L = parseInt(Math.min(this.parent.clientWidth / this.cols, this.parent.clientHeight / this.rows));
        this.ctx.canvas.width = this.L * this.cols;
        this.ctx.canvas.height = this.L * this.rows;
    }

    check_ready() {//判断两条蛇是否都准备好下一回合了
        for (const snake of this.snakes) {
            //js中判断是否相等 多一个=
            if (snake.status !== "idle") return false;//当前必须处于静止状态 才算准备好进入下一回合
            if (snake.direction === -1) return false;//判断有没有接收到键盘合法输入
        }
        return true;
    }

    next_step() {
        for (const snake of this.snakes) {
            snake.next_step();
        }
    }

    check_valid(cell) {//检测目标位置是否合法：没有撞到两条蛇的身体和障碍物
        for (const wall of this.walls) {
            if (wall.r === cell.r && wall.c === cell.c)
                return false;
        }

        for (const snake of this.snakes) {
            let k = snake.cells.length;
            if (!snake.check_tail_increasing()) {//蛇的长度不增加 也就是蛇尾会前进 此时蛇尾不做判断
                k--;//也就是不在snake.cells[k]这个位置上做判断 干脆k-1
            }
            //新增的cell与每条蛇的每个cell逐个判断
            for (let i = 0; i < k; i++) {
                if (snake.cells[i].r === cell.r && snake.cells[i].c === cell.c)
                    return false;
            }
        }
        return true;
    }

    //每帧执行一次
    update() {
        // 正方形的大小 自适应变化
        this.update_size();

        //判断蛇的的下一步
        if (this.check_ready()) {
            this.next_step();
        }

        this.render();
    }
    render() {//渲染    
        const color_even = '#AAD751'//偶数颜色
        const color_odd = '#A2D149'//奇数颜色
        for (let r = 0; r < this.rows; r++) {
            for (let c = 0; c < this.cols; c++) {
                if ((r + c) % 2 == 0) {
                    this.ctx.fillStyle = color_even;
                } else {
                    this.ctx.fillStyle = color_odd;
                }
                this.ctx.fillRect(c * this.L, r * this.L, this.L, this.L);
            }
        }
    }
}