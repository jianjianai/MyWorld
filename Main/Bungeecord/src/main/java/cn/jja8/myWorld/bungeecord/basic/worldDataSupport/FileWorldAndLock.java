package cn.jja8.myWorld.bungeecord.basic.worldDataSupport;

import java.io.*;

public class FileWorldAndLock implements WorldDataSupport {
    File allWordFile;
    public FileWorldAndLock(File allWordFile){
        this.allWordFile = allWordFile;
        allWordFile.mkdirs();
    }
    /**
     * 获取某世界的锁
     */
    @Override
    public WorldDataLock getWorldDataLock(String WorldName) {
        return new Look(FileLock.git(allWordFile,WorldName));
    }
    /**
     * 返回这个世界是否存在
     */
    @Override
    public boolean isWorldExistence(String name) {
        File 配置文件夹 = new File(allWordFile,name);
        return 配置文件夹.exists() & 配置文件夹.isDirectory();
    }

    public static class Look implements WorldDataLock {
        FileLock.lockWork lock;
        Look(FileLock.lockWork lock){
            this.lock=lock;
        }
        /**
         * 返回世界是否被锁
         *
         * @return true锁了 false没锁
         */
        @Override
        public boolean isLocked() {
            return lock.isLocked();
        }
        /**
         * 获取上锁服务器的名称
         * @return null 没有被锁
         */
        @Override
        public String gitLockName() {
            return lock.news().服务器名称;
        }
    }
}
