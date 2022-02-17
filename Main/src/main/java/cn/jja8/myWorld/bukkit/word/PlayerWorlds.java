package cn.jja8.myWorld.bukkit.word;

import cn.jja8.myWorld.bukkit.MyWorldBukkit;
import cn.jja8.myWorld.bukkit.basic.WorldData;
import cn.jja8.myWorld.bukkit.basic.worldDataSupport.WorldDataLock;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;


import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.function.Consumer;


/**
 * 代表一个世界
 */
public class PlayerWorlds {
    public static class LoadedByAnotherServer extends Error {}
    /**
     * 用于发送世界加载信息给全部玩家，有助于防止掉线
     * */
    public static class LoadWorldsNews{
        private String loadWold = "null";
        private int x = 0;
        private boolean task = true;

        public LoadWorldsNews() {
            //bukkit的异步任务在主线程被阻塞的情况下不会运行，所以就用Thread
            new Thread(() -> {
                while (task){
                    x++;
                    MyWorldBukkit.getMyWorldBukkit().getServer().getOnlinePlayers().forEach((Consumer<Player>) player -> player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(MyWorldBukkit.getLang().世界加载提示文本.replaceAll("<世界>", loadWold).replaceAll("<数>", String.valueOf(x)))));
                    try { Thread.sleep(50); } catch (InterruptedException ignored) { }
                }
            }).start();
        }

        public void setLoadWold(String loadWold) {
            this.loadWold = loadWold;
            x =0;
        }

        public void finish() {
            task = false;
            MyWorldBukkit.getMyWorldBukkit().getServer().getOnlinePlayers().forEach((Consumer<Player>) player -> player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(MyWorldBukkit.getLang().世界加载完成提示文本)));
        }
    }
    World 主世界,地狱,末地;
    PlayerWordInform 世界信息;
    String name;
    WorldDataLock 锁;
    PlayerWorlds(String name) {
        this.name = name;
        //验证没被其他服务器加载------
        锁 = WorldData.worldDataSupport.getWorldDataLock(name);
        if (锁.isLocked()){
            throw new LoadedByAnotherServer();
        }
        锁.locked(MyWorldBukkit.getWorldConfig().服务器名称);
        世界信息 = new PlayerWordInform(name);
        LoadWorldsNews loadWorldsNews = new LoadWorldsNews();
        try {
            //加载世界-------
            {
                loadWorldsNews.setLoadWold("name");
                WorldCreator 世界生成器 = new WorldCreator(name);
                世界生成器.generateStructures(MyWorldBukkit.getWorldConfig().主世界生成器.生成建筑);
                世界生成器.environment(MyWorldBukkit.getWorldConfig().主世界生成器.世界维度);
                世界生成器.type(MyWorldBukkit.getWorldConfig().主世界生成器.世界类型);
                世界生成器.generator(WorldCreator.getGeneratorForName(name, MyWorldBukkit.getWorldConfig().主世界生成器.世界生成器,null));
                世界生成器.generatorSettings(MyWorldBukkit.getWorldConfig().主世界生成器.世界生成器参数);
                主世界 = WorldData.worldDataSupport.loadWorld(世界生成器,name);
            }
            //地狱
            if (MyWorldBukkit.getWorldConfig().地狱界生成器.启用){
                String wordName = name+"_nether";
                loadWorldsNews.setLoadWold(wordName);
                WorldCreator 世界生成器 = new WorldCreator(wordName);
                世界生成器.generateStructures(MyWorldBukkit.getWorldConfig().地狱界生成器.生成建筑);
                世界生成器.environment(MyWorldBukkit.getWorldConfig().地狱界生成器.世界维度);
                世界生成器.type(MyWorldBukkit.getWorldConfig().地狱界生成器.世界类型);
                世界生成器.generator(WorldCreator.getGeneratorForName(name, MyWorldBukkit.getWorldConfig().地狱界生成器.世界生成器,null));
                世界生成器.generatorSettings(MyWorldBukkit.getWorldConfig().地狱界生成器.世界生成器参数);
                地狱 = WorldData.worldDataSupport.loadWorld(世界生成器,wordName);
            }
            //末地
            if (MyWorldBukkit.getWorldConfig().末地界生成器.启用){
                String wordName = name+"_the_end";
                loadWorldsNews.setLoadWold(wordName);
                WorldCreator 世界生成器 = new WorldCreator(wordName);
                世界生成器.generateStructures(MyWorldBukkit.getWorldConfig().末地界生成器.生成建筑);
                世界生成器.environment(MyWorldBukkit.getWorldConfig().末地界生成器.世界维度);
                世界生成器.type(MyWorldBukkit.getWorldConfig().末地界生成器.世界类型);
                世界生成器.generator(WorldCreator.getGeneratorForName(name, MyWorldBukkit.getWorldConfig().末地界生成器.世界生成器,null));
                世界生成器.generatorSettings(MyWorldBukkit.getWorldConfig().末地界生成器.世界生成器参数);
                末地 = WorldData.worldDataSupport.loadWorld(世界生成器, wordName);
            }
        }catch (Exception|Error e){
            loadWorldsNews.finish();
            throw e;
        }
        loadWorldsNews.finish();

    }
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
