package cn.jja8.myWorld.bukkit.work;

import cn.jja8.myWorld.bukkit.word.PlayerWorlds;
import org.bukkit.World;

import java.util.ArrayList;
import java.util.List;

public class MyWorldWorldGrouping {
    PlayerWorlds playerWorlds;
    MyWorldWorldGroup myWorldWorldGroup;
    MyWorldWorldGrouping(PlayerWorlds playerWorlds, MyWorldWorldGroup myWorldWorldGroup) {
        this.playerWorlds = playerWorlds;
        this.myWorldWorldGroup = myWorldWorldGroup;
    }

    /**
     * 卸载全部全部世界
     * @param save 是否保存数据。
     * */
    public void unLoad(boolean save){
        playerWorlds.unLoad(save);
        MyWorldWorldGroup.groupName_myWorldWorldGroupingMap.remove(myWorldWorldGroup.name);
        playerWorlds = null;
    }

    /**
     * 获取全部加载的世界
     * */
    public List<MyWorldWording> getAllLoadWorld(){
        ArrayList<MyWorldWording> myWorldWorldGroupings = new ArrayList<>();
        playerWorlds.getTypeWorldMap().forEach((s, world) -> {
            myWorldWorldGroupings.add(new MyWorldWording(this,world,s));
        });
        return myWorldWorldGroupings;
    }

    /**
     * 获取指定type的世界
     * */
    public MyWorldWording getMyWorldWording(String type){
        World world = playerWorlds.getWorld(type);
        return world==null?null:new MyWorldWording(this,world,type);
    }
}
