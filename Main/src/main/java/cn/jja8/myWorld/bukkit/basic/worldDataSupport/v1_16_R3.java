package cn.jja8.myWorld.bukkit.basic.worldDataSupport;

import cn.jja8.myWorld.all.veryUtil.Lock;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.mojang.serialization.Lifecycle;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.event.world.WorldInitEvent;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.generator.ChunkGenerator;

import java.io.*;
import java.lang.reflect.Field;
import java.util.*;

import net.minecraft.server.v1_16_R3.*;
import org.bukkit.craftbukkit.v1_16_R3.CraftServer;

public class v1_16_R3 implements WorldDataSupport{
    File allWordFile;
    public v1_16_R3(File allWordFile){
        this.allWordFile = allWordFile;
        allWordFile.mkdirs();
    }
    public boolean unloadWorld(World world, boolean save){
        CraftServer craftServer = (CraftServer) Bukkit.getServer();
        return craftServer.unloadWorld(world,save);
    }

    /**
     * 在指定路径加载世界，必须在主线程调用
     * */
    public World loadWorld(WorldCreator creator, String WordName) {
        try {
            CraftServer craftServer = (CraftServer) Bukkit.getServer();
            DedicatedServer console = craftServer.getServer();
            //利用反射拿到words
            Field worldsField = CraftServer.class.getDeclaredField("worlds");
            worldsField.setAccessible(true);
            Map<String, World> worlds = (Map<String, World>) worldsField.get(craftServer);
            //利用反射拿

            //-----------------------------------------------------------------
            Validate.notNull(creator, "Creator may not be null");
            String name = creator.name();
            ChunkGenerator generator = creator.generator();
            File folder = new File(new File(allWordFile,WordName), name);
            World world = craftServer.getWorld(name);
            if (world != null) {
                return world;
            } else if (folder.exists() && !folder.isDirectory()) {
                throw new IllegalArgumentException("File exists with the name '" + name + "' and isn't a folder");
            } else {
                if (generator == null) {
                    generator = craftServer.getGenerator(name);
                }

                ResourceKey actualDimension = WorldDimension.OVERWORLD;
                switch (creator.environment()) {
                    case NORMAL:
                        actualDimension = WorldDimension.OVERWORLD;
                        break;
                    case NETHER:
                        actualDimension = WorldDimension.THE_NETHER;
                        break;
                    case THE_END:
                        actualDimension = WorldDimension.THE_END;
                        break;
                }

                Convertable.ConversionSession worldSession;
                try {
                    worldSession = Convertable.a(craftServer.getWorldContainer().toPath()).c(name, actualDimension);
                } catch (IOException var21) {
                    throw new RuntimeException(var21);
                }

                MinecraftServer.convertWorld(worldSession);
                boolean hardcore = creator.hardcore();
                RegistryReadOps<NBTBase> registryreadops = RegistryReadOps.a(DynamicOpsNBT.a, console.dataPackResources.h(), console.customRegistry);
                WorldDataServer worlddata = (WorldDataServer)worldSession.a(registryreadops, console.datapackconfiguration);
                if (worlddata == null) {
                    Properties properties = new Properties();
                    properties.put("generator-settings", creator.generatorSettings());
                    properties.put("level-seed", Objects.toString(creator.seed()));
                    properties.put("generate-structures", Objects.toString(creator.generateStructures()));
                    properties.put("level-type", creator.type().getName());
                    GeneratorSettings generatorsettings = GeneratorSettings.a(console.getCustomRegistry(), properties);
                    WorldSettings worldSettings = new WorldSettings(name, EnumGamemode.getById(craftServer.getDefaultGameMode().getValue()), hardcore, EnumDifficulty.EASY, false, new GameRules(), console.datapackconfiguration);
                    worlddata = new WorldDataServer(worldSettings, generatorsettings, Lifecycle.stable());
                }

                worlddata.checkName(name);
                worlddata.a(console.getServerModName(), console.getModded().isPresent());
                if (console.options.has("forceUpgrade")) {
                    net.minecraft.server.v1_16_R3.Main.convertWorld(worldSession, DataConverterRegistry.a(), console.options.has("eraseCache"), () -> {
                        return true;
                    }, (ImmutableSet)worlddata.getGeneratorSettings().d().d().stream().map((entry) -> {
                        return ResourceKey.a(IRegistry.K, ((ResourceKey)entry.getKey()).a());
                    }).collect(ImmutableSet.toImmutableSet()));
                }

                long j = BiomeManager.a(creator.seed());
                List<MobSpawner> list = ImmutableList.of(new MobSpawnerPhantom(), new MobSpawnerPatrol(), new MobSpawnerCat(), new VillageSiege(), new MobSpawnerTrader(worlddata));
                RegistryMaterials<WorldDimension> registrymaterials = worlddata.getGeneratorSettings().d();
                WorldDimension worlddimension = registrymaterials.a(actualDimension);
                DimensionManager dimensionmanager;
                Object chunkgenerator;
                if (worlddimension == null) {
                    dimensionmanager = console.customRegistry.a().d(DimensionManager.OVERWORLD);
                    chunkgenerator = GeneratorSettings.a(console.customRegistry.b(IRegistry.ay), console.customRegistry.b(IRegistry.ar), (new Random()).nextLong());
                } else {
                    dimensionmanager = worlddimension.b();
                    chunkgenerator = worlddimension.c();
                }

                ResourceKey<net.minecraft.server.v1_16_R3.World> worldKey = ResourceKey.a(IRegistry.L, new MinecraftKey(name.toLowerCase(Locale.ENGLISH)));
                WorldServer internal = new WorldServer(console, console.executorService, worldSession, worlddata, worldKey, dimensionmanager, craftServer.getServer().worldLoadListenerFactory.create(11), (net.minecraft.server.v1_16_R3.ChunkGenerator)chunkgenerator, worlddata.getGeneratorSettings().isDebugWorld(), j, creator.environment() == World.Environment.NORMAL ? list : ImmutableList.of(), true, creator.environment(), generator);
                if (!worlds.containsKey(name.toLowerCase(Locale.ENGLISH))) {
                    return null;
                } else {
                    console.initWorld(internal, worlddata, worlddata, worlddata.getGeneratorSettings());
                    internal.setSpawnFlags(true, true);
                    console.worldServer.put(internal.getDimensionKey(), internal);
                    craftServer.getPluginManager().callEvent(new WorldInitEvent(internal.getWorld()));
                    craftServer.getServer().loadSpawn(internal.getChunkProvider().playerChunkMap.worldLoadListener, internal);
                    craftServer.getPluginManager().callEvent(new WorldLoadEvent(internal.getWorld()));
                    return internal.getWorld();
                }
            }
        }catch (Exception exception){
            exception.printStackTrace();
            return null;
        }


    }

    /**
     * 获取某世界的锁
     */
    @Override
    public WorldDataLock getWorldDataLock(String WorldName) {
        return new Look(Lock.git(allWordFile,WorldName));
    }

    /**
     * 获取某世界的自定义数据输入流
     */
    @Override
    public InputStream getCustomDataInputStream(String WorldName, String dataName) {
        File file = new File(allWordFile,WorldName);
        try {
            return new FileInputStream(new File(file,dataName));
        } catch (FileNotFoundException e) {
            return null;
        }
    }

    /**
     * 获取某世界的自定义数据输出流
     */
    @Override
    public OutputStream getCustomDataOutputStream(String WorldName, String dataName) {
        File file = new File(allWordFile,WorldName);
        File file1 = new File(file,dataName);
        file1.getParentFile().mkdirs();
        try {
            return new FileOutputStream(file1);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            throw new Error("无法打开文件输出流！！");
        }
    }

    /**
     * 删除掉指定世界
     */
    @Override
    public void delWorld(String wordName) {
        File file = new File(allWordFile,wordName);
        delFolder(file);
    }

    /**
     * 返回这个世界是否存在
     */
    @Override
    public boolean isWorldExistence(String name) {
        File 配置文件夹 = new File(allWordFile,name);
        return 配置文件夹.exists() & 配置文件夹.isDirectory();
    }

    /**
     * 递归删除文件夹
     * */
    private void delFolder(File file) {
        File[] list = file.listFiles();  //无法做到list多层文件夹数据
        if (list != null) {
            for (File temp : list) {     //先去递归删除子文件夹及子文件
                delFolder(temp);   //注意这里是递归调用
            }
        }
        file.delete();     //再删除自己本身的文件夹
    }

    public static class Look implements WorldDataLock{
        Lock.lockWork lock;
        Look(Lock.lockWork lock){
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
                lock.Lock(new Lock.lockNews(serverName));
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

        /**
         * 获取上锁服务器的名称
         *
         * @return null 没有被锁
         */
        @Override
        public String gitLockName() {
            return lock.news().服务器名称;
        }
    }
}
