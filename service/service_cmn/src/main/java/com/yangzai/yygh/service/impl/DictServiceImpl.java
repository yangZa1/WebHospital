package com.yangzai.yygh.service.impl;

import com.alibaba.excel.EasyExcel;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.yangzai.yygh.entity.Dict;
import com.yangzai.yygh.listener.DictListener;
import com.yangzai.yygh.mapper.DictMapper;
import com.yangzai.yygh.result.Result;
import com.yangzai.yygh.service.IDictService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yangzai.yygh.vo.cmn.DictEeVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 组织架构表 服务实现类
 * </p>
 *
 * @author yangzai
 * @since 2022-11-22
 */
@Service
public class DictServiceImpl extends ServiceImpl<DictMapper, Dict> implements IDictService {

    @Autowired
    DictMapper dictMapper;

    @Override
    //开启缓存机制（SpringCache + Redis）
    //@Cacheable(value = "dict", keyGenerator = "keyGenerator")
    public List<Dict> findChildData(Long id) {
        QueryWrapper<Dict> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("parent_id", id);
        List<Dict> list = baseMapper.selectList(queryWrapper);
        //向list集合中每个dict对象中设置hasChildren
        for (Dict dict : list) {
            Long dictId = dict.getId();
            boolean isChildren = this.isChildren(dictId);
            dict.setHasChildren(isChildren);
        }
        return list;
    }

    //导出数据字典接口
    @Override
    public void exportDictData(HttpServletResponse response) {
        //下载的基本操作(设置下载信息)
        response.setContentType("application/vnd.ms-excel");
        response.setCharacterEncoding("utf-8");
        // 这里URLEncoder.encode可以防止中文乱码 当然和easyexcel没有关系
//        String fileName = URLEncoder.encode("数据字典", "UTF-8");
        String fileName = "dict";
        //以下载的方式打开
        response.setHeader("Content-disposition", "attachment;filename=" + fileName + ".xlsx");

        //查询数据库
        List<Dict> dictList = baseMapper.selectList(null);

        //Dict对象转换为DictEeVo
        List<DictEeVo> dictVoList = new ArrayList<>(dictList.size());
        for (Dict dict : dictList) {
            DictEeVo dictVo = new DictEeVo();
            BeanUtils.copyProperties(dict, dictVo);
            dictVoList.add(dictVo);
        }

        //调用方法进行写操作
        try {
            EasyExcel.write(response.getOutputStream(), DictEeVo.class).sheet("dict").doWrite(dictVoList);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    //导入数据字典
    //开启缓存机制（SpringCache + Redis） 更新数据时先删除redis中缓存数据
    //@CacheEvict(value = "dict", allEntries = true )
    @Override
    public void importDictData(MultipartFile file) {
        try {
            EasyExcel.read(file.getInputStream(), DictEeVo.class, new DictListener(dictMapper)).sheet().doRead();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Dict getDictByDictCode(String dictCode){
        QueryWrapper<Dict> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("dict_code", dictCode);
        Dict dict = baseMapper.selectOne(queryWrapper);
        return dict;
    }

    @Override
    public String getDictName(String dictCode, String value) {
        //如果dictCode为空，直接根据value进行查询
        if(StringUtils.isEmpty(dictCode)){
            QueryWrapper<Dict> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("value", value);
            Dict dict = baseMapper.selectOne(queryWrapper);
            return dict.getName();
        }else{
            //根据dictCode查询dict对象 得到dict的id值
            QueryWrapper<Dict> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("dict_code", dictCode);
            Dict codeDict = baseMapper.selectOne(queryWrapper);
            Long parentId = codeDict.getId();
            //根据parentid 和 value 查询dictName
            Dict dict = baseMapper.selectOne(new QueryWrapper<Dict>().eq("parent_id", parentId)
                    .eq("value", value));
            return dict.getName();
        }
    }

    //根据dictCode获取下级节点
    @Override
    public List<Dict> findByDictCode(String dictCode) {
        //根据dictCode获取对应的Id
        Dict dict = this.getDictByDictCode(dictCode);
        //根据id获取字节點
        List<Dict> list = this.findChildData(dict.getId());
        return list;
    }


    //判断id下面是否有子节点
    private boolean isChildren(Long id) {
        QueryWrapper<Dict> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("parent_id", id);
        Long count = baseMapper.selectCount(queryWrapper);
        return count > 0;
    }
}
