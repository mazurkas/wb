package com.whosenet.wb.portal.entity;

import com.whosenet.wb.core.entity.BaseEntity;
import com.whosenet.wb.core.utils.FreemarkerUtils;
import com.whosenet.wb.portal.CommonAttributes;
import freemarker.template.TemplateException;
import org.apache.commons.lang.StringUtils;
import org.dom4j.io.SAXReader;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.springframework.core.io.ClassPathResource;

import javax.persistence.*;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Entity - 文章
 *
 * @author <a href="http://www.whosenet.com">whosenet.com</>
 * @version 1.0
 */
@Entity
@Table(name = "wb_portal_article")
@SequenceGenerator(name = "sequenceGenerator", sequenceName = "wb_portal_article_sequence")
public class Article extends BaseEntity {

    private static final long serialVersionUID = 1475773294701585482L;

    /** 点击数缓存名称 */
    public static final String HITS_CACHE_NAME = "articleHits";

    /** 点击数缓存更新间隔时间 */
    public static final int HITS_CACHE_INTERVAL = 600000;

    /** 内容分页长度 */
    private static final int PAGE_CONTENT_LENGTH = 800;

    /** 内容分页符 */
    private static final String PAGE_BREAK_SEPARATOR = "<hr class=\"pageBreak\" />";

    /** 段落分隔符配比 */
    private static final Pattern PARAGRAPH_SEPARATOR_PATTERN = Pattern.compile("[,;\\.!?，；。！？]");

    /** 文章静态路径-转换前 */
    private static String staticPath;

    /** 文章静态路径-转换前 */
    private static String pageStaticPath;

    static {
        try {
            File xmlFile = new ClassPathResource(CommonAttributes.APP_XML_PATH).getFile();
            org.dom4j.Document document = new SAXReader().read(xmlFile);

            org.dom4j.Element element = (org.dom4j.Element) document.selectSingleNode("/application/template[@id='articleContent']");
            staticPath = element.attributeValue("staticPath");

            org.dom4j.Element pageElement = (org.dom4j.Element) document.selectSingleNode("/application/template[@id='pageContent']");
            pageStaticPath = pageElement.attributeValue("staticPath");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /** 标题 */
    @NotEmpty
    @Length(max = 200)
    @Column(nullable = false)
    private String title;



    /** 作者 */
    @Length(max = 200)
    private String author;

    /** 内容 */
    @Lob
    private String content;

    /** 简介 */
    @Lob
    @Column
    private String summary;

    /** 是否发布 */
    @Column
    private Integer status;

    /** 点击数 */
    @Column
    private Long hits;

    /** 页码 */
    @Column
    private Integer pageNumber;

    /** 是否发布 */
    @Column
    private Boolean isPublication;


    public enum Type{
        article,page
    }

    @Column
    private Type type;

    /** 标签 */
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "wb_article_tag")
    @OrderBy("order asc")
    private Set<Tag> tags = new HashSet<Tag>();


    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;


    /**
     * 获取标题
     *
     * @return 标题
     */


    public String getTitle() {
        return title;
    }

    /**
     * 设置标题
     *
     * @param title
     *            标题
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * 获取作者
     *
     * @return 作者
     */

    public String getAuthor() {
        return author;
    }

    /**
     * 设置作者
     *
     * @param author
     *            作者
     */
    public void setAuthor(String author) {
        this.author = author;
    }




    /**
     * 获取内容
     *
     * @return 内容
     */

    public String getContent() {
        if (pageNumber != null) {
            String[] pageContents = getPageContents();
            if (pageNumber < 1) {
                pageNumber = 1;
            }
            if (pageNumber > pageContents.length) {
                pageNumber = pageContents.length;
            }
            return pageContents[pageNumber - 1];
        } else {
            return content;
        }
    }

    /**
     * 设置内容
     *
     * @param content
     *            内容
     */
    public void setContent(String content) {
        this.content = content;
    }


    /**
     * 获取点击数
     *
     * @return 点击数
     */
    public Long getHits() {
        return hits;
    }

    /**
     * 设置点击数
     *
     * @param hits
     *            点击数
     */
    public void setHits(Long hits) {
        this.hits = hits;
    }

    /**
     * 获取页码
     *
     * @return 页码
     */
    @Transient
    public Integer getPageNumber() {
        return pageNumber;
    }

    /**
     * 设置页码
     *
     * @param pageNumber
     *            页码
     */
    @Transient
    public void setPageNumber(Integer pageNumber) {
        this.pageNumber = pageNumber;
    }



    /**
     * 获取访问路径
     * 访问路径转换，例如：/page/${title}.html  转换为：/page/xxx.html  (xxx为标题)
     * @return 访问路径
     */
    @Transient
    public String getPath() {

        Map<String, Object> model = new HashMap<String, Object>();
        model.put("id", getId());
        model.put("createDate", getCreateDate());
        model.put("createTime",getCreateDate().getTime());
        model.put("modifyDate", getModifyDate());
        if(!getTitle().equals("")){
            model.put("title", getTitle().replace(" ","_"));
        }
        model.put("category",getCategory());
        model.put("pageNumber", getPageNumber());
        try {
            if(getType()==Type.article){
                return FreemarkerUtils.process(staticPath, model);
            }else{
                return FreemarkerUtils.process(pageStaticPath, model);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (TemplateException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取文本内容
     *
     * @return 文本内容
     */
    @Transient
    public String getText() {
        if (getContent() != null) {
            return Jsoup.parse(getContent()).text();
        }
        return null;
    }

    /**
     * 获取标签
     *
     * @return 标签
     */

    public Set<Tag> getTags() {
        return tags;
    }

    /**
     * 设置标签
     *
     * @param tags
     *            标签
     */
    public void setTags(Set<Tag> tags) {
        this.tags = tags;
    }



    /**
     * 获取分页内容
     *
     * @return 分页内容
     */
    @Transient
    public String[] getPageContents() {
        if (StringUtils.isEmpty(content)) {
            return new String[] { "" };
        }
        if (content.contains(PAGE_BREAK_SEPARATOR)) {
            return content.split(PAGE_BREAK_SEPARATOR);
        } else {
            List<String> pageContents = new ArrayList<String>();
            Document document = Jsoup.parse(content);
            List<Node> children = document.body().childNodes();
            if (children != null) {
                int textLength = 0;
                StringBuffer html = new StringBuffer();
                for (Node node : children) {
                    if (node instanceof Element) {
                        Element element = (Element) node;
                        html.append(element.outerHtml());
                        textLength += element.text().length();
                        if (textLength >= PAGE_CONTENT_LENGTH) {
                            pageContents.add(html.toString());
                            textLength = 0;
                            html.setLength(0);
                        }
                    } else if (node instanceof TextNode) {
                        TextNode textNode = (TextNode) node;
                        String text = textNode.text();
                        String[] contents = PARAGRAPH_SEPARATOR_PATTERN.split(text);
                        Matcher matcher = PARAGRAPH_SEPARATOR_PATTERN.matcher(text);
                        for (String content : contents) {
                            if (matcher.find()) {
                                content += matcher.group();
                            }
                            html.append(content);
                            textLength += content.length();
                            if (textLength >= PAGE_CONTENT_LENGTH) {
                                pageContents.add(html.toString());
                                textLength = 0;
                                html.setLength(0);
                            }
                        }
                    }
                }
                String pageContent = html.toString();
                if (StringUtils.isNotEmpty(pageContent)) {
                    pageContents.add(pageContent);
                }
            }
            return pageContents.toArray(new String[pageContents.size()]);
        }
    }

    /**
     * 获取总页数
     *
     * @return 总页数
     */
    @Transient
    public int getTotalPages() {
        return getPageContents().length;
    }


    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }


    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public Boolean getIsPublication() {
        return isPublication;
    }

    public void setIsPublication(Boolean isPublication) {
        this.isPublication = isPublication;
    }
}
