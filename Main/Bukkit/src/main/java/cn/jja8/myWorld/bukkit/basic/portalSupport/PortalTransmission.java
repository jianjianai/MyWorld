package cn.jja8.myWorld.bukkit.basic.portalSupport;

import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;

/**
 * 传送门的接口
 * */
public interface PortalTransmission {
    /**
     * 将实体进行传送门传送
     * world_main，world_nether，world_the_end 三个世界为一组。
     * @param entity 需要被传送的实体
     * @param block 传送门方块
     */
    void send(Entity entity, Block block, World world_main, World world_nether, World world_the_end);
}
