package cn.jja8.myWorld.bukkit.work;

import cn.jja8.myWorld.bukkit.ConfigBukkit;
import cn.jja8.myWorld.bukkit.MyWorldBukkit;
import cn.jja8.myWorld.bukkit.work.error.ExistsType;
import cn.jja8.myWorld.bukkit.work.error.MyWorldError;
import cn.jja8.myWorld.bukkit.work.error.NoAllWorldLocks;
import cn.jja8.myWorld.bukkit.work.name.PlayerWorldTypeAtName;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyWorldWorldGrouping {
    MyWorldWorldGroup myWorldWorldGroup;
    MyWorldWorldGroupInform myWorldWorldGroupInform;

    Map<String,MyWorldWorldGroupingWorlding> type_MyWorldWorldGroupingWorlding = new HashMap<>();

    MyWorldWorldGrouping(MyWorldWorldGroup myWorldWorldGroup) throws NoAllWorldLocks {
        this.myWorldWorldGroup = myWorldWorldGroup;
        myWorldWorldGroupInform = new MyWorldWorldGroupInform(myWorldWorldGroup);
        List<String> worldList = myWorldWorldGroup.worldGroup.getWorldList();
        List<MyWorldWorldLock> myWorldWorldLockList = new ArrayList<>();
        //加载所有世界的锁
        for (String s : worldList) {
            MyWorldWorld myworldworld = MyWorldManger.getWorld(s);
            if (myworldworld==null){
                MyWorldBukkit.getMyWorldBukkit().getLogger().warning("世界组"+myWorldWorldGroup.getName()+"中的"+s+"世界不存在，将不会被加载！");
                continue;
            }
            MyWorldWorldLock myWorldWorld = myworldworld.getMyWorldWorldLock();
            if (myWorldWorld==null){
                for (MyWorldWorldLock myWorldWorldLock : myWorldWorldLockList) {
                    myWorldWorldLock.unlock(false);
                }
                throw new NoAllWorldLocks("无法获得世界"+s+"的锁，有可能已经被其他服务器加载。");
            }else {
                myWorldWorldLockList.add(myWorldWorld);
            }
        }
        //加载全部世界
        for (MyWorldWorldLock myWorldWorldLock : myWorldWorldLockList) {
            String worldType = myWorldWorldLock.getMyWorldWorldInform().getMyWorldWorldType().getType();
            if (worldType==null){
                MyWorldBukkit.getMyWorldBukkit().getLogger().warning("世界组"+myWorldWorldGroup.getName()+"中的"+myWorldWorldLock.myWorldWorld.name+"世界没有指定type，不会被加载！");
                myWorldWorldLock.unlock(false);
                continue;
            }
            MyWorldWorldGroupingWorlding myWorldWorldGroupingWorlding = type_MyWorldWorldGroupingWorlding.get(worldType);
            if (myWorldWorldGroupingWorlding!=null){
                MyWorldBukkit.getMyWorldBukkit().getLogger().warning("世界组"+myWorldWorldGroup.getName()+"中的"+myWorldWorldLock.myWorldWorld.name+"世界的tpye与"+myWorldWorldGroupingWorlding.myWorldWorlding.myWorldWorldLock.myWorldWorld.name+"世界相同，不会被加载！");
                myWorldWorldLock.unlock(false);
                continue;
            }
            final MyWorldWorlding[] worlding = new MyWorldWorlding[1];
            final Exception[] err = new Exception[1];
            myWorldWorldLock.loadWorld(new MyWorldWorldLock.OnLoad() {
                @Override
                public void onload(MyWorldWorlding myWorldWorlding) {
                    worlding[0] = myWorldWorlding;
                }

                @Override
                public void fail(Exception exception) {
                    err[0] = exception;
                }
            });
            while (true){
                try {
                    Thread.sleep(20);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (worlding[0] !=null){
                    break;
                }
                if (err[0] !=null){
                    err[0].printStackTrace();
                    break;
                }

            }
            if (worlding[0] ==null){
                continue;
            }
            MyWorldWorldGroupingWorlding myWorld = new MyWorldWorldGroupingWorlding(worldType,this, worlding[0]);
            type_MyWorldWorldGroupingWorlding.put(worldType,myWorld);
            MyWorldManger.world_MyWorldWorldGrouping.put(myWorld.getMyWorldWorlding().getWorld(),this);
        }
        MyWorldManger.groupName_myWorldWorldGrouping.put(myWorldWorldGroup.name,this);
    }

    /**
     * 卸载全部全部世界
     * @param save 是否保存数据。
     * */
    public void unLoad(boolean save){
        //踢出玩家
        World mainWord = Bukkit.getWorld(ConfigBukkit.getWorldConfig().主世界名称);
        if (mainWord==null){
            type_MyWorldWorldGroupingWorlding.forEach((worldType, world) -> {
                for (Player player : world.myWorldWorlding.world.getPlayers()) {
                    player.kickPlayer(ConfigBukkit.getLang().世界卸载主世界配置错误);
                }
                world.myWorldWorlding.myWorldWorldLock.unlock(save);
                MyWorldManger.world_MyWorldWorldGrouping.remove(world.myWorldWorlding.world);
            });
        }else {
            type_MyWorldWorldGroupingWorlding.forEach((worldType, world) -> {
                for (Player player : world.myWorldWorlding.world.getPlayers()) {
                    player.teleport(mainWord.getSpawnLocation());
                }
                world.myWorldWorlding.myWorldWorldLock.unlock(save);
                MyWorldManger.world_MyWorldWorldGrouping.remove(world.myWorldWorlding.world);
            });
        }
        if (save) {
            myWorldWorldGroupInform.save();
        }
        MyWorldManger.groupName_myWorldWorldGrouping.remove(myWorldWorldGroup.getName());
    }


    /**
     * 获取全部加载的世界
     * */
    public Map<String,MyWorldWorldGroupingWorlding> getAllLoadWorld(){
        return new HashMap<>(type_MyWorldWorldGroupingWorlding);
    }

    /**
     * 获取指定type的世界
     * */
    public MyWorldWorldGroupingWorlding getMyWorldWording(String type){
        return type_MyWorldWorldGroupingWorlding.get(type);
    }

    /**
     * 玩家返回到这个世界
     * */
    public void playerBack(Player player) {
        Location location = null;
        try {
            location = myWorldWorldGroupInform.getPlayerLeaveLocation().getPlayerLocation(player);
        }catch (Exception|Error e){
            new Exception("玩家"+player.getName()+"在"+myWorldWorldGroup.getName()+"世界上次的位置加载失败！",e).printStackTrace();
        }

        if (location!=null){
            player.teleport(location);
        }else {
            playerBackSpawn(player);
        }
    }
    /**
     * 获得这个世界的信息
     * */
    public MyWorldWorldGroupInform getMyWorldWordInform(){
        return myWorldWorldGroupInform;
    }

    /**
     * 玩家回到出生点
     * */
    public void playerBackSpawn(Player player) {
        MyWorldWorldGroupingWorlding world = type_MyWorldWorldGroupingWorlding.get(PlayerWorldTypeAtName.world.toString());
        if (world==null){
            for (MyWorldWorldGroupingWorlding s : type_MyWorldWorldGroupingWorlding.values()) {
                world = s;
                break;
            }
        }
        if (world==null){
            player.sendMessage("你的世界组中没有任何世界，请联系管理员！");
            throw new MyWorldError("至少要开启一个世界才能去玩家的世界。");
        }
        player.teleport(world.myWorldWorlding.world.getSpawnLocation());
    }

    /**
     * 获取世界组
     * */
    public MyWorldWorldGroup getMyWorldWorldGroup() {
        return myWorldWorldGroup;
    }

    public void setPlayerLeaveLocation(Player player, Location location) {
        myWorldWorldGroupInform.getPlayerLeaveLocation().setPlayerLeaveLocation(player,location);
    }



    /**
     * 将一个世界添加到组中，type不允许重名
     * */
    public MyWorldWorldGroupingWorlding putWorld(String type,MyWorldWorlding myWorldWorlding) throws ExistsType {
        if (type_MyWorldWorldGroupingWorlding.containsKey(type)) {
            throw new ExistsType("type ‘"+type+"’ 已经存在");
        }
        MyWorldWorldGroupingWorlding myWorldWorldGroupingWorlding = new MyWorldWorldGroupingWorlding(type,this,myWorldWorlding);
        myWorldWorldGroup.worldGroup.addWorld(myWorldWorlding.myWorldWorldLock.myWorldWorld.name);
        type_MyWorldWorldGroupingWorlding.put(type,myWorldWorldGroupingWorlding);
        MyWorldManger.world_MyWorldWorldGrouping.put(myWorldWorlding.getWorld(),this);
        return myWorldWorldGroupingWorlding;
    }
}
