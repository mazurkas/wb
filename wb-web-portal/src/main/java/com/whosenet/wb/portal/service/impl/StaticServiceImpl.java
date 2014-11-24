package com.whosenet.wb.portal.service.impl;

import com.whosenet.wb.portal.Template;
import com.whosenet.wb.portal.entity.Article;
import com.whosenet.wb.portal.service.StaticService;
import com.whosenet.wb.portal.service.TemplateService;
import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.web.context.ServletContextAware;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import javax.annotation.Resource;
import javax.servlet.ServletContext;
import java.io.*;
import java.util.HashMap;
import java.util.Map;


@Service
public class StaticServiceImpl  implements StaticService, ServletContextAware {

    /** servletContext */
    private ServletContext servletContext;

    @Resource(name = "freeMarkerConfigurer")
    private FreeMarkerConfigurer freeMarkerConfigurer;


    @Resource
    private TemplateService templateService;

    @Override
    public void setServletContext(ServletContext servletContext) {
        this.servletContext = servletContext;
    }

    @Override
    public int build(String templatePath, String staticPath, Map<String, Object> model) {
        Assert.hasText(templatePath);
        Assert.hasText(staticPath);
        FileOutputStream fileOutputStream = null;
        OutputStreamWriter outputStreamWriter = null;
        Writer writer = null;
        try {
            freemarker.template.Template template = freeMarkerConfigurer.getConfiguration().getTemplate(templatePath);
            File staticFile = new File(servletContext.getRealPath(staticPath));
            File staticDirectory = staticFile.getParentFile();
            if (!staticDirectory.exists()) {
                staticDirectory.mkdirs();
            }
            fileOutputStream = new FileOutputStream(staticFile);
            outputStreamWriter = new OutputStreamWriter(fileOutputStream, "UTF-8");
            writer = new BufferedWriter(outputStreamWriter);
            template.process(model, writer);
            writer.flush();
            return 1;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeQuietly(writer);
            IOUtils.closeQuietly(outputStreamWriter);
            IOUtils.closeQuietly(fileOutputStream);
        }
        return 0;
    }

    @Override
    public int build(String templatePath, String staticPath) {
        return build(templatePath, staticPath, null);
    }

    @Override
    public int build(Article article) {
        Assert.notNull(article);
        delete(article);
        if(article.getType()== Article.Type.page){
            Template template = templateService.get("pageArticleContent");
            Map<String, Object> model = new HashMap<String, Object>();
            model.put("article", article);
            return build(template.getTemplatePath(), article.getPath(), model);
        }else{
            Template template = templateService.get("articleContent");
            int buildCount = 0;
            if (article.getIsPublication()) {
                Map<String, Object> model = new HashMap<String, Object>();
                model.put("article", article);
                for (int pageNumber = 1; pageNumber <= article.getTotalPages(); pageNumber++) {
                    article.setPageNumber(pageNumber);
                    buildCount += build(template.getTemplatePath(), article.getPath(), model);
                }
                article.setPageNumber(null);
            }
            return buildCount;
        }
    }

    @Override
    public int delete(String staticPath) {
        Assert.hasText(staticPath);
        File staticFile = new File(servletContext.getRealPath(staticPath));
        if (staticFile.exists()) {
            staticFile.delete();
            return 1;
        }
        return 0;
    }

    @Override
    public int delete(Article article) {
        Assert.notNull(article);
        int deleteCount = 0;
        for (int pageNumber = 1; pageNumber <= article.getTotalPages() + 1000; pageNumber++) {
            article.setPageNumber(pageNumber);
            int count = delete(article.getPath());
            if (count < 1) {
                break;
            }
            deleteCount += count;
        }
        article.setPageNumber(null);
        return deleteCount;
    }
}
