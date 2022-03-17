package cn.jja8.myWorld.bukkit.basic.playerDataSupport;

import net.minecraft.server.v1_16_R3.NBTCompressedStreamTools;
import net.minecraft.server.v1_16_R3.NBTTagCompound;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class v1_16_R3 extends LockAndDataFileSupport{

    public v1_16_R3(File dataFile) {
        super(dataFile);
    }

    @Override
    LockAndDataFile getLockAndDataFile(File playerDataFile, Player player) {
        return new LockAndDataFile(playerDataFile,player);
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
            ((CraftPlayer)player).getHandle().saveData(nbttagcompound);
            try {
                NBTCompressedStreamTools.a(nbttagcompound,outputStream);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void loadFromInputStream(InputStream inputStream) {
            try {
                ((CraftPlayer)player).getHandle().loadData(NBTCompressedStreamTools.a(inputStream));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
