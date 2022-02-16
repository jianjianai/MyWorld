package cn.jja8.myWorld.bukkit.basic.playerDataSupport;

import cn.jja8.myWorld.all.veryUtil.FileLock;
import com.mojang.datafixers.DataFixer;
import net.minecraft.nbt.GameProfileSerializer;
import net.minecraft.nbt.NBTCompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.storage.WorldNBTStorage;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_17_R1.CraftServer;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.io.*;
import java.lang.reflect.Field;

public class v1_17_R1 implements PlayerDataSupport{
    File dataFile;
    public v1_17_R1(File dataFile){
        this.dataFile = dataFile;
        dataFile.mkdirs();
    }

    private NBTTagCompound saveToNBT(Player player, NBTTagCompound nbttagcompound){
        nbttagcompound = ((CraftPlayer)player).getHandle().save(nbttagcompound);
        return nbttagcompound;
    }

    private NBTTagCompound loadFromNBT(Player player,NBTTagCompound nbttagcompound){
        Location location = player.getLocation();
        try {
            //利用反射拿到playerFileData的a
            Field field = WorldNBTStorage.class.getDeclaredField("a");
            field.setAccessible(true);
            DataFixer a = (DataFixer) field.get(((CraftServer)Bukkit.getServer()).getHandle().r);

            //加载数据
            int i = nbttagcompound.hasKeyOfType("DataVersion", 3) ? nbttagcompound.getInt("DataVersion") : -1;
            ((CraftPlayer)player).getHandle().load(GameProfileSerializer.a(a, DataFixTypes.b, nbttagcompound, i));

        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();

        }
        player.teleport(location);
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

    /**
     * 获得这个玩家的锁
     */
    @Override
    public PlayerDataLock getPlayerDataLock(Player player) {
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
