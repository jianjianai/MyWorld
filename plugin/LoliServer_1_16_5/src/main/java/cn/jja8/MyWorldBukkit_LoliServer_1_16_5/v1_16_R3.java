//package cn.jja8.MyWorldBukkit_LoliServer_1_16_5;
//
//import cn.jja8.myWorld.all.veryUtil.FileLock;
//import cn.jja8.myWorld.bukkit.basic.worldDataSupport.WorldDataLock;
//import cn.jja8.myWorld.bukkit.basic.worldDataSupport.WorldDataSupport;
//import com.google.common.collect.ImmutableList;
//import com.mojang.serialization.Lifecycle;
//import net.minecraft.nbt.INBT;
//import net.minecraft.nbt.NBTDynamicOps;
//import net.minecraft.server.MinecraftServer;
//import net.minecraft.server.dedicated.DedicatedServer;
//import net.minecraft.util.RegistryKey;
//import net.minecraft.util.ResourceLocation;
//import net.minecraft.util.registry.Registry;
//import net.minecraft.util.registry.SimpleRegistry;
//import net.minecraft.util.registry.WorldSettingsImport;
//import net.minecraft.village.VillageSiege;
//import net.minecraft.world.*;
//import net.minecraft.world.biome.BiomeManager;
//import net.minecraft.world.gen.settings.DimensionGeneratorSettings;
//import net.minecraft.world.server.ServerWorld;
//import net.minecraft.world.spawner.*;
//import net.minecraft.world.storage.SaveFormat;
//import net.minecraft.world.storage.ServerWorldInfo;
//import org.apache.commons.lang.Validate;
//import org.bukkit.Bukkit;
//import org.bukkit.Difficulty;
//import org.bukkit.World;
//import org.bukkit.WorldCreator;
//import org.bukkit.craftbukkit.v1_16_R3.CraftServer;
//import org.bukkit.event.world.WorldLoadEvent;
//import org.bukkit.generator.ChunkGenerator;
//
//import java.io.*;
//import java.lang.reflect.Field;
//import java.util.*;
//
//public class v1_16_R3 implements WorldDataSupport {
//    File allWordFile;
//    public v1_16_R3(File allWordFile){
//        this.allWordFile = allWordFile;
//        allWordFile.mkdirs();
//    }
//    public boolean unloadWorld(World world, boolean save){
//        CraftServer craftServer = (CraftServer) Bukkit.getServer();
//        return craftServer.unloadWorld(world,save);
//    }
//
//    /**
//     * 在指定路径加载世界，必须在主线程调用
//     * */
//    public World loadWorld(WorldCreator creator, String WordName) {
//        try {
//            CraftServer craftServer = (CraftServer) Bukkit.getServer();
//            DedicatedServer console = craftServer.getServer();
//            //利用反射拿到words
//            Field worldsField = CraftServer.class.getDeclaredField("worlds");
//            worldsField.setAccessible(true);
//            Map<String, World> worlds = (Map<String, World>) worldsField.get(craftServer);
//            //利用反射拿
//
//            //-----------------------------------------------------------------
//            Validate.notNull(creator, "Creator may not be null");
//            String name = creator.name();
//            ChunkGenerator generator = creator.generator();
//            File folder = new File(new File(allWordFile,WordName), name);
//            World world = craftServer.getWorld(name);
//            if (world != null) {
//                return world;
//            } else if (folder.exists() && !folder.isDirectory()) {
//                throw new IllegalArgumentException("File exists with the name '" + name + "' and isn't a folder");
//            } else {
//                if (generator == null) {
//                    generator = craftServer.getGenerator(name);
//                }
//
//                RegistryKey actualDimension;
//                switch(creator.environment()) {
//                    case NORMAL:
//                        actualDimension = Dimension.field_236053_b_;
//                        break;
//                    case NETHER:
//                        actualDimension = Dimension.field_236054_c_;
//                        break;
//                    case THE_END:
//                        actualDimension = Dimension.field_236055_d_;
//                        break;
//                    default:
//                        throw new IllegalArgumentException("Illegal dimension");
//                }
//
//                SaveFormat.LevelSave worldSession;
//                try {
//                    worldSession = SaveFormat.func_237269_a_(new File(allWordFile,WordName).toPath()).func_237274_c_(name, actualDimension);
//                } catch (IOException var21) {
//                    throw new RuntimeException(var21);
//                }
//
//                MinecraftServer.func_240777_a_(worldSession);
//                boolean hardcore = creator.hardcore();
//                WorldSettingsImport<INBT> registryreadops = WorldSettingsImport.func_244335_a(NBTDynamicOps.field_210820_a, console.getDataPackRegistries().func_240970_h_(), console.field_240767_f_);
//                ServerWorldInfo worlddata = (ServerWorldInfo)worldSession.func_237284_a_(registryreadops, console.datapackconfiguration);
//                if (worlddata == null) {
//                    Properties properties = new Properties();
//                    properties.put("generator-settings", creator.generatorSettings());
//                    properties.put("level-seed", Objects.toString(creator.seed()));
//                    properties.put("generate-structures", Objects.toString(creator.generateStructures()));
//                    properties.put("level-type", creator.type().getName());
//                    DimensionGeneratorSettings generatorsettings = DimensionGeneratorSettings.func_242753_a(console.func_244267_aX(), properties);
//                    WorldSettings worldSettings = new WorldSettings(name, GameType.func_77146_a(craftServer.getDefaultGameMode().getValue()), hardcore, Difficulty.EASY, false, new GameRules(), console.datapackconfiguration);
//                    worlddata = new ServerWorldInfo(worldSettings, generatorsettings, Lifecycle.stable());
//                }
//
//                worlddata.checkName(name);
//                worlddata.func_230412_a_(console.getServerModName(), console.func_230045_q_().isPresent());
//                long j = BiomeManager.func_235200_a_(creator.seed());
//                List<ISpecialSpawner> list = ImmutableList.of(new PhantomSpawner(), new PatrolSpawner(), new CatSpawner(), new VillageSiege(), new WanderingTraderSpawner(worlddata));
//                SimpleRegistry<Dimension> registrymaterials = worlddata.func_230418_z_().func_236224_e_();
//                Dimension worlddimension = (Dimension)registrymaterials.func_230516_a_(actualDimension);
//                DimensionType dimensionmanager;
//                Object chunkgenerator;
//                if (worlddimension == null) {
//                    dimensionmanager = (DimensionType)console.field_240767_f_.func_230520_a_().func_243576_d(DimensionType.field_235999_c_);
//                    chunkgenerator = DimensionGeneratorSettings.func_242750_a(console.field_240767_f_.func_243612_b(Registry.field_239720_u_), console.field_240767_f_.func_243612_b(Registry.field_243549_ar), (new Random()).nextLong());
//                } else {
//                    dimensionmanager = worlddimension.func_236063_b_();
//                    chunkgenerator = worlddimension.func_236064_c_();
//                }
//
//                RegistryKey<net.minecraft.world.World> worldKey = RegistryKey.func_240903_a_(Registry.field_239699_ae_, new ResourceLocation(name.toLowerCase(Locale.ENGLISH)));
//                net.minecraft.world.World.generatorAndEnvSetter.setAll(generator, creator.environment());
//                ServerWorld internal = new ServerWorld(console, console.field_213217_au, worldSession, worlddata, worldKey, dimensionmanager, craftServer.getServer().field_213220_d.create(11), (net.minecraft.world.gen.ChunkGenerator)chunkgenerator, worlddata.func_230418_z_().func_236227_h_(), j, creator.environment() == World.Environment.NORMAL ? list : ImmutableList.of(), true);
//                net.minecraft.world.World.generatorAndEnvSetter.reset();
//                if (!worlds.containsKey(name.toLowerCase(Locale.ENGLISH))) {
//                    return null;
//                } else {
//                    console.initWorld(internal, worlddata, worlddata, worlddata.func_230418_z_());
//                    internal.func_72891_a(true, true);
//                    console.field_71305_c.put(internal.func_234923_W_(), internal);
//                    craftServer.getServer().loadSpawn(internal.func_72863_F().field_217237_a.field_219266_t, internal);
//                    craftServer.getPluginManager().callEvent(new WorldLoadEvent(internal.getWorld()));
//                    return internal.getWorld();
//                }
//            }
//        }catch (Exception exception){
//            exception.printStackTrace();
//            return null;
//        }
//
//
//    }
//
//    /**
//     * 获取某世界的锁
//     */
//    @Override
//    public WorldDataLock getWorldDataLock(String WorldName) {
//        return new Look(FileLock.git(allWordFile,WorldName));
//    }
//
//    /**
//     * 获取某世界的自定义数据输入流
//     */
//    @Override
//    public InputStream getCustomDataInputStream(String WorldName, String dataName) {
//        File file = new File(allWordFile,WorldName);
//        try {
//            return new FileInputStream(new File(file,dataName));
//        } catch (FileNotFoundException e) {
//            return null;
//        }
//    }
//
//    /**
//     * 获取某世界的自定义数据输出流
//     */
//    @Override
//    public OutputStream getCustomDataOutputStream(String WorldName, String dataName) {
//        File file = new File(allWordFile,WorldName);
//        File file1 = new File(file,dataName);
//        file1.getParentFile().mkdirs();
//        try {
//            return new FileOutputStream(file1);
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//            throw new Error("无法打开文件输出流！！");
//        }
//    }
//
//    /**
//     * 删除掉指定世界
//     */
//    @Override
//    public void delWorld(String wordName) {
//        File file = new File(allWordFile,wordName);
//        delFolder(file);
//    }
//
//    /**
//     * 返回这个世界是否存在
//     */
//    @Override
//    public boolean isWorldExistence(String name) {
//        File 配置文件夹 = new File(allWordFile,name);
//        return 配置文件夹.exists() & 配置文件夹.isDirectory();
//    }
//
//    /**
//     * 递归删除文件夹
//     * */
//    private void delFolder(File file) {
//        File[] list = file.listFiles();  //无法做到list多层文件夹数据
//        if (list != null) {
//            for (File temp : list) {     //先去递归删除子文件夹及子文件
//                delFolder(temp);   //注意这里是递归调用
//            }
//        }
//        file.delete();     //再删除自己本身的文件夹
//    }
//
//    public static class Look implements WorldDataLock{
//        FileLock.lockWork lock;
//        Look(FileLock.lockWork lock){
//            this.lock=lock;
//        }
//        /**
//         * 返回世界是否被锁
//         *
//         * @return true锁了 false没锁
//         */
//        @Override
//        public boolean isLocked() {
//            return lock.isLocked();
//        }
//
//        /**
//         * 给世界上锁，如果世界已经被锁了就不能再次上锁。
//         * @return true上锁成功 false上锁失败
//         */
//        @Override
//        public boolean locked(String serverName) {
//            try {
//                lock.Lock(new FileLock.lockNews(serverName));
//                return true;
//            }catch (Error e){
//                return false;
//            }
//
//        }
//
//        /**
//         * 给世界解锁，解锁服务器名称必须等于上锁服务器，否则不能解锁。
//         * @return true解锁成功 false没有解锁
//         */
//        @Override
//        public boolean unlock(String serverName) {
//            try {
//                lock.unlock();
//                return true;
//            }catch (Error e){
//                return false;
//            }
//        }
//
//        /**
//         * 获取上锁服务器的名称
//         *
//         * @return null 没有被锁
//         */
//        @Override
//        public String gitLockName() {
//            return lock.news().服务器名称;
//        }
//    }
//}
