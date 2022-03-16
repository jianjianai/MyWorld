package cn.jja8.myWorld.bukkit.basic.worldDataSupport;

import net.minecraft.server.v1_12_R1.*;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.craftbukkit.v1_12_R1.CraftServer;
import org.bukkit.event.world.WorldInitEvent;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.generator.ChunkGenerator;

import java.io.*;
import java.lang.reflect.Field;
import java.util.Locale;
import java.util.Map;

public class v1_12_R1 implements WorldDataSupport{
    File allWordFile;
    MinecraftServer console;
    CraftServer craftServer;
    Map<String, World> worlds;
    public v1_12_R1(File allWordFile){
        this.allWordFile = allWordFile;
        allWordFile.mkdirs();
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
    public boolean unloadWorld(World world, boolean save){
        CraftServer craftServer = (CraftServer) Bukkit.getServer();
        return craftServer.unloadWorld(world,save);
    }

    /**
     * 在指定路径加载世界，必须在主线程调用
     *
     * */
    public World loadWorld(WorldCreator creator, String worldName,LoadingProgress loadingProgress) {
        Validate.notNull(creator, "Creator may not be null");
        String name = creator.name();
        ChunkGenerator generator = creator.generator();
        File folder = new File(new File(allWordFile,worldName), name);
        World world = craftServer.getWorld(name);
        WorldType type = WorldType.getType(creator.type().getName());
        boolean generateStructures = creator.generateStructures();
        if (world != null) {
            return world;
        } else if (folder.exists() && !folder.isDirectory()) {
            throw new IllegalArgumentException("File exists with the name '" + name + "' and isn't a folder");
        } else {
            if (generator == null) {
                generator = craftServer.getGenerator(name);
            }

//            Convertable converter = new WorldLoaderServer(new File(allWordFile,worldName), craftServer.getHandle().getServer().dataConverterManager);
//            if (converter.isConvertable(name)) {
//                craftServer.getLogger().info("Converting world '" + name + "'");
//                converter.convert(name, new IProgressUpdate() {
//                    private long b = System.currentTimeMillis();
//
//                    public void a(String s) {
//                    }
//
//                    public void a(int i) {
//                        if (System.currentTimeMillis() - this.b >= 1000L) {
//                            this.b = System.currentTimeMillis();
//                            MinecraftServer.LOGGER.info("Converting... " + i + "%");
//                        }
//
//                    }
//
//                    public void c(String s) {
//                    }
//                });
//            }

            int dimension = 10 + this.console.worlds.size();
            boolean used;
            do {
                used = false;
                for (WorldServer server : this.console.worlds) {
                    if(server.dimension == dimension){
                        used = true;
                        ++dimension;
                        break;
                    }
                }
            } while(used);

            boolean hardcore = false;
            IDataManager sdm = new ServerNBTManager(new File(allWordFile,worldName), name, true, craftServer.getHandle().getServer().dataConverterManager);
            WorldData worlddata = sdm.getWorldData();
            WorldSettings worldSettings = null;
            if (worlddata == null) {
                worldSettings = new WorldSettings(creator.seed(), EnumGamemode.getById(craftServer.getDefaultGameMode().getValue()), generateStructures, hardcore, type);
                worldSettings.setGeneratorSettings(creator.generatorSettings());
                worlddata = new WorldData(worldSettings, name);
            }

            worlddata.checkName(name);
            WorldServer internal = (WorldServer)(new WorldServer(this.console, sdm, worlddata, dimension, this.console.methodProfiler, creator.environment(), generator)).b();
            if (!this.worlds.containsKey(name.toLowerCase(Locale.ENGLISH))) {
                return null;
            } else {
                if (worldSettings != null) {
                    internal.a(worldSettings);
                }

                internal.scoreboard = craftServer.getScoreboardManager().getMainScoreboard().getHandle();
                internal.tracker = new EntityTracker(internal);
                internal.addIWorldAccess(new WorldManager(this.console, internal));
                internal.worldData.setDifficulty(EnumDifficulty.EASY);
                internal.setSpawnFlags(true, true);
                this.console.worlds.add(internal);
                craftServer.getPluginManager().callEvent(new WorldInitEvent(internal.getWorld()));
                System.out.println("Preparing start region for level " + (this.console.worlds.size() - 1) + " (Seed: " + internal.getSeed() + ")");
                if (internal.getWorld().getKeepSpawnInMemory()) {
                    short short1 = 196;
                    long i = System.currentTimeMillis();
                    int t = 0;

                    for(int j = -short1; j <= short1; j += 16) {
                        for(int k = -short1; k <= short1; k += 16) {
                            long l = System.currentTimeMillis();
                            if (l < i) {
                                i = l;
                            }

                            if (l > i + 50L) {
                                int i1 = (short1 * 2 + 1) * (short1 * 2 + 1);
                                int j1 = (j + short1) * (short1 * 2 + 1) + k + 1;
                                loadingProgress.loadingProgress(j1 * 100 / i1);
                                if (t++%20==0){
                                    System.out.println("Preparing spawn area for " + name + ", " + j1 * 100 / i1 + "%");
                                }
                                i = l;
                            }

                            BlockPosition chunkcoordinates = internal.getSpawn();
                            internal.getChunkProviderServer().getChunkAt(chunkcoordinates.getX() + j >> 4, chunkcoordinates.getZ() + k >> 4);
                        }
                    }
                }

                craftServer.getPluginManager().callEvent(new WorldLoadEvent(internal.getWorld()));
                return internal.getWorld();
            }
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
