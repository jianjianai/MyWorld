package cn.jja8.myWorld.bukkit.work;

import cn.jja8.myWorld.bukkit.ConfigBukkit;
import cn.jja8.myWorld.bukkit.MyWorldBukkit;
import cn.jja8.myWorld.bukkit.basic.WorldData;
import cn.jja8.myWorld.bukkit.basic.worldDataSupport.WorldDataLock;
import cn.jja8.myWorld.bukkit.config.WorldBuilder;
import cn.jja8.myWorld.bukkit.work.error.*;
import cn.jja8.myWorld.bukkit.work.name.PlayerWorldTypeAtName;
import cn.jja8.myWorld.bukkit.work.name.WorldCustomDataName;
import com.esotericsoftware.yamlbeans.YamlException;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyWorldWorldGrouping {
    MyWorldWorldGroup myWorldWorldGroup;
    MyWorldWorldGroupInform myWorldWorldGroupInform;

    Map<World, WorldDataLock> worldLockMap = new HashMap<>();
    Map<String,World> typeWorldMap=new HashMap<>();

    MyWorldWorldGrouping(MyWorldWorldGroup myWorldWorldGroup) throws NoAllWorldLocks {
        this.myWorldWorldGroup = myWorldWorldGroup;
        myWorldWorldGroupInform = new MyWorldWorldGroupInform(myWorldWorldGroup);
        List<String> worldList = myWorldWorldGroup.worldGroup.getWorldList();
        Map<WorldDataLock,String> worldDataLockNameMap = new HashMap<>();
        //加载所有世界的锁
        for (String s : worldList) {
            WorldCreator worldCreator = new WorldCreator(s);
            WorldDataLock worldDataLock = WorldData.worldDataSupport.getWorldDataLock(worldCreator, ConfigBukkit.getWorldConfig().服务器名称);
            if (worldDataLock!=null) {
                worldDataLockNameMap.put(worldDataLock,s);

                //加载世界生成器
                WorldBuilder worldBuilder = null;
                byte[] bytes = worldDataLock.getCustomDataByte(WorldCustomDataName.WorldCreator.toString());
                if (bytes!=null){
                    try {
                        worldBuilder = WorldBuilder.loadAsByte(bytes);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (worldBuilder==null){
                    worldBuilder = ConfigBukkit.getWorldConfig().主世界生成器;
                }
                worldCreator.copy(worldBuilder.getWordBuilder(s));
            }
        }
        //判断世界全部上锁
        if (worldDataLockNameMap.size()<worldList.size()){
            for (WorldDataLock worldDataLock : worldDataLockNameMap.keySet()) {
                worldDataLock.unlock();
            }
            throw new NoAllWorldLocks("无法获取"+myWorldWorldGroup.getName()+"世界组中全部的锁。");
        }

        //加载全部世界
        worldDataLockNameMap.forEach((worldDataLock, worldName) -> {
            byte[] bytes = worldDataLock.getCustomDataByte(WorldCustomDataName.WorldType.toString());
            if (bytes==null){
                MyWorldBukkit.getMyWorldBukkit().getLogger().warning(myWorldWorldGroup.getName()+"中"+worldName+"世界没有世界类型，将不会被加载。");
                worldDataLock.unlock();
                return;
            }
            String worldType = new String(bytes, StandardCharsets.UTF_8);
            LoadingProgress loadingProgress =  new LoadingProgress(worldName);
            World world = worldDataLock.loadWorldAsync(loadingProgress);
            loadingProgress.finish();
            typeWorldMap.put(worldType,world);
            worldLockMap.put(world,worldDataLock);
            MyWorldManger.world_MyWorldWorldGrouping.put(world,this);
        });
        MyWorldManger.groupName_myWorldWorldGrouping.put(myWorldWorldGroup.getName(),this);
    }

    /**
     * 卸载全部全部世界
     * @param save 是否保存数据。
     * */
    public void unLoad(boolean save){
        World mainWord = Bukkit.getWorld(ConfigBukkit.getWorldConfig().主世界名称);
        if (mainWord==null){
            typeWorldMap.forEach((worldType, world) -> {
                for (Player player : world.getPlayers()) {
                    player.kickPlayer(ConfigBukkit.getLang().世界卸载主世界配置错误);
                }
                WorldDataLock worldDataLock = worldLockMap.get(world);
                worldDataLock.unloadWorld(save);
                worldDataLock.setCustomDataByte("WorldType",worldType.getBytes(StandardCharsets.UTF_8));
                worldDataLock.unlock();
                MyWorldManger.world_MyWorldWorldGrouping.remove(world);
            });
        }else {
            typeWorldMap.forEach((worldType, world) -> {
                for (Player player : world.getPlayers()) {
                    player.teleport(mainWord.getSpawnLocation());
                }
                WorldDataLock worldDataLock = worldLockMap.get(world);
                worldDataLock.unloadWorld(save);
                worldDataLock.setCustomDataByte("WorldType",worldType.getBytes(StandardCharsets.UTF_8));
                worldDataLock.unlock();
                MyWorldManger.world_MyWorldWorldGrouping.remove(world);
            });
        }
        worldLockMap=null;
        typeWorldMap=null;
        if (save) {
            myWorldWorldGroupInform.save();
        }
        myWorldWorldGroupInform = null;
        MyWorldManger.groupName_myWorldWorldGrouping.remove(myWorldWorldGroup.getName());
    }

    public World getWorld(String type){
        return typeWorldMap.get(type);
    }

    /**
     * 获取全部加载的世界
     * */
    public List<MyWorldWorlding> getAllLoadWorld(){
        ArrayList<MyWorldWorlding> myWorldWorldGroupings = new ArrayList<>();
        typeWorldMap.forEach((s, world) -> {
            myWorldWorldGroupings.add(new MyWorldWorlding(world,s));
        });
        return myWorldWorldGroupings;
    }

    /**
     * 获取指定type的世界
     * */
    public MyWorldWorlding getMyWorldWording(String type){
        World world = getWorld(type);
        return world==null?null:new MyWorldWorlding(world,type);
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
        World world = getWorld(PlayerWorldTypeAtName.world.toString());
        if (world==null){
            for (World s : typeWorldMap.values()) {
                world = s;
                break;
            }
        }
        if (world==null){
            player.sendMessage("你的世界组中没有任何世界，请联系管理员！");
            throw new MyWorldError("至少要开启一个世界才能去玩家的世界。");
        }
        player.teleport(world.getSpawnLocation());
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
     * 删除世界
     * */
    public void removeWorld(String type){
        if (!myWorldWorldGroup.containsWorld(type)){
            return;
        }
        World world;
        if ((world=typeWorldMap.get(type))==null){
            myWorldWorldGroup.removeWorld(type);
            return;
        }
        WorldDataLock worldDataLock = worldLockMap.get(world);
        if (worldDataLock!=null){
            worldDataLock.delWorld();
            worldDataLock.unlock();

        }
        typeWorldMap.remove(type);
        worldLockMap.remove(world);
    }

    /**
     * 不允许在主线程调用，会柱塞线程。需要确保worldCreator的名称不和任何世界重名。type同一个世界组中不允许重名。
     * */
    public World putWorld(String type, WorldBuilder worldBuilder, String WorldName) throws ExistsWorld, NoWorldLocks, ExistsType {
        if (WorldData.worldDataSupport.isWorldExistence(WorldName)){
            throw new ExistsWorld("世界已经被创建");
        }
        WorldDataLock worldDataLock = WorldData.worldDataSupport.getWorldDataLock(worldBuilder.getWordBuilder(WorldName), ConfigBukkit.getWorldConfig().服务器名称);
        if (worldDataLock==null){
            throw new NoWorldLocks("无法获得锁");
        }
        World world1;
        LoadingProgress loadingProgress =  new LoadingProgress(WorldName);
        synchronized (MyWorldWorldGrouping.class){
            if (typeWorldMap.containsKey(type)){
                worldDataLock.unlock();
                loadingProgress.finish();
                throw new ExistsType("type已经存在");
            }
            byte[] data = new byte[0];
            try {
                data = worldBuilder.saveToByte();
            } catch (YamlException e) {
                e.printStackTrace();
            }
            if (data!=null){
                worldDataLock.setCustomDataByte(WorldCustomDataName.WorldCreator.toString(),data);
            }
            if (!myWorldWorldGroup.containsWorld(WorldName)){
                myWorldWorldGroup.addWorld(WorldName);
            }
            world1 = worldDataLock.loadWorldAsync(loadingProgress);
            worldLockMap.put(world1,worldDataLock);
            typeWorldMap.put(type,world1);
            MyWorldManger.world_MyWorldWorldGrouping.put(world1,this);
        }
        loadingProgress.finish();
        return world1;
    }
}
