package cn.jja8.myWorld.bukkit;


import cn.jja8.myWorld.bukkit.basic.PlayerData;
import cn.jja8.myWorld.bukkit.basic.Portal;
import cn.jja8.myWorld.bukkit.basic.Teams;
import cn.jja8.myWorld.bukkit.basic.WorldData;
import cn.jja8.myWorld.bukkit.command.AdminCommand;
import cn.jja8.myWorld.bukkit.command.UserCommand;
import cn.jja8.myWorld.bukkit.command.tool.NameTool;
import cn.jja8.myWorld.bukkit.listener.*;
import cn.jja8.myWorld.bukkit.work.MyWorldManger;
import cn.jja8.patronSaint.bukkit.v2.bStats.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Random;


public class MyWorldBukkit extends JavaPlugin{

    static LeaveTheWorldPositionRecord leaveTheWorldPositionRecord = null;
    static UserCommand userCommand = null;
    static AdminCommand adminCommand = null;
    static PlayerDataManager playerDataManager = null;
    static MyWorldBukkit myWorldBukkit = null;
    static WorldSecurity worldSecurity = null;
    static WorldClean worldClean = null;
    static WorldPortal worldPortal = null;

    public static LeaveTheWorldPositionRecord getPlayerWordMangaer() {
        return leaveTheWorldPositionRecord;
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
    public static WorldPortal getPlayerWorldPortal() {
        return worldPortal;
    }

    public MyWorldBukkit() {
        myWorldBukkit = this;
    }

    @Override
    public void onLoad() {
        getLogger().info("????????????????????????...");
        //????????????????????????????????????????????????????????????????????????????????????????????????
        ConfigBukkit.load();
        NameTool.load();
        getLogger().info("???????????????");
    }

    @Override
    public void onEnable() {
        //??????data
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
                getLogger().severe("???????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????");
            }
            getLogger().severe("-------------------------------------------------------");
            getLogger().severe("???????????????v1_12_R1,v1_16_R3,v1_17_R1,v1_18_R2,v1_19_R1");
            getLogger().severe("????????????????????????"+v);
            getLogger().severe("-------------------------------------------------------");
            getLogger().severe("?????????????????????????????????????????????????????????????????????????????????????????????????????? ???PlugClub/??????????????? - 820131534??? ?????????");
            return;
        }
        Teams.load();
        getLogger().info("-------------------------------------------------------");
        getLogger().info("????????????????????????"+v);
        getLogger().info("-------------------------------------------------------");
        getLogger().info("?????????????????????????????? ???PlugClub/??????????????? - 820131534??? ?????????");
        getLogger().warning("??????????????????????????????bug??????????????? ???PlugClub/??????????????? - 820131534??? ??????????????????");
        //???????????????
        leaveTheWorldPositionRecord = new LeaveTheWorldPositionRecord();
        playerDataManager = new PlayerDataManager();
        worldSecurity = new WorldSecurity();
        worldClean = new WorldClean();
        worldPortal = new WorldPortal();

        userCommand = new UserCommand();
        adminCommand = new AdminCommand();

        new Metrics(this,14206);
    }

    /**
     * ??????????????????????????????
     */
    @Override
    public ChunkGenerator getDefaultWorldGenerator(String worldName, String id) {
        return new ChunkGenerator() {
            @Override
            public ChunkData generateChunkData(World world, Random random, int x, int z, BiomeGrid biome) {
                ChunkData ?????? = createChunkData(world);
                if (x == 0 & z == 0) {
                    for (int i=0;i<=3;i++){
                        for (int q=0;q<=3;q++){
                            ??????.setBlock(i, 98, q, Material.STONE);
                        }
                    }
                }
                return ??????;
            }
            @Override
            public Location getFixedSpawnLocation(World world, Random random) {
                return new Location(world, 1.5, 100, 1.5);
            }
        };
    }

    @Override
    public void onDisable() {
        if (leaveTheWorldPositionRecord != null) {
            getLogger().info("??????????????????..");
            MyWorldManger.close();
        }
        if (playerDataManager != null) {
            getLogger().info("????????????????????????..");
            playerDataManager.close();
        }
        Teams.unLoad();
        getLogger().info("???????????????..");
    }
}
