package cn.jja8.myWorld.bukkit.word;

import cn.jja8.myWorld.bukkit.basic.WorldData;
import cn.jja8.myWorld.bukkit.basic.worldDataSupport.WorldDataLock;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;


import java.io.*;
import java.nio.charset.StandardCharsets;


/**
 * 代表一个世界
 */
public class PlayerWorlds {

    World 主世界,地狱,末地;
    PlayerWordInform 世界信息;
    String name;
    WorldDataLock 锁;

    protected PlayerWorlds() {}
    public String getName() {
        return name;
    }
    public PlayerWordInform getPlayerWordInform() {
        return 世界信息;
    }
    public World getWorld() {
        return 主世界;
    }
    public World getInfernalWorld(){
        return 地狱;
    }
    public World getEndWorld(){
        return 末地;
    }
    public void setPlayerLocation(Player player, Location location) {
        World world = location.getWorld();
        if (world!= getWorld()&world!= getInfernalWorld()&world!= getEndWorld()){
            throw new Error("不可以保存玩家世界以外的位置。"+world.getName()+"不是玩家世界"+getName()+"中的世界。");
        }
        OutputStream outputStream = WorldData.worldDataSupport.getCustomDataOutputStream(name, "location/"+player.getUniqueId());
        OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream, StandardCharsets.UTF_8);
        YamlConfiguration yamlConfiguration = new YamlConfiguration();
        yamlConfiguration.set("location",location);
        try {
            outputStreamWriter.write(yamlConfiguration.saveToString());
        } catch (IOException exception) {
            exception.printStackTrace();
        }
        try {outputStreamWriter.close(); } catch (IOException ignored) { }
        try { outputStream.close(); } catch (IOException ignored) { }
    }
    /**
     * @return null 没有这个玩家的位置
     * */
    public Location getPlayerLocation(Player player) {
        InputStream inputStream = WorldData.worldDataSupport.getCustomDataInputStream(name,"location/"+player.getUniqueId());
        if (inputStream==null){
            return null;
        }
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream,StandardCharsets.UTF_8);
        YamlConfiguration yamlConfiguration = YamlConfiguration.loadConfiguration(inputStreamReader);
        try { inputStreamReader.close(); } catch (IOException ignored) { }
        try { inputStream.close(); } catch (IOException ignored) { }
        return yamlConfiguration.getLocation("location",null);
    }

    /**
     * 将某玩家会到这个世界
     * */
    public void playerBack(Player player){
        Location location = null;
        try {
            location = getPlayerLocation(player);
        }catch (Exception|Error e){
            e.printStackTrace();
        }

        if (location!=null){
            player.teleport(location);
        }else {
            playerBackSpawn(player);
        }
    }
    /**
     * 将某玩家传送去出生点
     * */
    public void playerBackSpawn(Player player){
        World world = getWorld();
        if (world==null){
            world = getInfernalWorld();
        }
        if (world==null){
            world = getEndWorld();
        }
        if (world==null){
            throw new Error("至少要开启一个世界才能去玩家的世界。");
        }
        player.teleport(world.getSpawnLocation());
    }
}
