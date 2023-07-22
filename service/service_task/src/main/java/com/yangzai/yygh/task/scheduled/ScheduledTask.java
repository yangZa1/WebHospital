package com.yangzai.yygh.task.scheduled;

import com.yangzai.common.rabbit.constant.MqConst;
import com.yangzai.common.rabbit.service.RabbitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
//开启定时任务
@EnableScheduling
public class ScheduledTask {


    @Autowired
    private RabbitService rabbitService;


    //cron表达式，设置执行间隔 (网页有在线生成表达式的工具)
    //在每天八点执行方法 提醒就医
    //@Scheduled(cron = "0 0 8 * * ?")
    //每间隔30s执行一次
    @Scheduled(cron = "0/30 * * * * ?")
    public void taskPatient(){
        System.out.println(new Date());
        rabbitService.sendMessage(MqConst.EXCHANGE_DIRECT_TASK, MqConst.ROUTING_TASK_8, "");
    }
}
