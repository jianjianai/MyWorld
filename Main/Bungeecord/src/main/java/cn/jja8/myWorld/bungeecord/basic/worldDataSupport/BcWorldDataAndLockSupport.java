package cn.jja8.myWorld.bungeecord.basic.worldDataSupport;

import java.io.File;

public class BcWorldDataAndLockSupport extends WorldDataAndLockSupport{
    public BcWorldDataAndLockSupport(File allWordFile){
        super(allWordFile);
    }

    @Override
    public boolean isWorldExistence(String worldName) {
        File folder = new File(super.allWorldFile, worldName);
        return folder.exists();
    }
}
