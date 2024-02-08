package com.foxapplication.mc.interconnection.paper;

import com.foxapplication.embed.hutool.core.collection.ListUtil;
import com.foxapplication.embed.hutool.core.util.ObjectUtil;
import com.foxapplication.embed.hutool.core.util.StrUtil;
import com.foxapplication.embed.hutool.log.Log;
import com.foxapplication.embed.hutool.log.LogFactory;
import com.foxapplication.mc.interaction.base.data.BaseMessage;
import com.foxapplication.mc.interaction.base.service.ConnectManager;
import com.foxapplication.mc.interaction.base.service.MessageManager;
import com.foxapplication.mc.interconnection.common.Interconnection;
import com.foxapplication.mc.interconnection.common.util.MessageUtil;
import com.mojang.brigadier.CommandDispatcher;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.craftbukkit.v1_20_R3.CraftServer;
import org.bukkit.craftbukkit.v1_20_R3.command.CraftCommandMap;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerLoadEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public final class InterconnectionPaper extends JavaPlugin implements Listener {
    private static Log log;

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);
        log = LogFactory.get();
        Interconnection.Init();
        registerCommand();
    }

    @Override
    public void onDisable() {
        Interconnection.stop();
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onServerStart(ServerLoadEvent event) {
        if (event.getType() == ServerLoadEvent.LoadType.RELOAD) return;
        Interconnection.onServerStarted();
    }

    private void registerCommand(){
        if (getServer() instanceof CraftServer craftServer){
            SimpleCommandMap commandMap = craftServer.getCommandMap();
            Command command = new Command("interconnect") {
                @Override
                public boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String[] args) {
                    if (args.length > 0){
                        if (args[0].equalsIgnoreCase("list")){
                            sender.sendMessage(Component.text("当前已连接的节点："+ ConnectManager.getConnectMap().keySet().toString(), NamedTextColor.BLUE));
                            return true;
                        }else if (args[0].equalsIgnoreCase("update")){
                            BaseMessage baseMessage = new BaseMessage("server_info_list", MessageManager.ALL, ObjectUtil.serialize(ListUtil.toList(ConnectManager.getServerMap().values())));
                            MessageManager.send(baseMessage);
                            sender.sendMessage(Component.text("已发送更新数据", NamedTextColor.BLUE));
                            return true;
                        }else if(args[0].equalsIgnoreCase("test")){
                            String value = args.length > 1 ? args[1] : "";
                            BaseMessage baseMessage = new BaseMessage("test_interconnection", MessageManager.ALL, value);
                            MessageUtil.send(baseMessage);
                            sender.sendMessage(StrUtil.format("已发送测试数据数据：{}"));
                            return true;
                        }
                    }
                    sender.sendMessage(Component.text("使用方法: /interconnection <list|update|test> [TestValue]", NamedTextColor.RED));
                    return false;
                }
            };

            commandMap.register("interconnect", command);

        }else {
            log.error("服务器实例获取失败。");
        }
    }
}