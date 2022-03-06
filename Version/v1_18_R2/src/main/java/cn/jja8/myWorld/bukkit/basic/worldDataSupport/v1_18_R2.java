package cn.jja8.myWorld.bukkit.basic.worldDataSupport;

import cn.jja8.myWorld.all.veryUtil.FileLock;
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
import org.bukkit.craftbukkit.v1_18_R2.CraftServer;
import org.bukkit.craftbukkit.v1_18_R2.generator.CraftWorldInfo;
import org.bukkit.craftbukkit.v1_18_R2.generator.CustomWorldChunkManager;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.generator.BiomeProvider;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.generator.WorldInfo;

import javax.annotation.Nullable;
import java.io.*;
import java.lang.reflect.Field;
import java.util.*;

public class v1_18_R2 implements WorldDataSupport{
    File allWordFile;
    public v1_18_R2(File allWordFile){
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
    public World loadWorld(WorldCreator creator, String WordName,LoadingProgress loadingProgress) {
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
            BiomeProvider biomeProvider = creator.biomeProvider();
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
                    worldSession = Convertable.a(new File(allWordFile,WordName).toPath()).createAccess(name, actualDimension);
                } catch (IOException var23) {
                    throw new RuntimeException(var23);
                }

                boolean hardcore = creator.hardcore();
                WorldDataServer worlddata = (WorldDataServer)worldSession.a(console.registryreadops, console.datapackconfiguration, console.Q.g());
                if (worlddata == null) {
                    DedicatedServerProperties.a properties = new DedicatedServerProperties.a(Objects.toString(creator.seed()), ChatDeserializer.a(creator.generatorSettings().isEmpty() ? "{}" : creator.generatorSettings()), creator.generateStructures(), creator.type().name().toLowerCase(Locale.ROOT));
                    GeneratorSettings generatorsettings = GeneratorSettings.a(console.aU(), properties);
                    WorldSettings worldSettings = new WorldSettings(name, EnumGamemode.a(craftServer.getDefaultGameMode().getValue()), hardcore, EnumDifficulty.b, false, new GameRules(), console.datapackconfiguration);
                    worlddata = new WorldDataServer(worldSettings, generatorsettings, Lifecycle.stable());
                }

                worlddata.checkName(name);
                worlddata.a(console.getServerModName(), console.K().a());
//                if (console.options.has("forceUpgrade")) {
//                    net.minecraft.server.Main.a(worldSession, DataConverterRegistry.a(), console.options.has("eraseCache"), () -> {
//                        return true;
//                    }, worlddata.A());
//                }

                long j = BiomeManager.a(creator.seed());
                List<MobSpawner> list = ImmutableList.of(new MobSpawnerPhantom(), new MobSpawnerPatrol(), new MobSpawnerCat(), new VillageSiege(), new MobSpawnerTrader(worlddata));
                IRegistry<WorldDimension> iregistry = worlddata.A().d();
                WorldDimension worlddimension = iregistry.a(actualDimension);
                Holder holder;
                net.minecraft.world.level.chunk.ChunkGenerator chunkgenerator;
                if (worlddimension == null) {
                    holder = console.Q.d(IRegistry.N).c(DimensionManager.m);
                    chunkgenerator = GeneratorSettings.a(console.Q, (new Random()).nextLong());
                } else {
                    holder = worlddimension.a();
                    chunkgenerator = worlddimension.b();
                }

                WorldInfo worldInfo = new CraftWorldInfo(worlddata, worldSession, creator.environment(), (DimensionManager)holder.a());
                if (biomeProvider == null && generator != null) {
                    biomeProvider = generator.getDefaultBiomeProvider(worldInfo);
                }

                if (biomeProvider != null) {
                    WorldChunkManager worldChunkManager = new CustomWorldChunkManager(worldInfo, biomeProvider, console.Q.b(IRegistry.aP));
                    ChunkGeneratorAbstract cga;
                    if (chunkgenerator instanceof ChunkGeneratorAbstract && (cga = (ChunkGeneratorAbstract)chunkgenerator) == chunkgenerator) {
                        chunkgenerator = new ChunkGeneratorAbstract(cga.b, cga.k, worldChunkManager,((net.minecraft.world.level.chunk.ChunkGenerator)cga).j , cga.h);
                    }
                }

                String levelName = craftServer.getServer().a().p;
                ResourceKey worldKey;
                if (name.equals(levelName + "_nether")) {
                    worldKey = net.minecraft.world.level.World.f;
                } else if (name.equals(levelName + "_the_end")) {
                    worldKey = net.minecraft.world.level.World.g;
                } else {
                    worldKey = ResourceKey.a(IRegistry.O, new MinecraftKey(name.toLowerCase(Locale.ENGLISH)));
                }

                WorldServer internal = new WorldServer(console, console.az, worldSession, worlddata, worldKey, holder,new WorldLoadListenerLogger(11) {
                    int b = (11 * 2 + 1) * (11 * 2 + 1);
                    private int c;
                    boolean g = true;
                    public void a(ChunkCoordIntPair var0, @Nullable ChunkStatus var1) {
                        super.a(var0,var1);
                        if (var1 == ChunkStatus.o) {
                            ++this.c;
                        }
                        if (g){
                            loadingProgress.LoadingProgress(MathHelper.a(MathHelper.d((float)this.c * 100.0F / (float) b), 0, 100));
                        }
                    }
                    public void b() {
                        super.b();
                        g = false;
                    }
                } , chunkgenerator, worlddata.A().g(), j, creator.environment() == World.Environment.NORMAL ? list : ImmutableList.of(), true, creator.environment(), generator, biomeProvider);
                if (!worlds.containsKey(name.toLowerCase(Locale.ENGLISH))) {
                    return null;
                } else {
                    console.initWorld(internal, worlddata, worlddata, worlddata.A());
                    internal.b(true, true);
                    console.R.put(internal.aa(), internal);
                    craftServer.getServer().prepareLevels(internal.k().a.C, internal);
                    internal.O.a();
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
        return new Look(FileLock.git(allWordFile,WorldName));
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
