package cn.jja8.myWorld.bukkit.basic.worldDataSupport;

import cn.jja8.myWorld.all.veryUtil.FileLock;

import java.io.File;

public abstract class WorldDataAndLockSupport implements WorldDataSupport{
    File allWorldFile;
    public WorldDataAndLockSupport(File allWorldFile) {
        this.allWorldFile = allWorldFile;
        allWorldFile.mkdirs();
    }

    @Override
    public WorldDataLock getWorldDataLock(String worldName, String serverName) {
        File worldFile = new File(allWorldFile,worldName);
        File lockFile = new File(allWorldFile,worldName+".lock");
        FileLock fileLock = FileLock.getFileLock(lockFile,serverName);
        if (fileLock!=null){
            WorldDataAndLock worldDataAndLock = getWorldDataAndLock(worldFile,worldName);
            worldDataAndLock.fileLock = fileLock;
            worldDataAndLock.worldDataFile = new File(worldFile,"Data");
            return worldDataAndLock;
        }
        return null;
    }

    abstract WorldDataAndLock getWorldDataAndLock(File worldFile, String worldName);

    @Override
    public String gitLockServerName(String worldName) {
        File lockFile = new File(allWorldFile,worldName+".lock");
        return FileLock.getLockServerName(lockFile);
    }
}
