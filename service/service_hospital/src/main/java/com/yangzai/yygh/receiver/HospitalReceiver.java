package com.yangzai.yygh.receiver;

import com.rabbitmq.client.Channel;
import com.yangzai.common.rabbit.constant.MqConst;
import com.yangzai.common.rabbit.service.RabbitService;
import com.yangzai.yygh.entity.Schedule;
import com.yangzai.yygh.entity.order.OrderInfo;
import com.yangzai.yygh.service.ScheduleService;
import com.yangzai.yygh.vo.order.OrderMqVo;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.Map;

@Component
public class HospitalReceiver {

    @Autowired
    private ScheduleService scheduleService;

    @Autowired
    private RabbitService rabbitService;

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = MqConst.QUEUE_ORDER, durable = "true"),
            exchange = @Exchange(value = MqConst.EXCHANGE_DIRECT_ORDER),
            key = {MqConst.ROUTING_ORDER}
    ))
    public void receiver(OrderMqVo orderMqVo, Message message, Channel channel) throws IOException {
        //下单成功更新预约数
        if(orderMqVo.getAvailableNumber() != null){
            Schedule schedule = scheduleService.getById(orderMqVo.getScheduleId());
            schedule.setReservedNumber(orderMqVo.getReservedNumber());
            schedule.setAvailableNumber(orderMqVo.getAvailableNumber());
            scheduleService.update(schedule);

            Map<String, Object> param = orderMqVo.getParam();
            String patientName = (String) param.get("name");
            String msm = patientName + "预约成功 ~";
            if(!StringUtils.isEmpty(msm)) {
                rabbitService.sendMessage(MqConst.EXCHANGE_DIRECT_MSM, MqConst.ROUTING_MSM_ITEM, msm);
            }
        }else{
//取消预约更新预约数
            Schedule schedule = scheduleService.getById(orderMqVo.getScheduleId());
            int availableNumber = schedule.getAvailableNumber().intValue() + 1;
            schedule.setAvailableNumber(availableNumber);
            scheduleService.update(schedule);

            Map<String, Object> param = orderMqVo.getParam();
            String patientName = (String) param.get("name");
            String msm = patientName + "已取消预约 ~";
            if(!StringUtils.isEmpty(msm)) {
                rabbitService.sendMessage(MqConst.EXCHANGE_DIRECT_MSM, MqConst.ROUTING_MSM_ITEM, msm);
            }
        }


    }
}

