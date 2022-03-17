package cn.jja8.myWorld.bukkit.basic.playerDataSupport;

import cn.jja8.myWorld.all.veryUtil.FileLock;
import org.bukkit.entity.Player;

import java.io.File;

public abstract class LockAndDataFileSupport implements PlayerDataSupport{
    File dataFile;
    public LockAndDataFileSupport(File dataFile){
        this.dataFile = dataFile;
        dataFile.mkdirs();
    }
    @Override
    public PlayerDataLock getPlayerDataLock(Player player, String serverName) {
        File playerDataFile = new File(dataFile, player.getUniqueId() +".dat");
        File playerLockFile = new File(dataFile, player.getUniqueId() +".lock");
        FileLock fileLock = FileLock.getFileLock(playerLockFile,serverName);
        if (fileLock!=null){
            LockAndDataFile lockAndDataFile = getLockAndDataFile(playerDataFile,player);
            lockAndDataFile.fileLock = fileLock;
            return lockAndDataFile;
        }
        return null;
    }

    @Override
    public String getPlayerDataLockServerName(Player player) {
        File playerLockFile = new File(dataFile, player.getUniqueId() +".lock");
        return FileLock.getLockServerName(playerLockFile);
    }


    abstract LockAndDataFile getLockAndDataFile(File playerDataFile, Player player);

}
