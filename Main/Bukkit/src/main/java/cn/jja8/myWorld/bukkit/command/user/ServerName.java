package cn.jja8.myWorld.bukkit.command.user;

import cn.jja8.myWorld.bukkit.ConfigBukkit;
import cn.jja8.patronSaint_2022_3_2_1244.bukkit.command.CommandImplement;
import org.bukkit.command.CommandSender;

public class ServerName implements CommandImplement {
    @Override
    public void command(CommandSender commandSender, String[] strings) {

        if (!ConfigBukkit.getWorldConfig().自动配置服务器名称) {
            return;
        }
        if (strings.length == 3) {
            if (strings[0].equals("ServerName") && strings[1].equals("ServerName")) {
                ConfigBukkit.getWorldConfig().服务器名称 = strings[2];
            }
        }
    }
}
