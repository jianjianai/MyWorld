package cn.jja8.myWorld.bungeecord.veryUtil;

import cn.jja8.myWorld.all.veryUtil.PingServer;
import cn.jja8.myWorld.bungeecord.MyWorldBungeecord;
import cn.jja8.myWorld.bungeecord.basic.WorldData;
import cn.jja8.myWorld.bungeecord.basic.worldDataSupport.WorldDataLock;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import net.md_5.bungee.api.config.ServerInfo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 主要用于查找服务器
 * */
public class ServerFind {
    /**
     * 查找一个在线玩家少的可加载世界的服务器
     * @return null 所有服务器都达到了上限
     * */
    public static ServerInfo getLoadWorldServer(){
        Map<String, ServerInfo> serverInfoMap = MyWorldBungeecord.getMyWorldBungeecord().getProxy().getServers();
        for (String s:MyWorldBungeecord.getServerConfig().非MyWorld服务器列表){
            serverInfoMap.remove(s);
        }
        List<ServerInfo> serverInfoList = new ArrayList<>(serverInfoMap.values());
        while (true){
            ServerInfo minPlayerServer = null;
            for(ServerInfo serverInfo:serverInfoList){
                if (minPlayerServer==null){
                    minPlayerServer = serverInfo;
                }
                if (minPlayerServer.getPlayers().size()>serverInfo.getPlayers().size()){
                    minPlayerServer = serverInfo;
                }
            }
            if (minPlayerServer==null){
                MyWorldBungeecord.getMyWorldBungeecord().getLogger().warning("群集中没有任何可用的myWorld服务器了，请添加更多的myWorld服务器。（可能是所有服务器的可加入玩家数量都低于子服务器预留可加入玩家数）");
                return null;
            }
            try {
                String s = PingServer.connect(minPlayerServer.getSocketAddress());
                JSONObject jsonObject = JSON.parseObject(s).getJSONObject("players");
                if (jsonObject.isEmpty()){
                    MyWorldBungeecord.getMyWorldBungeecord().getLogger().warning("ping服务器"+minPlayerServer.getName()+"没有正常返回在线玩家数：\n"+s);
                    serverInfoList.remove(minPlayerServer);
                    continue;
                }
                int online = jsonObject.getIntValue("online");
                int max = jsonObject.getIntValue("max");
                if (online+MyWorldBungeecord.getServerConfig().子服务器预留可加入玩家数>max){
                    serverInfoList.remove(minPlayerServer);
                    continue;
                }
                return minPlayerServer;
            } catch (IOException exception) {
                serverInfoList.remove(minPlayerServer);
            }
        }
    }

    /**
     * 获得正在使用某个世界的服务器
     * @return null 这个世界没有被服务器使用
     * */
    public static ServerInfo getWorldBeLoadServer(String worldName){
        WorldDataLock worldDataLock = WorldData.worldDataSupport.getWorldDataLock(worldName);
        if (!worldDataLock.isLocked()){
            return null;
        }
        String name = worldDataLock.gitLockName();
        ServerInfo serverInfo = MyWorldBungeecord.getMyWorldBungeecord().getProxy().getServerInfo(name);
        if (serverInfo==null){
            throw new Error(worldName+"世界被"+name+"服务器上锁了，但是在群组中未找到"+name+"服务器。（请检查子服务器的配置文件中名称是否和bc中相同。）");
        }
        return serverInfo;
    }
}
