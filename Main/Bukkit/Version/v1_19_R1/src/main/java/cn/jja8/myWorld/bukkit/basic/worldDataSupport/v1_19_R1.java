package cn.jja8.myWorld.bukkit.basic.worldDataSupport;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Lifecycle;
import net.minecraft.core.Holder;
import net.minecraft.core.IRegistry;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.server.dedicated.DedicatedServerProperties;
import net.minecraft.server.level.WorldServer;
import net.minecraft.server.level.progress.WorldLoadListenerLogger;
import net.minecraft.util.ChatDeserializer;
import net.minecraft.util.MathHelper;
import net.minecraft.util.datafix.DataConverterRegistry;
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
import org.bukkit.craftbukkit.v1_19_R1.CraftServer;
import org.bukkit.craftbukkit.v1_19_R1.generator.CraftWorldInfo;
import org.bukkit.craftbukkit.v1_19_R1.generator.CustomWorldChunkManager;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.generator.BiomeProvider;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.generator.WorldInfo;

import java.io.*;
import java.lang.reflect.Field;
import java.util.*;

public class v1_19_R1 extends WorldDataAndLockSupport{
    public v1_19_R1(File allWordFile){
        super(allWordFile);
    }

    @Override
    WorldDataAndLock getWorldDataAndLock(File worldFile,WorldCreator creator) {
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

        private World load(WorldCreator creator, LoadingProgress loadingProgress) {
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
                        worldSession = Convertable.a(worldFile.toPath()).createAccess(name, actualDimension);
                    } catch (IOException var21) {
                        throw new RuntimeException(var21);
                    }

                    boolean hardcore = creator.hardcore();
                    WorldDataServer worlddata = (WorldDataServer)worldSession.a(this.console.registryreadops, this.console.datapackconfiguration, this.console.N.g());
                    if (worlddata == null) {
                        DedicatedServerProperties.a properties = new DedicatedServerProperties.a(Objects.toString(creator.seed()), ChatDeserializer.a(creator.generatorSettings().isEmpty() ? "{}" : creator.generatorSettings()), creator.generateStructures(), creator.type().name().toLowerCase(Locale.ROOT));
                        GeneratorSettings generatorsettings = properties.a(this.console.aX());
                        WorldSettings worldSettings = new WorldSettings(name, EnumGamemode.a(craftServer.getDefaultGameMode().getValue()), hardcore, EnumDifficulty.b, false, new GameRules(), this.console.datapackconfiguration);
                        worlddata = new WorldDataServer(worldSettings, generatorsettings, Lifecycle.stable());
                    }

                    worlddata.checkName(name);
                    worlddata.a(this.console.getServerModName(), this.console.J().a());

//                    if (this.console.options.has("forceUpgrade")) {
//                        net.minecraft.server.Main.a(worldSession, DataConverterRegistry.a(), this.console.options.has("eraseCache"), () -> {
//                            return true;
//                        }, worlddata.A());
//                    }

                    long j = BiomeManager.a(creator.seed());
                    List<MobSpawner> list = ImmutableList.of(new MobSpawnerPhantom(), new MobSpawnerPatrol(), new MobSpawnerCat(), new VillageSiege(), new MobSpawnerTrader(worlddata));
                    IRegistry<WorldDimension> iregistry = worlddata.A().d();
                    WorldDimension worlddimension = iregistry.a(actualDimension);
                    WorldInfo worldInfo = new CraftWorldInfo(worlddata, worldSession, creator.environment(), worlddimension.a().a());
                    if (biomeProvider == null && generator != null) {
                        biomeProvider = generator.getDefaultBiomeProvider(worldInfo);
                    }

                    String levelName = craftServer.getServer().a().m;
                    ResourceKey worldKey;
                    if (name.equals(levelName + "_nether")) {
                        worldKey = net.minecraft.world.level.World.f;
                    } else if (name.equals(levelName + "_the_end")) {
                        worldKey = net.minecraft.world.level.World.g;
                    } else {
                        worldKey = ResourceKey.a(IRegistry.P, new MinecraftKey(name.toLowerCase(Locale.ENGLISH)));
                    }

                    WorldServer internal = new WorldServer(this.console, this.console.ar, worldSession, worlddata, worldKey, worlddimension, new WorldLoadListenerLogger(11) {
                        int b = (11 * 2 + 1) * (11 * 2 + 1);
                        private int c;
                        boolean g = true;
                        public void a(ChunkCoordIntPair var0,ChunkStatus var1) {
                            super.a(var0,var1);
                            if (var1 == ChunkStatus.o) {
                                ++this.c;
                            }
                            if (g){
                                loadingProgress.loadingProgress(MathHelper.a(MathHelper.d((float)this.c * 100.0F / (float) b), 0, 100));
                            }
                        }
                        public void b() {
                            super.b();
                            g = false;
                        }
                    }, worlddata.A().g(), j, creator.environment() == World.Environment.NORMAL ? list : ImmutableList.of(), true, creator.environment(), generator, biomeProvider);
                    if (!this.worlds.containsKey(name.toLowerCase(Locale.ENGLISH))) {
                        return null;
                    } else {
                        this.console.initWorld(internal, worlddata, worlddata, worlddata.A());
                        internal.b(true, true);
                        this.console.O.put(internal.ab(), internal);
                        craftServer.getServer().prepareLevels(internal.k().a.D, internal);
                        internal.P.a();
                        craftServer.getPluginManager().callEvent(new WorldLoadEvent(internal.getWorld()));
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
