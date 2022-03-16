package cn.jja8.myWorld.bukkit.basic.playerDataSupport;

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

    private NBTTagCompound saveToNBT(Player player, NBTTagCompound nbttagcompound){
        if (nbttagcompound==null){
            nbttagcompound = new NBTTagCompound();
        }
        ((CraftPlayer)player).getHandle().saveData(nbttagcompound);
        return nbttagcompound;
    }

    private NBTTagCompound loadFromNBT(Player player, NBTTagCompound nbttagcompound){
        ((CraftPlayer)player).getHandle().loadData(nbttagcompound);
        return nbttagcompound;
    }

    public void saveData(Player player) {
        File file = new File(dataFile,player.getUniqueId().toString());
        try {
            NBTCompressedStreamTools.a(saveToNBT(player,new NBTTagCompound()),new FileOutputStream(file));
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    public void loadData(Player player) {
        File file = new File(dataFile,player.getUniqueId().toString());
        try {
            InputStream inputStream = new FileInputStream(file);
            try {
                loadFromNBT(player,NBTCompressedStreamTools.a(inputStream));
            } catch (IOException exception) {
                exception.printStackTrace();
            }
            try { inputStream.close(); } catch (IOException ignored) { }
        } catch (FileNotFoundException ignored) {

        }
    }

    @Override
    public PlayerDataLock getPlayerDataLock(Player player, String serverName) {
        return new Lock(FileLock.git(dataFile,player.getUniqueId().toString()));
    }

    public static class Lock implements PlayerDataLock{
        FileLock.lockWork lock;
        Lock(FileLock.lockWork lock){
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
         * 给世界上锁，如果世界已经被锁了就不能再次上锁。
         * @return true上锁成功 false上锁失败
         */
        @Override
        public boolean locked(String serverName) {
            try {
                lock.Lock(new FileLock.lockNews(serverName));
                return true;
            }catch (Error e){
                return false;
            }

        }

        /**
         * 给世界解锁，解锁服务器名称必须等于上锁服务器，否则不能解锁。
         * @return true解锁成功 false没有解锁
         */
        @Override
        public boolean unlock(String serverName) {
            try {
                lock.unlock();
                return true;
            }catch (Error e){
                return false;
            }
        }

    }
}
