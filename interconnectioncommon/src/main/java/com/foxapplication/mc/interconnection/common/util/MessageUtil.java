package com.foxapplication.mc.interconnection.common.util;

import com.foxapplication.embed.hutool.core.util.ObjectUtil;
import com.foxapplication.embed.hutool.log.Log;
import com.foxapplication.embed.hutool.log.LogFactory;
import com.foxapplication.mc.interaction.base.data.BaseMessage;
import com.foxapplication.mc.interaction.base.data.Server;
import com.foxapplication.mc.interaction.base.event.ServiceMessageBus;
import com.foxapplication.mc.interaction.base.service.ConnectManager;
import com.foxapplication.mc.interaction.base.service.MessageManager;

import java.util.ArrayList;
import java.util.List;

/**
 * 消息工具类
 * 你最好在初始化完后用
 */
public class MessageUtil {
    /**
     * 日志记录器
     */
    private static Log log = LogFactory.get();

    /**
     * 发送消息
     *
     * @param target  消息目标
     * @param service 服务名称
     * @param message 消息内容
     */
    public static void send(String target, String service, String message) {
        BaseMessage baseMessage = new BaseMessage(service, target, message);
        send(baseMessage);
    }

    /**
     * 发送消息
     *
     * @param target  消息目标
     * @param service 服务名称
     * @param message 消息内容
     */
    public static void send(String target, String service, byte[] message) {
        BaseMessage baseMessage = new BaseMessage(service, target, message);
        send(baseMessage);
    }

    /**
     * 发送服务消息
     *
     * @param target  消息目标
     * @param service 服务名称
     */
    public static void sendService(String target, String service) {
        send(target, service, "");
    }

    /**
     * 发送消息给所有目标
     *
     * @param service 服务名称
     * @param message 消息内容
     */
    public static void sendAll(String service, String message) {
        send(MessageManager.ALL, service, message);
    }

    /**
     * 发送消息给所有目标
     *
     * @param service 服务名称
     * @param message 消息内容
     */
    public static void sendAll(String service, byte[] message) {
        send(MessageManager.ALL, service, message);
    }

    /**
     * 发送服务消息给所有目标
     *
     * @param service 服务名称
     */
    public static void sendAllService(String service) {
        send(MessageManager.ALL, service, "");
    }

    /**
     * 发送对象消息
     *
     * @param target  消息目标
     * @param service 服务名称
     * @param message 消息内容
     */
    public static void sendObject(String target, String service, Object message) {
        BaseMessage baseMessage = new BaseMessage(service, target, ObjectUtil.serialize(message));
        send(baseMessage);
    }

    /**
     * 发送对象消息给所有目标
     *
     * @param service 服务名称
     * @param message 消息内容
     */
    public static void sendAllObject(String service, Object message) {
        sendObject(MessageManager.ALL, service, message);
    }

    /**
     * 发送循环消息
     *
     * @param service 服务名称
     * @param message 消息内容
     */
    public static void sendLoop(String service, String message) {
        send(MessageManager.LOOP, service, message);
    }

    /**
     * 发送循环消息
     *
     * @param service 服务名称
     * @param message 消息内容
     */
    public static void sendLoop(String service, byte[] message) {
        send(MessageManager.LOOP, service, message);
    }

    /**
     * 发送循环服务消息
     *
     * @param service 服务名称
     */
    public static void sendLoopService(String service) {
        sendLoop(service, "");
    }

    /**
     * 发送循环对象消息
     *
     * @param service 服务名称
     * @param message 消息内容
     */
    public static void sendLoopObject(String service, Object message) {
        sendObject(MessageManager.LOOP, service, message);
    }

    /**
     * 发送消息
     *
     * @param message 消息对象
     */
    public static void send(BaseMessage message) {
        MessageManager.send(message);
    }

    /**
     * 添加消息监听器
     *
     * @param service  服务名称
     * @param callback 消息回调函数
     */
    public static void addListener(String service, ServiceMessageBus.ServiceMessageCallback callback) {
        ServiceMessageBus.subscribe(service, callback);
    }

    /**
     * 移除消息监听器
     *
     * @param service  服务名称
     * @param callback 消息回调函数
     */
    public static void removeListener(String service, ServiceMessageBus.ServiceMessageCallback callback) {
        ServiceMessageBus.unsubscribe(service, callback);
    }

    /**
     * 移除消息监听器
     *
     * @param service 服务名称
     */
    public static void removeListener(String service) {
        ServiceMessageBus.unsubscribe(service);
    }

    /**
     * 获取指定目标的服务列表
     * @param target 目标
     * @return 服务列表
     */
    public static List<String> getServices(String target) {
        return new ArrayList<>(ConnectManager.getServerMap().get(target).getService());
    }

    /**
     * 获取提供指定服务的服务器列表
     * @param service 服务
     * @return 服务器列表
     */
    public static List<Server> getServiceServers(String service) {
        return ConnectManager.getServiceServers(service);
    }

    /**
     * 向指定服务器发送消息
     * @param servers 服务器列表
     * @param message 消息
     */
    public static void send(List<Server> servers, BaseMessage message){
        servers.forEach(server -> {
            MessageManager.sendMessageDirectly(new BaseMessage(message.getService(),server.getId(),message.getMessage()));
        });
    }

    /**
     * 向指定服务的所有服务器发送消息
     * @param service 服务
     * @param message 消息
     */
    public static void sendToAllService(String service, BaseMessage message) {
        send(getServiceServers(service), message);
    }

    /**
     * 向指定服务器发送消息
     * @param message 消息
     * @param servers 服务器列表
     */
    public static void send(BaseMessage message,Server ...servers){
        for (Server server : servers) {
            MessageManager.sendMessageDirectly(new BaseMessage(message.getService(),server.getId(),message.getMessage()));
        }
    }


}
