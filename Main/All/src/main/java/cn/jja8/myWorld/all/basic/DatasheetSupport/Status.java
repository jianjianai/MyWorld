package cn.jja8.myWorld.all.basic.DatasheetSupport;

/**
 * 数字越小级别越高
 * */
public enum Status {
    leader(0),
    admin(1),
    player(2),
    unKnow(99),
    ;

    final int level;
    Status(int level){
        this.level = level;
    }

    public int getLevel() {
        return level;
    }
}
