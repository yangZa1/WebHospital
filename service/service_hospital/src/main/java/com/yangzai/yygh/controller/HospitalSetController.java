package com.yangzai.yygh.controller;


import cn.hutool.crypto.digest.DigestUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yangzai.yygh.entity.HospitalSet;
import com.yangzai.yygh.result.Result;
import com.yangzai.yygh.service.IHospitalSetService;
import com.yangzai.yygh.vo.HospitalSetQueryVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;


import java.util.List;
import java.util.Random;

/**
 * <p>
 * 医院设置表 前端控制器
 * </p>
 *
 * @author yangzai
 * @since 2022-11-18
 */
//@RestController = @Contorller + @ResponseBody
@RestController
@Api(tags = "医院设置注解")
@RequestMapping("/admin/hosp/yygh/hospitalSet")
//允许跨域访问
//@CrossOrigin
public class HospitalSetController {

    @Autowired
    private IHospitalSetService hospitalSetService;

    //1 查询医院设置表中的所有信息
    @ApiOperation(value = "获取医院设置表中的所有信息")
    @GetMapping("findAll")
    public Result findAllHospitalSet() {
        //调用Service中的方法
        List<HospitalSet> hospitalSets = hospitalSetService.list();
        return Result.ok(hospitalSets);
    }

    //2 通过id删除医院设置表中的信息
    @ApiOperation(value = "删除医院设置表中的信息")
    @DeleteMapping("{id}")
    public Result deleteHosptalSetById(@PathVariable("id") Long id) {
        if (hospitalSetService.DeleteHospSetById(id) == 1) {
            return Result.ok();
        } else {
            return Result.fail();
        }
    }

    //3 条件查询带分页
    @PostMapping("findOageHospitalSet/{current}/{limit}")
    public Result findPageHospitalSet(@PathVariable("current") long current,
                                      @PathVariable("limit") long limit,
                                      @RequestBody(required = false) HospitalSetQueryVo hospitalSetQueryVo) {
        //创建page对象，传递当前页面、每页记录数
        Page<HospitalSet> page = new Page<>(current, limit);
        //封装查询条件
        QueryWrapper<HospitalSet> queryWrapper = new QueryWrapper<>();

        String hosname = hospitalSetQueryVo.getHosname();
        String hoscode = hospitalSetQueryVo.getHoscode();

        if (!StringUtils.isEmpty(hosname)) {
            queryWrapper.like("hosname", hospitalSetQueryVo.getHosname());
        }
        if (!StringUtils.isEmpty(hoscode)) {
            queryWrapper.like("hoscode", hospitalSetQueryVo.getHosname());
        }

        Page<HospitalSet> setPage = hospitalSetService.page(page, queryWrapper);

        return Result.ok(setPage);
    }

    //4 添加医院设置
    @PostMapping("saveHospitalSet")
    public Result saveHospitalSet(@RequestBody HospitalSet hospitalSet) {
        //设置状态 : 1 启用 0 禁用
        hospitalSet.setStatus(1);
        Random random = new Random();
        String strSrc = System.currentTimeMillis() + "" + random.nextInt(1000);
        //md5加密获取签名密钥
        String SignKey = DigestUtil.md5Hex(strSrc);

        hospitalSet.setSignKey(SignKey);

        boolean save = hospitalSetService.save(hospitalSet);

        if (save) {
            return Result.ok();
        } else {
            return Result.fail();
        }
    }

    //5 根据id获取医院设置
    @GetMapping("getHospitalSet/{id}")
    public Result getHospitalSetById(@PathVariable Long id) {
        HospitalSet hospitalSet = hospitalSetService.getById(id);
        if (hospitalSet != null) return Result.ok(hospitalSet);
        else return Result.fail();
    }

    //6 修改医院设置
    @PutMapping("updateHospitalSet")
    public Result updateHospitalSet(@RequestBody HospitalSet hospitalSet) {
        boolean update = hospitalSetService.updateById(hospitalSet);

        if (update) {
            return Result.ok();
        } else {
            return Result.fail();
        }
    }

    //7 批量删除医院设置
    @DeleteMapping("batchRemove")
    public Result batchRemoveHospitalSets(@RequestBody List<Long> list) {
        hospitalSetService.removeByIds(list);
        return Result.ok();
    }

    //8 医院设置锁定和解锁操作  status： 1 解锁 0 锁定
    @PutMapping("lockHospitalSet/{id}/{status}")
    public Result lockHospitalSet(@PathVariable Long id, @PathVariable Integer status) {
        //根据id查询医院设置信息
        HospitalSet hospitalSet = hospitalSetService.getById(id);
        hospitalSet.setStatus(status);

        hospitalSetService.updateById(hospitalSet);
        return Result.ok();
    }

    //9 发送签名密钥SignKey
    @PutMapping("sendKey/{id}")
    public Result sendKey(@PathVariable Long id) {
        HospitalSet hospitalSet = hospitalSetService.getById(id);
        String signKey = hospitalSet.getSignKey();
        String hoscode = hospitalSet.getHoscode();
        //TODO发送短信（日后完善）
        return Result.ok();
    }
}
