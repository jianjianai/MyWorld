package cn.jja8.myWorld.bukkit.command.user;

import cn.jja8.myWorld.all.basic.DatasheetSupport.Status;
import cn.jja8.myWorld.bukkit.ConfigBukkit;
import cn.jja8.myWorld.bukkit.MyWorldBukkit;
import cn.jja8.myWorld.bukkit.work.*;
import cn.jja8.patronSaint_2022_3_2_1244.bukkit.command.CommandImplement;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class DeleteWorld implements CommandImplement {
    @Override
    public void command(CommandSender commandSender, String[] strings) {

        if ((!(commandSender instanceof Player))) return;
        Player player = (Player) commandSender;
        if (strings.length < 1) {
            player.sendMessage(ConfigBukkit.getLang().删除世界_删除确认);
            return;
        }
        if (!"yes".equals(strings[0])) {
            player.sendMessage(ConfigBukkit.getLang().删除世界_删除确认);
            return;
        }
        MyWorldPlayer myWorldPlayer = MyWorldManger.getPlayer(player);
        MyWorldTeam team = myWorldPlayer.getTeam();
        if (team == null) {
            player.sendMessage(ConfigBukkit.getLang().删除世界_玩家没有团队);
            return;
        }
        if (myWorldPlayer.getStatus().getLevel()> Status.leader.getLevel()) {
            player.sendMessage(ConfigBukkit.getLang().删除世界_不是团长);
            return;
        }
        MyWorldWorldGroup worldGroup = team.getWorldGroup();
        if (worldGroup == null) {
            player.sendMessage(ConfigBukkit.getLang().删除世界_世界不存在);
            return;
        }

        MyWorldWorldGrouping myWorldWorldGrouping = worldGroup.getLoading();
        if (myWorldWorldGrouping!=null){
            myWorldWorldGrouping.unLoad(false);
        }
        List<MyWorldWorld> worldList = worldGroup.delete();
        for (MyWorldWorld myWorldWorld : worldList) {
            MyWorldWorldLock lock = myWorldWorld.getMyWorldWorldLock();
            if (lock==null){
                MyWorldBukkit.getMyWorldBukkit().getLogger().warning("无法获得"+myWorldWorld.getName()+"世界的锁，无法删除！");
                continue;
            }
            lock.delete();
        }
        player.sendMessage(ConfigBukkit.getLang().删除世界_删除成功);
    }
}
