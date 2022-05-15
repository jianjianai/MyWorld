package cn.jja8.myWorld.bukkit.work;

/**
 * 组中的世界
 * */
public class MyWorldWorldGroupingWorlding {
    final String type;
    final MyWorldWorldGrouping myWorldWorldGrouping;
    final MyWorldWorlding myWorldWorlding;
    MyWorldWorldGroupingWorlding(String type, MyWorldWorldGrouping myWorldWorldGrouping, MyWorldWorlding myWorldWorlding) {
        this.type = type;
        this.myWorldWorldGrouping = myWorldWorldGrouping;
        this.myWorldWorlding = myWorldWorlding;

        myWorldWorldGrouping.type_MyWorldWorldGroupingWorlding.put(type,this);
    }

    /**
     * 从组中删除这个世界
     * */
    public MyWorldWorlding delete(){
        myWorldWorldGrouping.type_MyWorldWorldGroupingWorlding.remove(type);
        myWorldWorldGrouping.myWorldWorldGroup.worldGroup.removeWorld(myWorldWorlding.myWorldWorldLock.myWorldWorld.name);
        return myWorldWorlding;
    }

    public MyWorldWorldGrouping getMyWorldWorldGrouping() {
        return myWorldWorldGrouping;
    }

    public MyWorldWorlding getMyWorldWorlding() {
        return myWorldWorlding;
    }
}
