package cn.jja8.myWorld.bukkit.config;


import it.unimi.dsi.fastutil.ints.Int2ByteMaps;

import java.util.ArrayList;

public class Lang {
    public   String 世界加载完成提示文本 = "完成";
    public   String 世界加载提示文本 = "服务器可能卡顿几秒钟，正在加载世界<世界>..<数>";
    public   String 查询信息_还没有团队 = "你现在还没有加入团队";
    public   String 返回世界_世界被其他服务器加载 = "世界已经被其他服务器加载，（这个信息如果bc服务器配置正常是不会出现的）";
    public   String 返回世界_团队没有世界 = "你的团队没有世界。";
    public   String 返回世界_没有团队 = "你还没有加入团队。";
    public   String 返回世界_传送成功 = "已回到上次的位置。";
    public   String 删除团队_没有删除世界 = "在删除团队之前，请先删除世界。";
    public   String 世界卸载_找不到主世界 = "您所在的世界已被卸载，找不到主世界<世界>，只能将您踢出游戏。如果您是服主，可以在配置文件中修改主世界名称。";
    public   String 世界交互_无权限 = "你没有<世界>的交互权限";
    public   String 创建世界_名称禁止使用 = "<世界>名称被禁止使用";
    public   String 创建世界_世界已经存在 = "<世界>世界已经存在服务器中了，请换一个名称。";
    public   String 删除世界_删除成功 = "删除成功";
    public   String 删除世界_删除确认 = "请添加一条参数”yes“来确认你确认要删除世界。";
    public   String 删除世界_玩家没有团队 = "你没有团队";
    public   String 删除世界_不是团长 = "只有团长才可以删除。";
    public   String 删除世界_世界未加载 = "世界没有加载时无法删除，请先进入你所在团队的世界。";
    public   String 玩家数据加载_完成 = "欢迎！";
    public   String 玩家数据加载_等待信息 = "正在加载你的数据，请稍等..<数>";
    public   String 去出生点_世界被其他服务器加载 = "世界已经被其他服务器加载，（这个信息如果bc服务器配置正常是不会出现的）";
    public   String 信任列表_信息 = "你所在的世界<世界>中，共有<数量>位信任玩家。分别是：<列表>";
    public   String 信任列表_你没有团队 = "你不在任何一个团队中。";
    public   String 信任列表_世界未加载 = "世界没有加载时无法查看信任玩家，请先进入你所在团队的世界。";
    public   String 添加信任_你没有团队 = "你不在任何一个团队中。";
    public   String 添加信任_权限不足 = "只有管理员才可以添加信任的玩家。";
    public   String 添加信任_世界未加载 = "世界没有加载时无法添加信任玩家，请先进入你所在团队的世界。";
    public   String 添加信任_添加成功 = "添加成功！";
    public   String 添加信任_没有参数 = "需要添加的玩家名称";
    public   String 取消信任_取消成功 = "取消成功！";
    public   String 取消信任_没有参数 = "需要取消的玩家名称";
    public   String 取消信任_权限不足 = "只有管理员才可以取消信任的玩家。";
    public   String 取消信任_你没有团队 = "你不在任何一个团队中。";
    public   String 取消信任_世界未加载 = "世界没有加载时无法取消信任玩家，请先进入你所在团队的世界。";
    public   String 创建世界_团队名不合法 = "世界创建失败，团队名不合法";
    public   String 接受邀请_接受成功 = "你已加入<团队>团队。";
    public   String 接受邀请_已经有团队 = "你已经在<团队>团队中了。";
    public   String 接受邀请_没被邀请 = "你没有被邀请哦。";
    public   String 邀请成员_被邀请信息 = "你被<玩家>邀请进<团队>团队，赶紧同意吧！";
    public   String 邀请成员_玩家不在线 = "玩家<不在线玩家>不在线。";
    public   String 邀请成员_没有参数 = "你要邀请谁呢？请添加一个玩家名参数吧。";
    public   String 邀请成员_邀请成功 = "邀请成功";
    public   String 邀请成员_不是管理 = "只有管理可以邀请成员";
    public   String 邀请成员_玩家没有团队 = "你没有团队";
    public   String 退出团队_退出成功 = "退出成功";
    public   String 退出团队_团长不能退出 = "团长不能退出团队";
    public   String 退出团队_退出确认 = "请添加一条参数”yes“来确认你确认要退出团队。";
    public   String 去出生点_传送成功 = "已将你传送到出生点。";
    public   String 创建世界_创建成功 = "世界创建成功";
    public   String 创建世界_世界名不合法 = "世界创建失败，世界名不合法。{长度必须大于3，只能使用小写字母}";
    public   String 创建团队_创建成功 = "团队创建成功";
    public   String 删除团队_删除成功 = "删除成功";
    public   String 退出团队_你没有团队 = "你不在任何团队中";
    public   String 删除团队_删除确认 = "请添加一条参数”yes“来确认你确认要删除团队。";
    public   String 删除团队_不是团长 = "只有团长才可以删除。";
    public   String 删除团队_玩家没有团队 = "你没有团队";
    public   String 去出生点_团队没有世界 = "你的团队没有世界。";
    public   String 去出生点_没有团队 = "你还没有加入团队。";
    public   String 创建世界_世界名称被他人占用 = "这个世界名称已经被占用，请换一个吧。";
    public   String 创建世界_需要世界名称 = "世界需要一个名称哦";
    public   String 创建世界_不是团长 = "只有团长才可以创建世界。";
    public   String 创建世界_团队已经有世界了 = "你所在的团队已经有世界了";
    public   String 创建世界_玩家没有团队 = "创建世界之前需要先加入一个团队";
    public   String 创建团队_团队名称被占用 = "<团队>名称已经被其他团队占用了。";
    public   String 创建团队_没有团队名参数 = "需要给团队取一个名字哦。";
    public   String 创建团队_已经在团队中了 = "你已经在<团队>团队中了。";
    public   String 查询信息_团队世界信息 = "该团队的世界是：<世界>";
    public   String 查询信息_团队没有世界 = "你加入的团队还没有世界";
    public   ArrayList<String> 查询信息_长信息列表 = new ArrayList<>();
    public String nuLoadWorld_世界没有加载 = "<世界>没有被加载，不用卸载哦。";
    public String nuLoadWorld_卸载完成="<世界>卸载完成。";
    public String nuLoadAllWorld_卸载 = "正在卸载<世界>";
    public String nuLoadAllWorld_卸载完成="所以世界已经卸载完成。";
    public String nuLoadAllWorld_未指定世界名="未指定世界名";
    public String loadWorld_未指定世界名="未指定世界名";
    public String loadWorld_世界不存在="<世界>世界不存在。";
    public String loadWorld_加载完成 ="加载完成";
    public String goTo_未指定世界名="未指定世界名";
    public String goTo_世界没有加载="<世界>世界没有被加载。";
    public String goTo_传送成功="传送成功";

    public Lang(){
        查询信息_长信息列表.add("你所在的团队是：<团队>");
        查询信息_长信息列表.add("<团队世界信息>");
        查询信息_长信息列表.add("团长是：<团长>");
        查询信息_长信息列表.add("管理员有<管理员列表>");
        查询信息_长信息列表.add("队员有<队员列表>");
    }
}
