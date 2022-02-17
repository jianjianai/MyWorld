package cn.jja8.myWorld.bukkit;

import cn.jja8.myWorld.all.basic.teamSupport.TeamManager;
import cn.jja8.myWorld.bukkit.basic.PlayerData;
import cn.jja8.myWorld.bukkit.basic.Portal;
import cn.jja8.myWorld.bukkit.basic.Teams;
import cn.jja8.myWorld.bukkit.basic.WorldData;
import cn.jja8.myWorld.bukkit.basic.playerDataSupport.PlayerDataSupport;
import cn.jja8.myWorld.bukkit.basic.portalSupport.PortalTransmission;
import cn.jja8.myWorld.bukkit.basic.worldDataSupport.WorldDataSupport;

/**
 * 版本兼容的api，用于使插件兼容更多游戏版本。
 * 如果需要兼容1.16以外的服务器版本，请实现PlayerDataSupport，WorldDataSupport，PortalTransmission然后使用此api在load阶段注册。
 * */
public class VersionCompatibleAPI {

    /**
     * 注册基础玩家数据管理器
     * 用于管理基础的玩家数据加载和保存，负责将玩家的数据保存到流中，和将玩家的信息从流中加载。
     * 插件默认的PlayerDataSupport只支持1.16版本。
     * 您可在你插件的Load阶段调用此接口，来使插件兼容您的mc版本。
     * 插件其他逻辑基本支持bukkit服务端的所有版本。
     * */
    public static void regPlayerDataSupport(PlayerDataSupport playerDataSupport){
        PlayerData.playerDataSupport = playerDataSupport;
    }

    /**
     * 注册基础世界数据管理器
     * 用于管理基础的世界加载和保存，提供世界生成器和世界名称，从同步的数据库中加载世界，和保存世界。
     * 插件默认的WorldDataSupport只支持1.16版本
     * 您可在你插件的Load阶段调用此接口，来使插件兼容您的mc版本。
     * 插件其他逻辑基本支持bukkit服务端的所有版本。
     * */
    public static void regWorldDataSupport(WorldDataSupport worldDataSupport){
        WorldData.worldDataSupport = worldDataSupport;
    }

    /**
     * 注册传送门
     * 用于管理基础的世界加载和保存，提供世界生成器和世界名称，从同步的数据库中加载世界，和保存世界。
     * 插件默认的PortalTransmission只支持1.16版本
     * 您可在你插件的Load阶段调用此接口，来使插件兼容您的mc版本。
     * 插件其他逻辑基本支持bukkit服务端的所有版本。
     * */
    public static void regPortalTransmission(PortalTransmission portalTransmission){
        Portal.portalTransmission = portalTransmission;
    }

    /**
     * 注册团队管理
     * 用于管理团队
     * 插件默认的TeamManager基本支持所有版本，并且使用的sqlite
     * 您可在你插件的Load阶段调用此接口
     * */
    public static void regPortalTeam(TeamManager teamManager){
        Teams.teamManager = teamManager;
    }




}
