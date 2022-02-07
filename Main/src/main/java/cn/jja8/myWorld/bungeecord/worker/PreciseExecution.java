package cn.jja8.myWorld.bungeecord.worker;

import cn.jja8.myWorld.bungeecord.MyWorldBungeecord;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.ServerConnectedEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.util.HashMap;
import java.util.Map;

/**
 * 负责在指定的时候执行指定的事情
 * */
public class PreciseExecution implements Listener {
    static class ServerInfoAndRunnable {
        private final ServerInfo serverInfo;
        private final Runnable runnable;
        public ServerInfoAndRunnable(ServerInfo serverInfo, Runnable runnable) {
            this.serverInfo = serverInfo;
            this.runnable = runnable;
        }
        public ServerInfo getServerInfo() {
            return serverInfo;
        }
        public Runnable getRunnable() {
            return runnable;
        }
    }

    Map<ProxiedPlayer, ServerInfoAndRunnable> serverInfoAndRunnableMap = new HashMap<>();
    public PreciseExecution(){
        MyWorldBungeecord.getMyWorldBungeecord().getProxy().getPluginManager().registerListener(MyWorldBungeecord.getMyWorldBungeecord(),this);
    }

    /**
     * 将玩家跳转到指定服务器，并且跳转之后执行runnable
     * 如果玩家跳转到了其他服务器，或者没有跳转就不会执行。
     * */
    public void jumpAndImplement(ProxiedPlayer proxiedPlayer,ServerInfo serverInfo, Runnable runnable){
        serverInfoAndRunnableMap.put(proxiedPlayer,new ServerInfoAndRunnable(serverInfo,runnable));
        if (proxiedPlayer.getServer().getInfo().equals(serverInfo)){
            runnable.run();
            return;
        }
        proxiedPlayer.connect(serverInfo);
    }

    @EventHandler
    public void 玩家成功跳转服务器(ServerConnectedEvent event){
        ServerInfoAndRunnable serverInfoAndRunnable =  serverInfoAndRunnableMap.remove(event.getPlayer());
        if (serverInfoAndRunnable==null){
            return;
        }
        if (!serverInfoAndRunnable.getServerInfo().equals(event.getServer().getInfo())){
            return;
        }
        MyWorldBungeecord.getMyWorldBungeecord().getProxy().getScheduler().runAsync(MyWorldBungeecord.getMyWorldBungeecord(),serverInfoAndRunnable.getRunnable());
    }

    @EventHandler
    public void 玩家离开bc(PlayerDisconnectEvent event){
        serverInfoAndRunnableMap.remove(event.getPlayer());
    }

}
