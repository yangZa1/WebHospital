package com.yangzai.common.rabbit.constant;

public class MqConst {
    /**
     * 预约下单
     */
    //交换机名
    public static final String EXCHANGE_DIRECT_ORDER = "exchange.direct.order";
    //routingkey
    public static final String ROUTING_ORDER = "order";
    //队列名
    public static final String QUEUE_ORDER  = "queue.order";
    /**
     * 短信
     */
    public static final String EXCHANGE_DIRECT_MSM = "exchange.direct.msm";
    public static final String ROUTING_MSM_ITEM = "msm.item";
    //队列
    public static final String QUEUE_MSM_ITEM  = "queue.msm.item";


    /**
     * 定时任务 交换机 队列 routingkey的声明
     */
    public static final String EXCHANGE_DIRECT_TASK = "exchange.direct.task";
    public static final String ROUTING_TASK_8 = "task.8";
    //队列
    public static final String QUEUE_TASK_8 = "queue.task.8";

}
