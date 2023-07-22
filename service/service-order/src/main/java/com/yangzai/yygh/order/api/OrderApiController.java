package com.yangzai.yygh.order.api;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yangzai.yygh.entity.order.OrderInfo;
import com.yangzai.yygh.order.service.OrderService;
import com.yangzai.yygh.result.OrderStatusEnum;
import com.yangzai.yygh.result.Result;
import com.yangzai.yygh.utils.AuthContextHolder;
import com.yangzai.yygh.vo.order.OrderCountQueryVo;
import com.yangzai.yygh.vo.order.OrderQueryVo;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@RestController
@RequestMapping("/api/order/orderInfo")
public class OrderApiController {

    @Autowired
    private OrderService orderService;

    @ApiOperation(value = "创建订单")
    @PostMapping("auth/submitOrder/{scheduleId}/{patientId}")
    public Result submitOrder(
            @PathVariable("scheduleId") String scheduleId,
            @PathVariable("patientId") Long patientId) {
        Long orderId = orderService.saveOrder(scheduleId, patientId);
        return Result.ok(orderId);
    }

    //根据订单id查询订单信息
    @GetMapping("auth/getOrders/{orderId}")
    public Result getOrders(@PathVariable("orderId") String orderId){
        OrderInfo orderInfo = orderService.getOrder(orderId);
        return Result.ok(orderInfo);
    }

    //订单列表（条件查询带分页）
    @GetMapping("auth/{page}/{limit}")
    public Result list(@PathVariable("page") Long page,
                       @PathVariable("limit") Long limit,
                       OrderQueryVo orderQueryVo,
                       HttpServletRequest request){

        //设置当前用户的id
        orderQueryVo.setUserId(AuthContextHolder.getUserId(request));
        Page<OrderInfo> pageParam = new Page<>(page, limit);
        IPage<OrderInfo> pageModel = orderService.selectPage(pageParam, orderQueryVo);
        return Result.ok(pageModel);

    }

    @ApiOperation(value = "获取订单状态")
    @GetMapping("getStatusList")
    public Result getStatusList() {
        return Result.ok(OrderStatusEnum.getStatusList());
    }


    @ApiOperation(value = "取消预约")
    @GetMapping("auth/cancelOrder/{orderId}")
    public Result cancel(@PathVariable("orderId") Long orderId) {
        return Result.ok(orderService.cancelOrder(orderId));
    }

    @ApiOperation(value = "获取订单统计数据")
    @PostMapping("inner/getCountMap")
    public Map<String, Object> getCountMap(@RequestBody OrderCountQueryVo orderCountQueryVo) {
        return orderService.getCountMap(orderCountQueryVo);
    }

}
