package cn.jja8.myWorld.bukkit.command.user;

import cn.jja8.myWorld.all.basic.DatasheetSupport.Status;
import cn.jja8.myWorld.bukkit.work.*;
import cn.jja8.patronSaint.bukkit.v3.command.CanSetUp;
import cn.jja8.patronSaint.bukkit.v3.command.CommandImplement;
import cn.jja8.patronSaint.bukkit.v3.command.NeedSet;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TrustHim implements CommandImplement , CanSetUp {

    @NeedSet public String 世界不存在 ="你所在的团队没有世界";
    @NeedSet public String 你没有团队 = "你不在任何一个团队中。";
    @NeedSet public String 权限不足 = "只有管理员才可以添加信任的玩家。";
    @NeedSet public String 世界未加载 = "世界没有加载时无法添加信任玩家，请先进入你所在团队的世界。";
    @NeedSet public String 添加成功 = "添加成功！";
    @NeedSet public String 没有参数 = "需要添加的玩家名称";
    
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
        if (myWorldPlayer.getStatus().getLevel()>Status.admin.getLevel()) {
            player.sendMessage(权限不足);
            return true;
        }
        MyWorldWorldGroup worldGroup = team.getWorldGroup();
        if (worldGroup == null) {
            player.sendMessage(世界不存在);
        }
        MyWorldWorldGrouping beLoadWorlds = worldGroup.getLoading();
        if (beLoadWorlds == null) {
            player.sendMessage(世界未加载);
            return true;
        }
        beLoadWorlds.getMyWorldWordInform().getTrust().addBeTrust(strings[0]);
        player.sendMessage(添加成功);
        return true;
    }
}
