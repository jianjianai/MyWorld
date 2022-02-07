package cn.jja8.myWorld.bukkit.basic.portalSupport;

import org.bukkit.World;
import org.bukkit.entity.Entity;

/**
 * 传送门的接口
 * */
public interface PortalTransmission {
    /**
     * 将玩家从当前位置传送到指定世界。但是不是单纯的传送。
     * 如果传送到的世界是末地，那么应该生成末地出生平台。
     * 如果传送到的是地狱，那么肯定是从主世界的传送门传送。那么就应该在对应位置生成地狱门。
     * 如果是传送到主世界，那么可能是从末地，也可能是从地狱。如果是从末地传送，应该传送到出生点。如果是从地狱传送，应该生成地狱传送门。
     * */
    void TpToWorld(Entity entity,World ToWorld);
}
