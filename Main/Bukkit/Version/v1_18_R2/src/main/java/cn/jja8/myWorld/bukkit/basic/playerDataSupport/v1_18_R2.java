package cn.jja8.myWorld.bukkit.basic.playerDataSupport;

import net.minecraft.nbt.NBTCompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import org.bukkit.craftbukkit.v1_18_R2.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.io.*;

public class v1_18_R2 extends LockAndDataFileSupport{

    public v1_18_R2(File dataFile) {
        super(dataFile);
    }

    @Override
    v1_16_R3.LockAndDataFile getLockAndDataFile(File playerDataFile, Player player) {
        return new v1_16_R3.LockAndDataFile(playerDataFile,player);
    }

    public static class LockAndDataFile extends cn.jja8.myWorld.bukkit.basic.playerDataSupport.LockAndDataFile {
        Player player;
        public LockAndDataFile(File playerDataFile, Player player) {
            super(playerDataFile);
            this.player = player;
        }
        @Override
        public void saveToOutputStream(OutputStream outputStream){
            NBTTagCompound nbttagcompound = new NBTTagCompound();
            ((CraftPlayer)player).getHandle().b(nbttagcompound);
            try {
                NBTCompressedStreamTools.a(nbttagcompound,outputStream);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void loadFromInputStream(InputStream inputStream) {
            try {
                ((CraftPlayer)player).getHandle().a(NBTCompressedStreamTools.a(inputStream));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}