package com.foxapplication.mc.interconnectionneo.forge;

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
import lombok.Getter;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.MessageArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.dedicated.DedicatedServer;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.server.ServerStartedEvent;
import net.neoforged.neoforge.event.server.ServerStoppingEvent;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(InterconnectionNeoForge.MODID)
public class InterconnectionNeoForge {
    // Define mod id in a common place for everything to reference
    public static final String MODID = "interconnectionneoforge";
    private static Log log;

    @Getter
    private static DedicatedServer server;

    public InterconnectionNeoForge(IEventBus modEventBus) {
        // Register the commonSetup method for modloading
        log = LogFactory.get();
        Interconnection.Init();

        // Register ourselves for server and other game events we are interested in
        NeoForge.EVENT_BUS.register(this);
    }


    @SubscribeEvent
    public void onServerStarted(ServerStartedEvent event) {
        if (event.getServer() instanceof DedicatedServer dedicatedServer){
            InterconnectionNeoForge.server = dedicatedServer;
        }else {
            log.warn("请在专用服务器上使用Interconnection，Interconnection停止初始化。");
            return;
        }
        Interconnection.onServerStarted();
    }
    @SubscribeEvent
    public void onServerStopping(ServerStoppingEvent event){
        Interconnection.stop();
    }
    @SubscribeEvent
    public void onRegisterCommand(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();
        dispatcher.register(Commands.literal("interconnection")
                .requires(source->source.hasPermission(2))
                .then(Commands.literal("list")
                        .executes(context -> {
                            context.getSource().sendSystemMessage(Component.literal( "当前已连接的节点："+ ConnectManager.getConnectMap().keySet().toString()));
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
    }

}
