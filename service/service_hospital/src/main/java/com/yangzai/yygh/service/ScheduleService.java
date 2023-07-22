package com.yangzai.yygh.service;

import com.yangzai.yygh.entity.Schedule;
import com.yangzai.yygh.vo.ScheduleQueryVo;
import com.yangzai.yygh.vo.order.ScheduleOrderVo;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Map;

public interface ScheduleService {
    void save(Map<String, Object> paramMap);

    Page<Schedule> findPageSchedule(int page, int limit, ScheduleQueryVo scheduleQueryVo);

    void remove(String hoscode, String hosScheduleId);

    Map<String, Object> getScheduleRule(long page, long limit, String hoscode, String depcode);

    List<Schedule> getDetailSchedule(String hoscode, String depcode, String workDate);

    Map<String, Object> getBookingScheduleRule(Integer page, Integer limit, String hoscode, String depcode);

    Schedule getById(String scheduleId);

    ScheduleOrderVo getScheduleOrderVo(String scheduleId);

    //更新排班的数据（结合RabbitMQ）
    void update(Schedule schedule);
}
