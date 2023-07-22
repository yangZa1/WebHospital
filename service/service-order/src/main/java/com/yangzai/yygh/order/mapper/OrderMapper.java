package com.yangzai.yygh.order.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yangzai.yygh.entity.order.OrderInfo;
import com.yangzai.yygh.vo.order.OrderCountQueryVo;
import com.yangzai.yygh.vo.order.OrderCountVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;


public interface OrderMapper extends BaseMapper<OrderInfo> {

    //查询预约统计数据的方法
    List<OrderCountVo> selectOrderCount(@Param("vo") OrderCountQueryVo orderCountQueryVo);
}
