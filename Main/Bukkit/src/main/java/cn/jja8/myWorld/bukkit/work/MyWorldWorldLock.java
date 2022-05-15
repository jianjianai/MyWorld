package cn.jja8.myWorld.bukkit.work;

import cn.jja8.myWorld.bukkit.ConfigBukkit;
import cn.jja8.myWorld.bukkit.MyWorldBukkit;
import cn.jja8.myWorld.bukkit.basic.worldDataSupport.WorldDataLock;
import cn.jja8.myWorld.bukkit.config.Lang;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;
import java.util.function.Consumer;

/**
 * 一个世界的锁
 * */
public class MyWorldWorldLock {
    public static interface OnLoad{
        /**
         * 加载完成时
         * */
        void onload(MyWorldWorlding myWorldWorlding);
        /**
         * 加载失败
         */
        void fail(Exception exception);
    }

    MyWorldWorld myWorldWorld;
    WorldDataLock worldDataLock;
    MyWorldWorldInform myWorldWorldInform;
    public MyWorldWorldLock(MyWorldWorld myWorldWorld, WorldDataLock worldDataLock) {
        this.myWorldWorld = myWorldWorld;
        this.worldDataLock = worldDataLock;
        myWorldWorldInform = new MyWorldWorldInform(this);
        worldDataLock.getWorldCreator().copy(myWorldWorldInform.getMyWorldWorldCreator().getWorldCreator(myWorldWorld.name));
        MyWorldManger.worldName_MyWorldWorldLock.put(myWorldWorld.name,this);
    }

    public void delete(){
        worldDataLock.delWorld();
        worldDataLock.unlock();
        MyWorldManger.worldName_MyWorldWorldLock.remove(myWorldWorld.name);
    }

    /**
     * 获得世界信息
     * */
    public MyWorldWorldInform getMyWorldWorldInform() {
        return myWorldWorldInform;
    }

    /**
     * 解锁
     * @param save 是否保存数据
     * */
    public void unlock(boolean save){
        MyWorldWorlding myWorldWorlding = MyWorldManger.worldName_MyWorldWorlding.get(myWorldWorld.name);
        if (myWorldWorlding!=null){
            myWorldWorlding.unLoad(save);
        }
        if (save){
            myWorldWorldInform.save();
        }
        worldDataLock.unlock();
        MyWorldManger.worldName_MyWorldWorldLock.remove(myWorldWorld.name);
    }

    /**
     * 加载世界
     * */
    public void loadWorld(OnLoad onLoad){
        Bukkit.getScheduler().runTaskAsynchronously(MyWorldBukkit.getMyWorldBukkit(), () -> {
            synchronized (MyWorldWorldLock.class){
                MyWorldWorlding myWorldWorlding = MyWorldManger.worldName_MyWorldWorlding.get(myWorldWorld.name);
                if (myWorldWorlding!=null){
                    onLoad.onload(myWorldWorlding);
                    return;
                }
                onLoad.onload(new MyWorldWorlding(MyWorldWorldLock.this));
            }
        });
    }


    /**
     * 加载进度接收
     * */
    public static class LoadingProgress implements cn.jja8.myWorld.bukkit.basic.worldDataSupport.LoadingProgress {
        UUID uuid = UUID.randomUUID();
        Lang lang = ConfigBukkit.getLang();
        String worldName;
        int v =0;
        long t = 0;
        public LoadingProgress(String worldName) {
            this.worldName = worldName;
            loadingProgress(-1);

        }
        @Override
        public void loadingProgress(int loading) {
            try {
                if (System.currentTimeMillis()-50<t){
                    return;
                }
                t = System.currentTimeMillis();
                Bukkit.getOnlinePlayers().forEach((Consumer<Player>) player ->
                        player.spigot().sendMessage(
                                ChatMessageType.ACTION_BAR,
                                uuid,
                                new TextComponent(lang.世界加载提示文本.replaceAll("<世界>",worldName).replaceAll("<数>",loading==-1|loading==0?v():loading+"%"))
                        )
                );
            }catch (Exception|Error throwable){
                throwable.printStackTrace();
            }
        }
        private String v(){
            String s = "/";
            switch (v++%4){
                case 0: s="/";break;
                case 1: s="-";break;
                case 2: s="\\\\";break;
                case 3: s="|";break;
            }
            return s;
        }
        public void finish() {
            Bukkit.getOnlinePlayers().forEach((Consumer<Player>) player ->
                    player.spigot().sendMessage(
                            ChatMessageType.ACTION_BAR,
                            uuid,
                            new TextComponent(lang.世界加载完成提示文本)
                    )
            );
        }
    }

}
