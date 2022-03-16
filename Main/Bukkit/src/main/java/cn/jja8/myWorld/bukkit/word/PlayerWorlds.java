package cn.jja8.myWorld.bukkit.word;

import cn.jja8.myWorld.bukkit.basic.WorldData;
import cn.jja8.myWorld.bukkit.basic.worldDataSupport.WorldDataLock;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;


import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


/**
 * 代表一个世界
 */
public class PlayerWorlds {

    Map<String,World> worldMap = new HashMap<>();
    PlayerWordInform playerWordInform;
    String name;
    WorldDataLock lock;

    protected PlayerWorlds() {}
    public String getName() {
        return name;
    }
    public PlayerWordInform getPlayerWordInform() {
        return playerWordInform;
    }
    public World getWorld(String type){
        return worldMap.get(type);
    }
    public World getWorld(PlayerWorldTypeAtName type) {
        return getWorld(type.toString());
    }
    public World putWorld(String type,World world){
        return worldMap.put(type,world);
    }
    public World putWorld(PlayerWorldTypeAtName type, World world){
        return putWorld(type.toString(),world);
    }
    public void setPlayerLocation(Player player, Location location) {
        World world = location.getWorld();
        if (!worldMap.containsValue(world)){
            throw new Error("不可以保存玩家世界以外的位置。"+ Objects.requireNonNull(world).getName()+"不是玩家世界"+getName()+"中的世界。");
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
            new Exception("玩家"+player.getName()+"在"+name+"世界上次的位置加载失败！",e).printStackTrace();
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
        World world = getWorld(PlayerWorldTypeAtName.world);
        if (world==null){
            for (World s : worldMap.values()) {
                world = s;
                break;
            }
        }
        if (world==null){
            player.sendMessage("你的世界组中没有任何世界，请联系管理员！");
            throw new Error("至少要开启一个世界才能去玩家的世界。");
        }
        player.teleport(world.getSpawnLocation());
    }
}
