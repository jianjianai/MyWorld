package cn.jja8.myWorld.bukkit.basic.worldDataSupport;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Lifecycle;
import net.minecraft.core.IRegistry;
import net.minecraft.core.RegistryMaterials;
import net.minecraft.nbt.DynamicOpsNBT;
import net.minecraft.nbt.NBTBase;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.resources.RegistryReadOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.server.level.WorldServer;
import net.minecraft.server.level.progress.WorldLoadListenerLogger;
import net.minecraft.util.MathHelper;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.entity.ai.village.VillageSiege;
import net.minecraft.world.entity.npc.MobSpawnerCat;
import net.minecraft.world.entity.npc.MobSpawnerTrader;
import net.minecraft.world.level.*;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.biome.WorldChunkManager;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.dimension.DimensionManager;
import net.minecraft.world.level.dimension.WorldDimension;
import net.minecraft.world.level.levelgen.ChunkGeneratorAbstract;
import net.minecraft.world.level.levelgen.GeneratorSettings;
import net.minecraft.world.level.levelgen.MobSpawnerPatrol;
import net.minecraft.world.level.levelgen.MobSpawnerPhantom;
import net.minecraft.world.level.storage.Convertable;
import net.minecraft.world.level.storage.WorldDataServer;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.craftbukkit.v1_17_R1.CraftServer;
import org.bukkit.craftbukkit.v1_17_R1.generator.CraftWorldInfo;
import org.bukkit.craftbukkit.v1_17_R1.generator.CustomWorldChunkManager;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.generator.BiomeProvider;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.generator.WorldInfo;

import javax.annotation.Nullable;
import java.io.*;
import java.lang.reflect.Field;
import java.util.*;

public class v1_17_R1 extends WorldDataAndLockSupport{
    public v1_17_R1(File allWordFile){
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
        public WorldDataAndLock(File worldFile, WorldCreator creator) {
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
        public World loadWorld( LoadingProgress loadingProgress) {
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
            try {
                //-----------------------------------------------------------------
                Validate.notNull(creator, "Creator may not be null");
                String name = creator.name();
                ChunkGenerator generator = creator.generator();
                BiomeProvider biomeProvider = creator.biomeProvider();
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

                    if (biomeProvider == null) {
                        biomeProvider = craftServer.getBiomeProvider(name);
                    }

                    ResourceKey actualDimension = switch (creator.environment()) {
                        case NORMAL, CUSTOM -> WorldDimension.b;
                        case NETHER -> WorldDimension.c;
                        case THE_END -> WorldDimension.d;
                    };
                    Convertable.ConversionSession worldSession;
                    try {
                        worldSession = Convertable.a(worldFile.toPath()).c(name, actualDimension);
                    } catch (IOException var24) {
                        throw new RuntimeException(var24);
                    }

                    MinecraftServer.convertWorld(worldSession);
                    boolean hardcore = creator.hardcore();
                    RegistryReadOps<NBTBase> registryreadops = RegistryReadOps.a(DynamicOpsNBT.a, console.aB.i(), console.l);
                    WorldDataServer worlddata = (WorldDataServer)worldSession.a(registryreadops, console.datapackconfiguration);
                    if (worlddata == null) {
                        Properties properties = new Properties();
                        properties.put("generator-settings", creator.generatorSettings());
                        properties.put("level-seed", Objects.toString(creator.seed()));
                        properties.put("generate-structures", Objects.toString(creator.generateStructures()));
                        properties.put("level-type", creator.type().getName());
                        GeneratorSettings generatorsettings = GeneratorSettings.a(console.getCustomRegistry(), properties);
                        WorldSettings worldSettings = new WorldSettings(name, EnumGamemode.getById(craftServer.getDefaultGameMode().getValue()), hardcore, EnumDifficulty.b, false, new GameRules(), console.datapackconfiguration);
                        worlddata = new WorldDataServer(worldSettings, generatorsettings, Lifecycle.stable());
                    }

                    worlddata.checkName(name);
                    worlddata.a(console.getServerModName(), console.getModded().isPresent());
//                if (console.options.has("forceUpgrade")) {
//                    net.minecraft.server.Main.convertWorld(worldSession, DataConverterRegistry.a(), console.options.has("eraseCache"), () -> true, worlddata.getGeneratorSettings().d().d().stream().map((entry) -> ResourceKey.a(IRegistry.P, (entry.getKey()).a())).collect(ImmutableSet.toImmutableSet()));
//                }

                    long j = BiomeManager.a(creator.seed());
                    List<MobSpawner> list = ImmutableList.of(new MobSpawnerPhantom(), new MobSpawnerPatrol(), new MobSpawnerCat(), new VillageSiege(), new MobSpawnerTrader(worlddata));
                    RegistryMaterials<WorldDimension> registrymaterials = worlddata.getGeneratorSettings().d();
                    WorldDimension worlddimension = registrymaterials.a(actualDimension);
                    DimensionManager dimensionmanager;
                    net.minecraft.world.level.chunk.ChunkGenerator chunkgenerator;
                    if (worlddimension == null) {
                        dimensionmanager = console.l.d(IRegistry.P).d(DimensionManager.k);
                        chunkgenerator = GeneratorSettings.a(console.l.d(IRegistry.aO), console.l.d(IRegistry.aH), (new Random()).nextLong());
                    } else {
                        dimensionmanager = worlddimension.b();
                        chunkgenerator = worlddimension.c();
                    }

                    WorldInfo worldInfo = new CraftWorldInfo(worlddata, worldSession, creator.environment(), dimensionmanager);
                    if (biomeProvider == null && generator != null) {
                        biomeProvider = generator.getDefaultBiomeProvider(worldInfo);
                    }

                    if (biomeProvider != null) {
                        WorldChunkManager worldChunkManager = new CustomWorldChunkManager(worldInfo, biomeProvider, console.l.b(IRegistry.aO));
                        if (chunkgenerator instanceof ChunkGeneratorAbstract) {
                            chunkgenerator = new ChunkGeneratorAbstract(worldChunkManager, chunkgenerator.e, ((ChunkGeneratorAbstract)chunkgenerator).g);
                        }
                    }

                    String levelName = craftServer.getServer().getDedicatedServerProperties().p;
                    ResourceKey worldKey;
                    if (name.equals(levelName + "_nether")) {
                        worldKey = net.minecraft.world.level.World.g;
                    } else if (name.equals(levelName + "_the_end")) {
                        worldKey = net.minecraft.world.level.World.h;
                    } else {
                        worldKey = ResourceKey.a(IRegistry.Q, new MinecraftKey(name.toLowerCase(Locale.ENGLISH)));
                    }

                    WorldServer internal = new WorldServer(console, console.az, worldSession, worlddata, worldKey, dimensionmanager, new WorldLoadListenerLogger(11) {
                        final int b = (11 * 2 + 1) * (11 * 2 + 1);
                        private int c;
                        boolean g = true;
                        public void a(ChunkCoordIntPair var0, @Nullable ChunkStatus var1) {
                            super.a(var0,var1);
                            if (var1 == ChunkStatus.m) {
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
                    }, chunkgenerator, worlddata.getGeneratorSettings().isDebugWorld(), j, creator.environment() == World.Environment.NORMAL ? list : ImmutableList.of(), true, creator.environment(), generator, biomeProvider);
                    if (!worlds.containsKey(name.toLowerCase(Locale.ENGLISH))) {
                        return null;
                    } else {
                        console.initWorld(internal, worlddata, worlddata, worlddata.getGeneratorSettings());
                        internal.setSpawnFlags(true, true);
                        console.R.put(internal.getDimensionKey(), internal);
                        craftServer.getServer().loadSpawn(internal.getChunkProvider().a.z, internal);
                        internal.G.tick();
                        Bukkit.getServer().getPluginManager().callEvent(new WorldLoadEvent(internal.getWorld()));
                        return internal.getWorld();
                    }
                }
            }catch (Exception exception){
                exception.printStackTrace();
                return null;
            }
        }

    }
}
