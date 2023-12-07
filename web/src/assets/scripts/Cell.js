export class Cell{
    //r表示行 c表示列
    constructor(r,c){
        this.r = r;
        this.c = c;
        this.x = c + 0.5;
        this.y = r + 0.5;
    }

}