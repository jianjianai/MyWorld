package cn.jja8.myWorld.bukkit;


import cn.jja8.myWorld.bukkit.basic.PlayerData;
import cn.jja8.myWorld.bukkit.basic.Portal;
import cn.jja8.myWorld.bukkit.basic.Teams;
import cn.jja8.myWorld.bukkit.basic.WorldData;
import cn.jja8.myWorld.bukkit.command.AdminCommand;
import cn.jja8.myWorld.bukkit.command.UserCommand;
import cn.jja8.myWorld.bukkit.player.PlayerDataManager;
import cn.jja8.myWorld.bukkit.word.PlayerWordMangaer;
import cn.jja8.myWorld.bukkit.word.PlayerWorldPortal;
import cn.jja8.myWorld.bukkit.word.WorldClean;
import cn.jja8.myWorld.bukkit.word.WorldSecurity;
import cn.jja8.patronSaint_2022_3_2_1244.bukkit.bStats.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Random;


public class MyWorldBukkit extends JavaPlugin{

    static PlayerWordMangaer playerWordMangaer = null;
    static UserCommand userCommand = null;
    static AdminCommand adminCommand = null;
    static PlayerDataManager playerDataManager = null;
    static MyWorldBukkit myWorldBukkit = null;
    static WorldSecurity worldSecurity = null;
    static WorldClean worldClean = null;
    static PlayerWorldPortal playerWorldPortal = null;

    public static PlayerWordMangaer getPlayerWordMangaer() {
        return playerWordMangaer;
    }
    public static MyWorldBukkit getMyWorldBukkit() {
        return myWorldBukkit;
    }
    public static PlayerDataManager getPlayerDataManager() {
        return playerDataManager;
    }
    public static UserCommand getUserCommand() {
        return userCommand;
    }
    public static AdminCommand getAdminCommand() {
        return adminCommand;
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
        ConfigBukkit.load();
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
        playerDataManager = new PlayerDataManager();
        worldSecurity = new WorldSecurity();
        worldClean = new WorldClean();
        playerWorldPortal = new PlayerWorldPortal();

        userCommand = new UserCommand();
        adminCommand = new AdminCommand();

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
