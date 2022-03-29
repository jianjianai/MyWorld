package cn.jja8.myWorld.bukkit.basic.worldDataSupport;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Lifecycle;
import net.minecraft.server.v1_16_R3.*;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.craftbukkit.v1_16_R3.CraftServer;
import org.bukkit.event.world.WorldInitEvent;
import org.bukkit.event.world.WorldLoadEvent;

import javax.annotation.Nullable;
import java.io.*;
import java.lang.reflect.Field;
import java.util.*;

public class v1_16_R3 extends WorldDataAndLockSupport{
    public v1_16_R3(File allWordFile){
        super(allWordFile);
    }

    @Override
    WorldDataAndLock getWorldDataAndLock(File worldFile, WorldCreator creator) {
        return new WorldDataAndLock(worldFile,creator);
    }

    @Override
    public boolean isWorldExistence(String worldName) {
        File folder = new File(super.allWorldFile, worldName);
        return folder.exists();
    }

    public static class WorldDataAndLock extends cn.jja8.myWorld.bukkit.basic.worldDataSupport.WorldDataAndLock {
        File worldFile;
        World world;
        WorldCreator creator;

        DedicatedServer console;
        CraftServer craftServer;
        Map<String, World> worlds;
        public WorldDataAndLock(File worldFile,WorldCreator creator) {
            this.creator = creator;
            this.worldFile = worldFile;
            worldFile.mkdirs();

            craftServer = (CraftServer) Bukkit.getServer();
            console = craftServer.getServer();
            //利用反射拿到words
            try {
                Field worldsField = CraftServer.class.getDeclaredField("worlds");
                worldsField.setAccessible(true);
                worlds = (Map<String, World>) worldsField.get(craftServer);
            } catch (NoSuchFieldException | IllegalAccessException noSuchFieldException) {
                noSuchFieldException.printStackTrace();
            }
        }

        @Override
        public void unloadWorld(boolean save) {
            CraftServer craftServer = (CraftServer) Bukkit.getServer();
            craftServer.unloadWorld(world,save);
        }

        @Override
        public World loadWorld(LoadingProgress loadingProgress) {
            if (world!=null) return world;
            world = load(creator,loadingProgress);
            return world;
        }

        @Override
        public void delWorld() {
            delFolder(worldFile);
        }


        @Override
        public WorldCreator getWorldCreator() {
            return creator;
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

        private World load(WorldCreator creator, LoadingProgress loadingProgress){
            Validate.notNull(creator, "Creator may not be null");
            String name = creator.name();
            org.bukkit.generator.ChunkGenerator generator = creator.generator();
            File folder = new File(worldFile, name);
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
                    worldSession = Convertable.a(worldFile.toPath()).c(name, actualDimension);
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
//                if (console.options.has("forceUpgrade")) {
//                    net.minecraft.server.v1_16_R3.Main.convertWorld(worldSession, DataConverterRegistry.a(), console.options.has("eraseCache"), () -> true, worlddata.getGeneratorSettings().d().d().stream().map((entry) -> ResourceKey.a(IRegistry.K, (entry.getKey()).a())).collect(ImmutableSet.toImmutableSet()));
//                }

                long j = BiomeManager.a(creator.seed());
                List<MobSpawner> list = ImmutableList.of(new MobSpawnerPhantom(), new MobSpawnerPatrol(), new MobSpawnerCat(), new VillageSiege(), new MobSpawnerTrader(worlddata));
                RegistryMaterials<WorldDimension> registrymaterials = worlddata.getGeneratorSettings().d();
                WorldDimension worlddimension = registrymaterials.a(actualDimension);
                DimensionManager dimensionmanager;
                ChunkGenerator chunkgenerator;
                if (worlddimension == null) {
                    dimensionmanager = console.customRegistry.a().d(DimensionManager.OVERWORLD);
                    chunkgenerator = GeneratorSettings.a(console.customRegistry.b(IRegistry.ay), console.customRegistry.b(IRegistry.ar), (new Random()).nextLong());
                } else {
                    dimensionmanager = worlddimension.b();
                    chunkgenerator = worlddimension.c();
                }

                ResourceKey<net.minecraft.server.v1_16_R3.World> worldKey = ResourceKey.a(IRegistry.L, new MinecraftKey(name.toLowerCase(Locale.ENGLISH)));
                WorldServer internal = new WorldServer(console, console.executorService, worldSession, worlddata, worldKey, dimensionmanager, new WorldLoadListenerLogger(11) {
                    final int b = (11 * 2 + 1) * (11 * 2 + 1);
                    boolean g = true;
                    private int c;

                    public void a(ChunkCoordIntPair var0, @Nullable ChunkStatus var1) {
                        super.a(var0,var1);
                        if (var1 == ChunkStatus.FULL) {
                            ++this.c;
                        }
                        if (g){
                            loadingProgress.loadingProgress(MathHelper.clamp(MathHelper.d((float)this.c * 100.0F / (float) b), 0, 100));
                        }
                    }
                    public void b() {
                        super.b();
                        g = false;
                    }
                }, chunkgenerator, worlddata.getGeneratorSettings().isDebugWorld(), j, creator.environment() == World.Environment.NORMAL ? list : ImmutableList.of(), true, creator.environment(), generator);
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
        }

    }
}
