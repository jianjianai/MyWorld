package cn.jja8.myWorld.bukkit.basic.playerDataSupport;

import cn.jja8.myWorld.all.veryUtil.FileLock;
import net.minecraft.server.v1_16_R3.*;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.io.*;

public class v1_16_R3 implements PlayerDataSupport{
    File dataFile;
    public v1_16_R3(File dataFile){
        this.dataFile = dataFile;
        dataFile.mkdirs();
    }
    public static class PlayerDataLock_v1_16_R3{
        File playerDataFile;
        FileLock fileLock;

        public PlayerDataLock_v1_16_R3(File playerDataFile, FileLock fileLock) {
            this.playerDataFile = playerDataFile;
            this.fileLock = fileLock;
        }
    }

    @Override
    public PlayerDataLock getPlayerDataLock(Player player, String serverName) {
        File playerDataFile = new File(dataFile, player.getUniqueId() +".dat");
        File playerLockFile = new File(dataFile, player.getUniqueId() +".lock");
        FileLock fileLock = FileLock.getFileLock(playerLockFile,serverName);
        if (fileLock!=null){
            return new v1_16_R3_old.Lock(playerDataFile,fileLock);
        }
    }
}
