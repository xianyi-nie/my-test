const GAME_OBJECTS = [];

export class GameObject {
    constructor(){
        GAME_OBJECTS.push(this);
        this.timedelta = 0;//两帧之间的执行的时间间隔 单位是毫秒
        this.has_called_start = false;//记录start()是否有被执行过
    }
    start(){ //只执行一次

    }
    update(){//每一帧执行一次 除了第一帧之外

    }
    on_destroy(){//删除之前执行

    } 
    destroy(){
        this.on_destroy();

        for(let i in GAME_OBJECTS){
            const obj = GAME_OBJECTS[i];
            if (obj == this){
                GAME_OBJECTS.splice(i);
                break;
            }
        }
    }
}
let last_timestemp;//上一次执行的时刻
const step = timestemp =>{
    for(let obj of GAME_OBJECTS){
        if(!obj.has_called_start){
            obj.has_called_start = true;
            obj.start();
        }else{
            obj.timedelta = timestemp - last_timestemp;
            obj.update();
        }
    }
    last_timestemp = timestemp;
    requestAnimationFrame(step)
}
requestAnimationFrame(step)