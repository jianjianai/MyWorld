package cn.jja8.myWorld.bukkit.basic.portalSupport;

import cn.jja8.myWorld.bukkit.MyWorldBukkit;
import cn.jja8.myWorld.bukkit.word.PlayerWorlds;
import cn.jja8.myWorld.bukkit.word.PlayerWorldTypeAtName;
import net.minecraft.server.v1_12_R1.*;
import org.bukkit.*;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_12_R1.CraftServer;
import org.bukkit.craftbukkit.v1_12_R1.CraftTravelAgent;
import org.bukkit.craftbukkit.v1_12_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.event.entity.EntityPortalEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.util.Vector;

import java.lang.reflect.Field;

public class v1_12_R1 implements PortalTransmission{
    /**
     * 将玩家从当前位置传送到指定世界。但是不是单纯的传送。
     * 如果传送到的世界是末地，那么应该生成末地出生平台。
     * 如果传送到的是地狱，那么肯定是从主世界的传送门传送。那么就应该在对应位置生成地狱门。
     * 如果是传送到主世界，那么可能是从末地，也可能是从地狱。如果是从末地传送，应该传送到出生点。如果是从地狱传送，应该生成地狱传送门。
     */
    public void TpToWorld(Entity entity, World ToWorld,int dimension,int to_dimension) {
//        if (!entity.getWorld().getEnvironment().equals(World.Environment.NETHER)&ToWorld.getEnvironment().equals(World.Environment.NORMAL)){//如果是从末地传送到主世界
//            entity.teleport(ToWorld.getSpawnLocation());
//        }
        try {

            if (entity instanceof CraftPlayer){
                changeWorld(((CraftPlayer)entity).getHandle(),dimension,((CraftWorld)ToWorld).getHandle(),to_dimension);
                //changeDimension(((CraftPlayer)entity).getHandle(),dimension,((CraftWorld)ToWorld).getHandle(),to_dimension,dimension==1?PlayerTeleportEvent.TeleportCause.END_PORTAL: PlayerTeleportEvent.TeleportCause.PLUGIN);
            }else {
                //changeWorld(((CraftEntity)entity).getHandle(),dimension,((CraftWorld)ToWorld).getHandle(),to_dimension);
                changeWorld(entity,dimension,((CraftWorld)ToWorld).getHandle(),to_dimension);
            }

           // Location location = calculateTarget(entity.getLocation(),dimension,((CraftWorld) ToWorld).getHandle(),to_dimension);
            //((CraftServer)Bukkit.getServer()).getServer().getPlayerList().calculateTarget(location,true);

        }catch (Throwable ignored){
            ignored.printStackTrace();
        }

    }

    public void changeWorld(EntityPlayer entity,int entity_dimension,WorldServer worldServer,int worldServer_worldServer){
        Field cv = null;
        try {
            cv = EntityPlayer.class.getDeclaredField("cv");
            cv.setAccessible(true);
        } catch (NoSuchFieldException noSuchFieldException) {
            noSuchFieldException.printStackTrace();
        }
        if (cv==null){
            return;
        }
        if (entity.isSleeping()) {
        } else {
            try {
                if (entity.dimension == 0 && worldServer_worldServer == -1) {
                    cv.set(entity,new Vec3D(entity.locX, entity.locY, entity.locZ));
                } else if (entity_dimension != -1 && worldServer_worldServer != 0) {
                    cv.set(entity,null);
                }
            }catch (IllegalArgumentException | IllegalAccessException illegalArgumentException){
                illegalArgumentException.printStackTrace();
            }


            if (entity.dimension == 1 && worldServer_worldServer == 1) {
                entity.worldChangeInvuln = true;
                entity.world.kill(entity);
                if (!entity.viewingCredits) {
                    entity.viewingCredits = true;
                    entity.playerConnection.sendPacket(new PacketPlayOutGameStateChange(4, 0.0F));
                }
            } else {
                PlayerTeleportEvent.TeleportCause cause = entity.dimension != 1 && worldServer_worldServer != 1 ? PlayerTeleportEvent.TeleportCause.NETHER_PORTAL : PlayerTeleportEvent.TeleportCause.END_PORTAL;
                changeDimension(entity,entity_dimension, worldServer,worldServer_worldServer, cause);
                entity.playerConnection.sendPacket(new PacketPlayOutWorldEvent(1032, BlockPosition.ZERO, 0, false));
                entity.lastSentExp = -1;
            }
        }
    }
    
    public void changeWorld(Entity entity,int entity_dimension,WorldServer worldServer,int worldServer_worldServer){
        ((CraftEntity)entity).getHandle().world.methodProfiler.a("changeDimension");
        Location exit = calculateTarget(entity.getLocation(), entity_dimension, worldServer, worldServer_worldServer);
        boolean useTravelAgent = worldServer != null && (((CraftEntity)entity).getHandle().dimension != 1 || worldServer.dimension != 1);
        TravelAgent agent = exit != null ? (TravelAgent)((CraftWorld)exit.getWorld()).getHandle().getTravelAgent() : CraftTravelAgent.DEFAULT;
        boolean oldCanCreate = agent.getCanCreatePortal();
        EntityPortalEvent event = new EntityPortalEvent(entity, entity.getLocation(), exit, agent);
        event.useTravelAgent(useTravelAgent);
        event.getEntity().getServer().getPluginManager().callEvent(event);
        if (!event.isCancelled() && event.getTo() != null && event.getTo().getWorld() != null && ((CraftEntity)entity).getHandle().isAlive()) {
            exit = event.useTravelAgent() ? event.getPortalTravelAgent().findOrCreate(event.getTo()) : event.getTo();
            agent.setCanCreatePortal(oldCanCreate);
            ((CraftEntity)entity).getHandle().teleportTo(exit, true);
            ((CraftEntity)entity).getHandle().world.methodProfiler.b();
        } else {
            agent.setCanCreatePortal(oldCanCreate);
        }
        ((CraftEntity)entity).getHandle().teleportTo(exit, true);
        ((CraftEntity)entity).getHandle().world.methodProfiler.b();
    }
    public void changeDimension(EntityPlayer entityplayer, int entityplayer_world_dimension, WorldServer exitWorld, int exitWorld_dimension, PlayerTeleportEvent.TeleportCause cause) {
        Location enter = entityplayer.getBukkitEntity().getLocation();
        Location exit = null;
        boolean useTravelAgent = false;
        if (exitWorld != null) {
            if (cause == PlayerTeleportEvent.TeleportCause.END_PORTAL && exitWorld_dimension == 0) {
                exit = entityplayer.getBukkitEntity().getBedSpawnLocation();
                if (exit != null && entityplayer_world_dimension == 0) {
                    exit = exit.add(0.5D, 0.10000000149011612D, 0.5D);
                } else {
                    BlockPosition randomSpawn = entityplayer.getSpawnPoint(((CraftServer)Bukkit.getServer()).getServer(), exitWorld);
                    exit = new Location(exitWorld.getWorld(), randomSpawn.getX(), randomSpawn.getY(), randomSpawn.getZ());
                }
            } else {
                exit = this.calculateTarget(enter,entityplayer_world_dimension, exitWorld,exitWorld_dimension);
                useTravelAgent = true;
            }
        }

        TravelAgent agent = exit != null ? (TravelAgent)((CraftWorld)exit.getWorld()).getHandle().getTravelAgent() : CraftTravelAgent.DEFAULT;
        PlayerPortalEvent event = new PlayerPortalEvent(entityplayer.getBukkitEntity(), enter, exit, agent, cause);
        event.useTravelAgent(useTravelAgent);
        Bukkit.getServer().getPluginManager().callEvent(event);
        if (!event.isCancelled() && event.getTo() != null) {
            exit = event.useTravelAgent() ? event.getPortalTravelAgent().findOrCreate(event.getTo()) : event.getTo();
            if (exit != null) {
                exitWorld = ((CraftWorld)exit.getWorld()).getHandle();
                PlayerTeleportEvent tpEvent = new PlayerTeleportEvent(entityplayer.getBukkitEntity(), enter, exit, cause);
                Bukkit.getServer().getPluginManager().callEvent(tpEvent);
                if (!tpEvent.isCancelled() && tpEvent.getTo() != null) {
                    Vector velocity = entityplayer.getBukkitEntity().getVelocity();
                    exitWorld.getTravelAgent().adjustExit(entityplayer, exit, velocity);
                    entityplayer.worldChangeInvuln = true;
                    ((CraftServer)Bukkit.getServer()).getServer().getPlayerList().moveToWorld(entityplayer, exitWorld.dimension, true, exit, true);
                    if (entityplayer.motX != velocity.getX() || entityplayer.motY != velocity.getY() || entityplayer.motZ != velocity.getZ()) {
                        entityplayer.getBukkitEntity().setVelocity(velocity);
                    }

                }
            }
        }
    }



//    public void changeWorld(net.minecraft.server.v1_12_R1.Entity entity,int enter_dimension, WorldServer worldserver1,int worldserver1_dimension) {
//        Location exit = this.calculateTarget(entity.getBukkitEntity().getLocation(),enter_dimension, worldserver1,worldserver1_dimension);
//        this.repositionEntity(entity, enter_dimension,exit, true);
//    }

    public Location calculateTarget(Location enter,int enter_dimension, net.minecraft.server.v1_12_R1.World target,int target_dimension) {
        WorldServer worldserver = ((CraftWorld)enter.getWorld()).getHandle();
        WorldServer worldserver1 = target.getWorld().getHandle();
        int i = enter_dimension;//worldserver.dimension;
        double y = enter.getY();
        float yaw = enter.getYaw();
        float pitch = enter.getPitch();
        double d0 = enter.getX();
        double d1 = enter.getZ();
        double d2 = 8.0D;
        worldserver.methodProfiler.a("moving");
        //if (worldserver1.dimension == -1) {
        if (target_dimension == -1) {
            d0 = MathHelper.a(d0 / d2, worldserver1.getWorldBorder().b() + 16.0D, worldserver1.getWorldBorder().d() - 16.0D);
            d1 = MathHelper.a(d1 / d2, worldserver1.getWorldBorder().c() + 16.0D, worldserver1.getWorldBorder().e() - 16.0D);
        //} else if (worldserver1.dimension == 0) {
        } else if (target_dimension == 0) {
            d0 = MathHelper.a(d0 * d2, worldserver1.getWorldBorder().b() + 16.0D, worldserver1.getWorldBorder().d() - 16.0D);
            d1 = MathHelper.a(d1 * d2, worldserver1.getWorldBorder().c() + 16.0D, worldserver1.getWorldBorder().e() - 16.0D);
        } else {
            BlockPosition blockposition;
            if (i == 1) {
                worldserver1 = ((CraftServer)Bukkit.getServer()).getServer().worlds.get(0);
                blockposition = worldserver1.getSpawn();
            } else {
                blockposition = worldserver1.getDimensionSpawn();
            }

            d0 = blockposition.getX();
            y = blockposition.getY();
            d1 = blockposition.getZ();
        }

        worldserver.methodProfiler.b();
        if (i != 1) {
            worldserver.methodProfiler.a("placing");
            d0 = MathHelper.clamp((int)d0, -29999872, 29999872);
            d1 = MathHelper.clamp((int)d1, -29999872, 29999872);
            worldserver.methodProfiler.b();
        }

        return new Location(worldserver1.getWorld(), d0, y, d1, yaw, pitch);
    }
    public void repositionEntity(net.minecraft.server.v1_12_R1.Entity entity,int entity_world_dimension, Location exit, boolean portal) {
        WorldServer worldserver = (WorldServer)entity.world;
        WorldServer worldserver1 = ((CraftWorld)exit.getWorld()).getHandle();
        int i = entity_world_dimension;//worldserver.dimension;
        worldserver.methodProfiler.a("moving");
        entity.setPositionRotation(exit.getX(), exit.getY(), exit.getZ(), exit.getYaw(), exit.getPitch());
        if (entity.isAlive()) {
            worldserver.entityJoinedWorld(entity, false);
        }

        worldserver.methodProfiler.b();
        if (i != 1) {
            worldserver.methodProfiler.a("placing");
            if (entity.isAlive()) {
                if (portal) {
                    Vector velocity = entity.getBukkitEntity().getVelocity();
                    worldserver1.getTravelAgent().adjustExit(entity, exit, velocity);
                    entity.setPositionRotation(exit.getX(), exit.getY(), exit.getZ(), exit.getYaw(), exit.getPitch());
                    if (entity.motX != velocity.getX() || entity.motY != velocity.getY() || entity.motZ != velocity.getZ()) {
                        entity.getBukkitEntity().setVelocity(velocity);
                    }
                }

                worldserver1.entityJoinedWorld(entity, false);
            }

            worldserver.methodProfiler.b();
        }

        entity.spawnIn(worldserver1);
    }

    /**
     * 将实体进行传送门传送
     * world_main，world_nether，world_the_end 三个世界为一组。
     *
     * @param entity        需要被传送的实体
     * @param block         传送门方块
     * @param world_main
     * @param world_nether
     * @param world_the_end
     */
    @Override
    public void send(Entity entity, Block block, World world_main, World world_nether, World world_the_end) {
        World world = block.getWorld();
        PlayerWorlds playerWorlds = MyWorldBukkit.getPlayerWordMangaer().getBeLoadPlayerWorlds(world);
        if (playerWorlds==null){
            return;
        }
        if (block.getType().equals(Material.ENDER_PORTAL)){//末地门
            if(world==playerWorlds.getWorld(PlayerWorldTypeAtName.end)){//在末地
                if(world_main!=null){
                    TpToWorld(entity, world_main,1,0);
                }
            }else {
                if(world_the_end!=null){
                    TpToWorld(entity,world_the_end,0,1);
                }
            }
        }else if (block.getType().equals(Material.PORTAL)){//地狱门
            if(world==playerWorlds.getWorld(PlayerWorldTypeAtName.infernal)){//在地狱
                if(world_main!=null){
                    TpToWorld(entity, world_main,-1,0);
                }
            }else {
                if(world_nether!=null){
                    TpToWorld(entity, world_nether,0,-1);
                }
            }
        }
    }
}
