package cn.jja8.myWorld.bukkit.work;

import cn.jja8.myWorld.bukkit.word.PlayerWorlds;
import org.bukkit.World;
import org.bukkit.entity.Player;

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
        MyWorldManger.groupName_myWorldWorldGroupingMap.remove(myWorldWorldGroup.name);
        playerWorlds = null;
    }

    /**
     * 获取全部加载的世界
     * */
    public List<MyWorldWorlding> getAllLoadWorld(){
        ArrayList<MyWorldWorlding> myWorldWorldGroupings = new ArrayList<>();
        playerWorlds.getTypeWorldMap().forEach((s, world) -> {
            myWorldWorldGroupings.add(new MyWorldWorlding(this,world,s));
        });
        return myWorldWorldGroupings;
    }

    /**
     * 获取指定type的世界
     * */
    public MyWorldWorlding getMyWorldWording(String type){
        World world = playerWorlds.getWorld(type);
        return world==null?null:new MyWorldWorlding(this,world,type);
    }

    /**
     * 玩家返回到这个世界
     * */
    public void playerBack(Player player) {
        playerWorlds.playerBack(player);
    }
    /**
     * 获得这个世界的信息
     * */
    public MyWorldWorldGroupInform getMyWorldWordInform(){
        return new MyWorldWorldGroupInform(playerWorlds.getPlayerWordInform());
    }

    public void playerBackSpawn(Player player) {
        playerWorlds.playerBackSpawn(player);
    }
}
