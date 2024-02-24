package com.foxapplication.mc.interconnection.fabric;

import com.foxapplication.embed.hutool.core.collection.CollectionUtil;
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
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import lombok.Getter;
import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.fabricmc.fabric.api.networking.v1.ServerLoginConnectionEvents;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.MessageArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.dedicated.DedicatedServer;

import static com.mojang.brigadier.arguments.StringArgumentType.*;

public class InterconnectionFabric implements DedicatedServerModInitializer {
    @Getter
    private static DedicatedServer server;
    private static Log log;
    @Override
    public void onInitializeServer() {
        log = LogFactory.get();
        Interconnection.Init();

        ServerLifecycleEvents.SERVER_STARTED.register((server) -> {
            if (server instanceof DedicatedServer dedicatedServer){
                InterconnectionFabric.server = dedicatedServer;
            }else {
                log.warn("请在专用服务器上使用Interconnection，Interconnection停止初始化。");
                return;
            }
            Interconnection.onServerStarted();
        });
        ServerLifecycleEvents.SERVER_STOPPING.register((server) -> {
            Interconnection.stop();
        });
        registerCommend();
    }

    public void registerCommend(){
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            dispatcher.register(Commands.literal("interconnection")
                    .requires(source->source.hasPermission(2))
                    .then(Commands.literal("list")
                            .executes(context -> {
                                context.getSource().sendSystemMessage(Component.literal( "当前已连接的节点："+ConnectManager.getConnectMap().keySet().toString()));
                                return 1;
                            })
                    ).then(Commands.literal("update")
                            .executes(context -> {
                                BaseMessage baseMessage = new BaseMessage("server_info_list", MessageManager.ALL, ObjectUtil.serialize(ListUtil.toList(ConnectManager.getServerMap().values())));
                                MessageManager.send(baseMessage);
                                context.getSource().sendSystemMessage(Component.literal("已发送更新数据"));
                                return 1;
                            })
                    ).then(Commands.literal("test")
                            .then(Commands.argument("value", MessageArgument.message())
                                    .executes(context -> {
                                        final Component value = MessageArgument.getMessage(context, "value");
                                        BaseMessage baseMessage = new BaseMessage("test_interconnection", MessageManager.ALL, value.getString());
                                        MessageUtil.send(baseMessage);
                                        context.getSource().sendSystemMessage(Component.literal(StrUtil.format("已发送测试数据数据：{}",value.getString())));
                                        return 1;
                                    })
                            )
                    )

            );

        });
    }
}
