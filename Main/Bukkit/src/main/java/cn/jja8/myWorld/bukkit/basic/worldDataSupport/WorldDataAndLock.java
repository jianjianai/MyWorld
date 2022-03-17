package cn.jja8.myWorld.bukkit.basic.worldDataSupport;

import cn.jja8.myWorld.all.veryUtil.FileLock;

import java.io.File;

public abstract class WorldDataAndLock implements WorldDataLock{
    private final File worldFile;
    private final FileLock fileLock;


    @Override
    public boolean unloadWorldAndUnlock(boolean save) {
        return false;
    }

    @Override
    public byte[] getCustomDataByte(String WorldName, String dataName) {
        return new byte[0];
    }

    @Override
    public void setCustomDataByte(String WorldName, String dataName, byte[] bytes) {

    }
}
