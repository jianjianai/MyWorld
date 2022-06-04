package cn.jja8.myWorld.bukkit.command.user;

import cn.jja8.myWorld.bukkit.ConfigBukkit;
import cn.jja8.patronSaint.bukkit.v3.command.CommandImplement;
import org.bukkit.command.CommandSender;

public class ServerName implements CommandImplement{

    @Override
    public boolean command(CommandSender commandSender, String[] strings) {

        if (!ConfigBukkit.getWorldConfig().自动配置服务器名称) {
            return true;
        }
        if (strings.length == 3) {
            if (strings[0].equals("ServerName") && strings[1].equals("ServerName")) {
                ConfigBukkit.getWorldConfig().服务器名称 = strings[2];
            }
        }
        return true;
    }
}
