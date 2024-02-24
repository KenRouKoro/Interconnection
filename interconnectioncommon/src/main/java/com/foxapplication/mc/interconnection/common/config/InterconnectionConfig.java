package com.foxapplication.mc.interconnection.common.config;

import com.foxapplication.mc.core.config.interfaces.FieldAnnotation;
import lombok.Data;

/**
 * 互联配置类
 */
@Data
public class InterconnectionConfig {
    /**
     * 是否启用测试命令
     * 默认情况本Mod带一个用于测试连接的命令，关了就没了。
     */
    @FieldAnnotation(name = "是否启用测试命令", value = "默认情况本Mod带一个用于测试连接的命令，关了就没了。")
    boolean enabledTestCommand = true;

    /**
     * 服务端口
     * 本节点所使用的端口号，推荐改一下，不然容易打架。
     */
    @FieldAnnotation(name = "服务端口", value = "本节点所使用的端口号，推荐改一下，不然容易打架。")
    int port = 18080;
    /**
     * 外部端口
     * 其它节点连接本节点所访问的端口，用于在使用例如Nginx等代理的情况
     */
    @FieldAnnotation(name = "外部端口", value = "外部端口，其它节点连接本节点所访问的端口，用于在使用例如Nginx等代理的情况，默认请和服务端口保存一致")
    int externalPort = 18000;

    /**
     * 节点地址
     * 本节点的地址，除非只在一台服务器上使用，不然老实改罢。
     */
    @FieldAnnotation(name = "节点地址", value = "本节点的地址，除非只在一台服务器上使用，不然老实改罢。")
    String host = "127.0.0.1";

    /**
     * 节点ID
     * 本节点的ID，必须**唯一**。
     */
    @FieldAnnotation(name = "节点ID", value = "本节点的ID，必须**唯一**。")
    String id = "InterconnectionClient";
}

