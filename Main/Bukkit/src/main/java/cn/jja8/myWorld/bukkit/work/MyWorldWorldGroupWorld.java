package cn.jja8.myWorld.bukkit.work;

public class MyWorldWorldGroupWorld {
    MyWorldWorldGroup myWorldWorldGroup;
    MyWorldWorld myWorldWorld;

    public MyWorldWorldGroupWorld(MyWorldWorldGroup myWorldWorldGroup, MyWorldWorld myWorldWorld) {
        this.myWorldWorldGroup = myWorldWorldGroup;
        this.myWorldWorld = myWorldWorld;
    }
    /**
     * 从组中删除世界
     * */
    public MyWorldWorld delete(){
        myWorldWorldGroup.worldGroup.removeWorld(myWorldWorld.name);
        return myWorldWorld;
    }

    public MyWorldWorldGroup getMyWorldWorldGroup() {
        return myWorldWorldGroup;
    }

    public MyWorldWorld getMyWorldWorld() {
        return myWorldWorld;
    }
}
