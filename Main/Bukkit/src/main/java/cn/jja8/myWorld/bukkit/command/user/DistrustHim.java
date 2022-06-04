package cn.jja8.myWorld.bukkit.command.user;

import cn.jja8.myWorld.all.basic.DatasheetSupport.Status;
import cn.jja8.myWorld.bukkit.work.*;
import cn.jja8.patronSaint.bukkit.v3.command.CanSetUp;
import cn.jja8.patronSaint.bukkit.v3.command.CommandImplement;
import cn.jja8.patronSaint.bukkit.v3.command.NeedSet;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class DistrustHim implements CommandImplement , CanSetUp {

    @NeedSet public String 取消成功 = "取消成功！";
    @NeedSet public String 没有参数 = "需要取消的玩家名称";
    @NeedSet public String 权限不足 = "只有管理员才可以取消信任的玩家。";
    @NeedSet public String 你没有团队 = "你不在任何一个团队中。";
    @NeedSet public String 世界未加载 = "世界没有加载时无法取消信任玩家，请先进入你所在团队的世界。";
    @NeedSet public String 世界不存在 = "你所在的团队没有世界";


    @Override
    public boolean command(CommandSender commandSender, String[] strings) {
        if ((!(commandSender instanceof Player))) return true;
        Player player = (Player) commandSender;
        if (strings.length < 1) {
            player.sendMessage(没有参数);
            return true;
        }
        MyWorldPlayer myWorldPlayer = MyWorldManger.getPlayer(player);
        MyWorldTeam team = myWorldPlayer.getTeam();
        if (team == null) {
            player.sendMessage(你没有团队);
            return true;
        }
        if (myWorldPlayer.getStatus().getLevel()> Status.admin.getLevel()) {
            player.sendMessage(权限不足);
            return true;
        }
        MyWorldWorldGroup worldGroup = team.getWorldGroup();
        if (worldGroup == null) {
            player.sendMessage(世界不存在);
            return true;
        }
        MyWorldWorldGrouping myWorldWorldGrouping = worldGroup.getLoading();
        if (myWorldWorldGrouping == null) {
            player.sendMessage(世界未加载);
            return true;
        }
        myWorldWorldGrouping.getMyWorldWordInform().getTrust().delBeTrust(strings[0]);
        player.sendMessage(取消成功);
        return true;
    }

    @Override
    public List<String> tabCompletion(CommandSender commandSender, String[] strings) {
        if ((!(commandSender instanceof Player))) return null;
        Player player = (Player) commandSender;
        MyWorldPlayer myWorldPlayer = MyWorldManger.getPlayer(player);
        MyWorldTeam team = myWorldPlayer.getTeam();
        if (team == null) {
            return null;
        }
        MyWorldWorldGroup worldGroup = team.getWorldGroup();
        if (worldGroup == null) {
            return null;
        }
        MyWorldWorldGrouping beLoadWorlds = worldGroup.getLoading();
        if (beLoadWorlds == null) {
            return null;
        }
        return beLoadWorlds.getMyWorldWordInform().getTrust().beTrustList();
    }
}
