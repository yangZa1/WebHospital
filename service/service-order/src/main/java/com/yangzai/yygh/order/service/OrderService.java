package com.yangzai.yygh.order.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yangzai.yygh.entity.order.OrderInfo;
import com.yangzai.yygh.vo.order.OrderCountQueryVo;
import com.yangzai.yygh.vo.order.OrderQueryVo;

import java.util.Map;

public interface OrderService extends IService<OrderInfo> {
    Long saveOrder(String scheduleId, Long patientId);

    OrderInfo getOrder(String orderId);

    IPage<OrderInfo> selectPage(Page<OrderInfo> pageParam, OrderQueryVo orderQueryVo);

    Boolean  cancelOrder(Long orderId);

    void patientTips();

    //预约统计方法（Echarts整合）
    Map<String,Object> getCountMap(OrderCountQueryVo orderCountQueryVo);
}
