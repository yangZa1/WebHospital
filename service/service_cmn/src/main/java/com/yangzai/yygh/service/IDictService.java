package com.yangzai.yygh.service;

import com.yangzai.yygh.entity.Dict;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * <p>
 * 组织架构表 服务类
 * </p>
 *
 * @author yangzai
 * @since 2022-11-22
 */
public interface IDictService extends IService<Dict> {

    List<Dict> findChildData(Long id);

    void exportDictData(HttpServletResponse response);

    void importDictData(MultipartFile file);

    String getDictName(String dictCode, String value);

    List<Dict> findByDictCode(String dictCode);
}
