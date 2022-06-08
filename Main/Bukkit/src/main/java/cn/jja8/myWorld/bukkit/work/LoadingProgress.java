package cn.jja8.myWorld.bukkit.work;

import cn.jja8.myWorld.bukkit.ConfigBukkit;
import cn.jja8.myWorld.bukkit.config.Lang;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.function.Consumer;

/**
 * 加载进度接收
 */
public class LoadingProgress implements cn.jja8.myWorld.bukkit.basic.worldDataSupport.LoadingProgress {
    Lang lang = ConfigBukkit.getLang();
    String worldName;
    int v = 0;
    long t = 0;

    public LoadingProgress(String worldName) {
        this.worldName = worldName;
        loadingProgress(-1);

    }

    @Override
    public void loadingProgress(int loading) {
        try {
            if (System.currentTimeMillis() - 50 < t) {
                return;
            }
            t = System.currentTimeMillis();
            Bukkit.getOnlinePlayers().forEach((Consumer<Player>) player ->
                    player.spigot().sendMessage(
                            ChatMessageType.ACTION_BAR,
                            new TextComponent(lang.世界加载提示文本.replaceAll("<世界>", worldName).replaceAll("<数>", loading == -1 | loading == 0 ? v() : loading + "%"))
                    )
            );
        } catch (Exception | Error throwable) {
            throwable.printStackTrace();
        }
    }

    private String v() {
        String s = "/";
        switch (v++ % 4) {
            case 0:
                s = "/";
                break;
            case 1:
                s = "-";
                break;
            case 2:
                s = "\\\\";
                break;
            case 3:
                s = "|";
                break;
        }
        return s;
    }

    public void finish() {
        Bukkit.getOnlinePlayers().forEach((Consumer<Player>) player ->
                player.spigot().sendMessage(
                        ChatMessageType.ACTION_BAR,
                        new TextComponent(lang.世界加载完成提示文本)
                )
        );
    }
}
