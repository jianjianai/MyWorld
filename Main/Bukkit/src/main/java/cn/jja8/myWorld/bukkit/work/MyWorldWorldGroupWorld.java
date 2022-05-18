package cn.jja8.myWorld.bukkit.work;

import java.util.Objects;

/**
 * 代表组中的世界
 * */
public class MyWorldWorldGroupWorld {
    private final MyWorldWorldGroup myWorldWorldGroup;
    private final MyWorldWorld myWorldWorld;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MyWorldWorldGroupWorld that = (MyWorldWorldGroupWorld) o;
        return myWorldWorldGroup.equals(that.myWorldWorldGroup) && myWorldWorld.equals(that.myWorldWorld);
    }

    @Override
    public int hashCode() {
        return Objects.hash(myWorldWorldGroup, myWorldWorld);
    }
}
