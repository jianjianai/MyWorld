package cn.jja8.myWorld.bukkit.command.user;

import cn.jja8.myWorld.all.basic.DatasheetSupport.Status;
import cn.jja8.myWorld.bukkit.MyWorldBukkit;
import cn.jja8.myWorld.bukkit.work.*;
import cn.jja8.patronSaint.bukkit.v3.command.CanSetUp;
import cn.jja8.patronSaint.bukkit.v3.command.CommandImplement;
import cn.jja8.patronSaint.bukkit.v3.command.NeedSet;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class DeleteWorld implements CommandImplement , CanSetUp {
    @NeedSet public String 删除成功 = "删除成功";
    @NeedSet public String 删除确认 = "请添加一条参数”yes“来确认你确认要删除世界。";
    @NeedSet public String 玩家没有团队 = "你没有团队";
    @NeedSet public String 不是团长 = "只有团长才可以删除。";
    @NeedSet public String 世界不存在 = "团队没有世界";

    @Override
    public boolean command(CommandSender commandSender, String[] strings) {

        if ((!(commandSender instanceof Player))) return true;
        Player player = (Player) commandSender;
        if (strings.length < 1) {
            player.sendMessage(删除确认);
            return true;
        }
        if (!"yes".equals(strings[0])) {
            player.sendMessage(删除确认);
            return true;
        }
        MyWorldPlayer myWorldPlayer = MyWorldManger.getPlayer(player);
        MyWorldTeam team = myWorldPlayer.getTeam();
        if (team == null) {
            player.sendMessage(玩家没有团队);
            return true;
        }
        if (myWorldPlayer.getStatus().getLevel()> Status.leader.getLevel()) {
            player.sendMessage(不是团长);
            return true;
        }
        MyWorldWorldGroup worldGroup = team.getWorldGroup();
        if (worldGroup == null) {
            player.sendMessage(世界不存在);
            return true;
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
        player.sendMessage(删除成功);
        return true;
    }

}
