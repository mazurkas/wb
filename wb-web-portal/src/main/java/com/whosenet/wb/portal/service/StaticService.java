package com.whosenet.wb.portal.service;

import com.whosenet.wb.portal.entity.Article;

import java.util.Map;


public interface StaticService {

    /**
     * 生成静态
     *
     * @param templatePath 模板文件路径
     * @param staticPath 静态文件路径
     * @param model 数据
     * @return 生成数量
     */
    int build(String templatePath, String staticPath, Map<String, Object> model);

    /**
     * 生成静态
     *
     * @param templatePath 模板文件路径
     * @param staticPath 静态文件路径
     * @return 生成数量
     */
    int build(String templatePath, String staticPath);

    /**
     * 生成静态
     *
     * @param article 文章
     * @return 生成数量
     */
    int build(Article article);

    /**
     * 删除静态
     *
     * @param staticPath 静态文件路径
     * @return 删除数量
     */
    int delete(String staticPath);

    /**
     * 删除静态
     *
     * @param article 文章
     * @return 删除数量
     */
    int delete(Article article);

}
