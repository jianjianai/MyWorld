package cn.jja8.myWorld.bukkit;


import cn.jja8.myWorld.bukkit.basic.Portal;
import cn.jja8.myWorld.bukkit.basic.Teams;
import cn.jja8.myWorld.bukkit.command.Command;
import cn.jja8.myWorld.bukkit.config.*;
import cn.jja8.myWorld.bukkit.basic.PlayerData;
import cn.jja8.myWorld.bukkit.basic.WorldData;
import cn.jja8.myWorld.bukkit.player.PlayerDataManager;
import cn.jja8.myWorld.bukkit.word.PlayerWordMangaer;
import cn.jja8.myWorld.bukkit.word.PlayerWorldPortal;
import cn.jja8.myWorld.bukkit.word.WorldClean;
import cn.jja8.myWorld.bukkit.word.WorldSecurity;
import cn.jja8.patronSaint_2022_3_2_1244.allUsed.file.YamlConfig;
import cn.jja8.patronSaint_2022_3_2_1244.bukkit.bStats.Metrics;
import org.bukkit.*;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;


public class MyWorldBukkit extends JavaPlugin{
    static FileConfig fileConfig = new FileConfig();
    static Lang lang = new Lang();
    static WorldConfig worldConfig = new WorldConfig();
    static TeamConfig teamConfig = new TeamConfig();
    static PlayerDataConfig playerDataConfig = new PlayerDataConfig();
    static PlayerWordMangaer playerWordMangaer = null;
    static Command command = null;
    static PlayerDataManager playerDataManager = null;
    static MyWorldBukkit myWorldBukkit = null;
    static WorldSecurity worldSecurity = null;
    static WorldClean worldClean = null;
    static PlayerWorldPortal playerWorldPortal = null;

    public static FileConfig getFileConfig() {
        return fileConfig;
    }
    public static Lang getLang() {
        return lang;
    }
    public static WorldConfig getWorldConfig() {
        return worldConfig;
    }
    public static TeamConfig getTeamConfig() {
        return teamConfig;
    }
    public static PlayerWordMangaer getPlayerWordMangaer() {
        return playerWordMangaer;
    }
    public static PlayerDataConfig getPlayerDataConfig(){
        return playerDataConfig;
    }
    public static MyWorldBukkit getMyWorldBukkit() {
        return myWorldBukkit;
    }
    public static PlayerDataManager getPlayerDataManager() {
        return playerDataManager;
    }
    public static Command getCommand() {
        return command;
    }
    public static WorldSecurity getWorldSecurity() {
        return worldSecurity;
    }
    public static WorldClean getWorldClean() {
        return worldClean;
    }
    public static PlayerWorldPortal getPlayerWorldPortal() {
        return playerWorldPortal;
    }



    @Override
    public void onEnable() {
        myWorldBukkit = this;
        //加载配置
        try {
            fileConfig = YamlConfig.loadFromFile(new File(getDataFolder(), "FileConfig.yml"), fileConfig);
        } catch (IOException exception) {
            exception.printStackTrace();
        }
        try {
            lang = YamlConfig.loadFromFile(new File(getDataFolder(), "Lang.yml"), lang);
        } catch (IOException exception) {
            exception.printStackTrace();
        }
        try {
            worldConfig = YamlConfig.loadFromFile(new File(getDataFolder(), "WorldConfig.yml"), worldConfig);
            ArrayList<String> list = new ArrayList<>();
            worldConfig.禁止玩家使用的世界名称列表.forEach(s -> list.add(s.toLowerCase()));
            worldConfig.禁止玩家使用的世界名称列表 = list;
        } catch (IOException exception) {
            exception.printStackTrace();
        }
        try {
            teamConfig = YamlConfig.loadFromFile(new File(getDataFolder(), "TeamConfig.yml"), teamConfig);
        } catch (IOException exception) {
            exception.printStackTrace();
        }
        try {
            playerDataConfig = YamlConfig.loadFromFile(new File(getDataFolder(), "PlayerDataConfig.yml"), playerDataConfig);
        } catch (IOException exception) {
            exception.printStackTrace();
        }
        //加载data
        boolean ok = true;
        try {
            PlayerData.load();
            WorldData.load();
            Portal.load();
        }catch (Error|Exception throwable){
            ok = false;
            throwable.printStackTrace();
        }
        String v = Bukkit.getServer().getClass().getName().split("\\.")[3];
        if (!ok){
            for (int i = 0; i < 3; i++) {
                getLogger().severe("插件无法启用，可能暂时还不兼容当前服务端，您可以从网上下载兼容扩展来使插件兼容当前服务端。");
            }
            getLogger().severe("-------------------------------------------------------");
            getLogger().severe("兼容版本：v1_12_R1,v1_16_R3,v1_17_R1,v1_18_R2");
            getLogger().severe("当前服务端版本："+v);
            getLogger().severe("-------------------------------------------------------");
            getLogger().severe("如果您的服务器在兼容列表中，请自行排除上方报错！若有疑问，您可以前往 “PlugClub/插件实验室 - 820131534” 交流。");
            return;
        }
        Teams.load();
        getLogger().info("-------------------------------------------------------");
        getLogger().info("当前服务端版本："+v);
        getLogger().info("-------------------------------------------------------");
        getLogger().info("若有疑问，您可以前往 “PlugClub/插件实验室 - 820131534” 交流。");
        getLogger().warning("当前非正式版本，若有bug您可以前往 “PlugClub/插件实验室 - 820131534” 交流和反馈。");
        //加载管理器
        playerWordMangaer = new PlayerWordMangaer();
        command = new Command();
        playerDataManager = new PlayerDataManager();
        worldSecurity = new WorldSecurity();
        worldClean = new WorldClean();
        playerWorldPortal = new PlayerWorldPortal();

        new Metrics(this,14206);
    }

    /**
     * 默认的虚空世界生成器
     */
    @Override
    public ChunkGenerator getDefaultWorldGenerator(String worldName, String id) {
        return new ChunkGenerator() {
            @Override
            public ChunkData generateChunkData(World world, Random random, int x, int z, BiomeGrid biome) {
                ChunkData 区块 = createChunkData(world);
                if (x == 0 & z == 0) {
                    for (int i=0;i<=3;i++){
                        for (int q=0;q<=3;q++){
                            区块.setBlock(i, 98, q, Material.STONE);
                        }
                    }
                }
                return 区块;
            }
            @Override
            public Location getFixedSpawnLocation(World world, Random random) {
                return new Location(world, 1.5, 100, 1.5);
            }
        };
    }

    @Override
    public void onDisable() {
        if (playerWordMangaer != null) {
            getLogger().info("正在保存世界..");
            playerWordMangaer.close();
        }
        if (playerDataManager != null) {
            getLogger().info("正在保存玩家数据..");
            playerDataManager.close();
        }
        Teams.unLoad();
        getLogger().info("插件已关闭..");
    }
}
