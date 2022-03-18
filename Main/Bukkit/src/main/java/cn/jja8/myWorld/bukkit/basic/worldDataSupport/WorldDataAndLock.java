package cn.jja8.myWorld.bukkit.basic.worldDataSupport;

import cn.jja8.myWorld.all.veryUtil.FileLock;

import java.io.File;

public abstract class WorldDataAndLock implements WorldDataLock{
    File worldFile;//世界的数据文件夹
    FileLock fileLock;


    @Override
    public void unloadWorldAndUnlock(boolean save) {
        unLoadWorld(save);
        fileLock.unLock();
    }

    @Override
    public byte[] getCustomDataByte(String dataName) {

    }

    @Override
    public void setCustomDataByte(String dataName, byte[] bytes) {
        File file = new File(worldFile,dataName);

    }

    public File getWorldFile() {
        return worldFile;
    }

    abstract void unLoadWorld(boolean save);
}
