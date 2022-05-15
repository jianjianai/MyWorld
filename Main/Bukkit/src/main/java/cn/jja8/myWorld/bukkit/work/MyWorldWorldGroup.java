package cn.jja8.myWorld.bukkit.work;

import cn.jja8.myWorld.all.basic.DatasheetSupport.WorldGroup;
import cn.jja8.myWorld.bukkit.MyWorldBukkit;
import cn.jja8.myWorld.bukkit.work.error.GroupIsRunning;
import cn.jja8.myWorld.bukkit.work.error.NoAllWorldLocks;
import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


/**
 * 代表一个世界组
 * */
public class MyWorldWorldGroup {


    public static interface OnLoad{
        /**
         * 加载完成时
         * */
        void onload(MyWorldWorldGrouping worldGrouping);
        /**
         * 加载失败
         * @param exception 可能抛出 NoAllWorldLocks 或其他
         */
        void fail(Exception exception);
    }

    WorldGroup worldGroup;
    String name;
    MyWorldWorldGroup(WorldGroup worldGroup) {
        this.worldGroup = worldGroup;
        name = worldGroup.getWorldGroupName();
    }

    /**
     * 获取世界组名称
     * */
    public String getName() {
        return name;
    }

    /**
     * 添加世界到组中
     * */
    public MyWorldWorldGroupWorld putWorld(MyWorldWorld world){
        worldGroup.addWorld(world.name);
        return new MyWorldWorldGroupWorld(this,world);
    }

    /**
     * 获取组中的世界
     * */
    public MyWorldWorldGroupWorld getWorld(String worldName){
        if (!worldGroup.containsWorld(worldName)) {
            return null;
        }
        return new MyWorldWorldGroupWorld(this,new MyWorldWorld(worldName));
    }

    /**
     * 获取组中的所有世界
     * */
    public List<MyWorldWorldGroupWorld> getWorldList(){
        List<MyWorldWorldGroupWorld> worlds = new ArrayList<>();
        for (String s : worldGroup.getWorldList()) {
            worlds.add(new MyWorldWorldGroupWorld(this,new MyWorldWorld(s)));
        }
        return worlds;
    }



    /**
     * 删除这个世界组,并且返回组中的世界。
     * */
    public List<MyWorldWorld> delete() throws GroupIsRunning {
        MyWorldWorldGrouping po = MyWorldManger.groupName_myWorldWorldGrouping.get(name);
        if(po!=null){
            throw new GroupIsRunning("组正在运行！");
        }
        //删除世界库中的世界
        List<MyWorldWorld> worlds = new ArrayList<>();
        for (MyWorldWorldGroupWorld myWorldWorldGroupWorld : getWorldList()) {
            worlds.add(myWorldWorldGroupWorld.getMyWorldWorld());
        }
        worldGroup.delete();
        return worlds;
    }

    /**
     * 获取已经加载的本世界组
     * @return null 没有被加载
     * */
    public MyWorldWorldGrouping getLoading(){
        return MyWorldManger.groupName_myWorldWorldGrouping.get(name);
    }

    /**
     * 加载这个世界组
     * @param onLoad 加载完成时返回世界组,此时是在异步运行
     * */
    public void load(OnLoad onLoad){
        Bukkit.getScheduler().runTaskAsynchronously(MyWorldBukkit.getMyWorldBukkit(), () -> {
            synchronized (MyWorldWorldGroup.class){
                MyWorldWorldGrouping myWorldWorldGrouping = getLoading();
                if (myWorldWorldGrouping!=null){
                    onLoad.onload(myWorldWorldGrouping);
                    return;
                }
                try {
                    myWorldWorldGrouping = new MyWorldWorldGrouping(this);
                    onLoad.onload(myWorldWorldGrouping);
                } catch (NoAllWorldLocks e) {
                    onLoad.fail(e);
                }
            }
        });
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MyWorldWorldGroup that = (MyWorldWorldGroup) o;
        return Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }


}
