package com.foxapplication.mc.interconnection.common;

import com.foxapplication.embed.hutool.core.thread.GlobalThreadPool;
import com.foxapplication.embed.hutool.core.thread.ThreadUtil;
import com.foxapplication.embed.hutool.cron.CronUtil;
import com.foxapplication.embed.hutool.cron.task.Task;
import com.foxapplication.embed.hutool.log.Log;
import com.foxapplication.embed.hutool.log.LogFactory;
import com.foxapplication.mc.core.FoxCore;
import com.foxapplication.mc.core.config.BeanFoxConfig;
import com.foxapplication.mc.core.config.LocalFoxConfig;
import com.foxapplication.mc.core.config.webconfig.WebConfig;
import com.foxapplication.mc.interaction.base.BaseClient;
import com.foxapplication.mc.interaction.base.data.BaseMessage;
import com.foxapplication.mc.interaction.base.data.Server;
import com.foxapplication.mc.interaction.base.service.ConnectManager;
import com.foxapplication.mc.interaction.base.service.MessageManager;
import com.foxapplication.mc.interconnection.common.config.InterconnectionConfig;
import com.foxapplication.mc.interconnection.common.util.MessageUtil;
import lombok.Getter;

import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Interconnection 互联系统
 */
public class Interconnection {

    /**
     * 日志记录器
     */
    private static Log log ;

    /**
     * Interconnection的配置信息
     */
    @Getter
    private static InterconnectionConfig config ;

    /**
     * BeanFox的配置信息
     */
    @Getter
    private static BeanFoxConfig beanFoxConfig ;

    /**
     * 本地Fox的配置信息
     */
    @Getter
    private static LocalFoxConfig localFoxConfig ;

    /**
     * 是否连接中心
     */
    @Getter
    private static final AtomicBoolean ConnectCenterStatus = new AtomicBoolean(false);

    /**
     * 服务器对象
     */
    @Getter
    private static Server mine;

    /**
     * 启动后的监听器列表
     */
    private final static CopyOnWriteArrayList<Runnable> afterStartListeners = new CopyOnWriteArrayList<>();

    /**
     * 初始化Interconnection
     */
    public static void Init(){
        log = LogFactory.get();

        log.info("开始初始化Interconnection。");

        localFoxConfig = new LocalFoxConfig(InterconnectionConfig.class);
        beanFoxConfig = localFoxConfig.getBeanFoxConfig();
        config = (InterconnectionConfig) beanFoxConfig.getBean();


        MessageUtil.addListener("test_interconnection", (message) -> {
            log.info("接收到来自{}的测试数据：{}",message.getForm(),message.getMessageByString());
        });
    }

    /**
     * 服务器启动后的操作
     */
    public static void onServerStarted(){
        log.info("正在启动Interaction...");
        mine = new Server(config.getId(), config.getHost(), false, config.getExternalPort(),new CopyOnWriteArrayList<>());
        BaseClient.init(mine, config.getPort());
        if (FoxCore.getConfig().isEnabledWebConfig()){
            WebConfig.addConfig(BaseClient.getBeanFoxConfig());
            WebConfig.addConfig(beanFoxConfig);
        }
        //定时任务检查与核心的连接
        CronUtil.schedule("*/2 * * * *", (Task) ()->{
            ConnectCenterStatus.set(ConnectManager.getConnectMap().get(BaseClient.getConfig().centerID)!=null);
            if (!ConnectCenterStatus.get()){
                if (BaseClient.getConfig().getCenterID().equals(mine.getId()))return;
                log.info("正在尝试链接中央节点");
                ConnectManager.connect(new Server(BaseClient.getConfig().centerID, BaseClient.getConfig().centerAddress, BaseClient.getConfig().centerUseSSL,BaseClient.getConfig().centerPort,new ArrayList<>()));
            }else{

            }
        });
        //定时向中心更新服务器列表
        CronUtil.schedule("*/1 * * * *", (Task) ()->{
            if (ConnectCenterStatus.get()){
                MessageManager.send(new BaseMessage("get_server_info_list", BaseClient.getConfig().centerID));
            }
        });

        CronUtil.start(true);

        afterStartListeners.forEach(Runnable::run);
        afterStartListeners.clear();
        log.info("已启动Interaction，Interconnection服务就绪");
    }

    /**
     * 添加启动后的监听器
     * @param runnable 启动后的监听器
     * @return 添加的启动后的监听器
     */
    public static Runnable addAfterStartListener(Runnable runnable){
        afterStartListeners.add(runnable);
        return runnable;
    }

    /**
     * 关闭Interconnection
     */
    public static void stop(){
        log.info("正在关闭Interconnection...");
        BaseClient.stop();
        CronUtil.stop();
    }

}

