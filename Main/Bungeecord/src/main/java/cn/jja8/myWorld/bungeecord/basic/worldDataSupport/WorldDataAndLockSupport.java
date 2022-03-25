package cn.jja8.myWorld.bungeecord.basic.worldDataSupport;

import cn.jja8.myWorld.all.veryUtil.FileLock;

import java.io.File;

public abstract class WorldDataAndLockSupport implements WorldDataSupport{
    File allWorldFile;
    public WorldDataAndLockSupport(File allWorldFile) {
        this.allWorldFile = allWorldFile;
        allWorldFile.mkdirs();
    }

    @Override
    public String gitLockServerName(String worldName) {
        File lockFile = new File(allWorldFile,worldName+".lock");
        return FileLock.getLockServerName(lockFile);
    }
}
