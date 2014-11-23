/*
 * <File Name>
 * Copyright (c) 2014 www.whosenet.com
 */

package com.whosenet.wb.core.web;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Utils-页面分页入口参数.
 *
 * @author <a href="http://ruo.whosenet.com">ruo.whosenet.com</>
 * @version 1.0
 */
public class Page <T> implements Serializable {

    private static final long serialVersionUID = -2053800594583879853L;

    /** 内容 */
    private final List<T> content = new ArrayList<T>();

    /** 总记录数 */
    private final long total;

    /** 分页信息 */
    private final PageView pageView;

    /**
     * 初始化一个新创建的Page对象
     */
    public Page() {
        this.total = 0L;
        this.pageView = new PageView();
    }

    /**
     * @param content
     *            内容
     * @param total
     *            总记录数
     * @param mPageView
     *            分页信息
     */
    public Page(List<T> content, long total, PageView mPageView) {
        this.content.addAll(content);
        this.total = total;
        this.pageView = mPageView;
    }

    /**
     * 获取页码
     *
     * @return 页码
     */
    public int getPageNum() {
        return pageView.getPageNum();
    }

    /**
     * 获取每页记录数
     *
     * @return 每页记录数
     */
    public int getNumPerPage() {
        return pageView.getNumPerPage();
    }



    /**
     * 获取排序属性
     *
     * @return 排序属性
     */
    public String getOrderField() {
        return pageView.getOrderField();
    }



    /**
     * 获取筛选
     *
     * @return 筛选
     */
    public List<Filter> getFilters() {
        return pageView.getFilters();
    }

    /**
     * 获取总页数
     *
     * @return 总页数
     */
    public int getTotalPages() {
        return (int) Math.ceil((double) getTotal() / (double) getNumPerPage());
    }

    /**
     * 获取内容
     *
     * @return 内容
     */
    public List<T> getContent() {
        return content;
    }

    /**
     * 获取总记录数
     *
     * @return 总记录数
     */
    public long getTotal() {
        return total;
    }

    /**
     * 获取分页信息
     *
     * @return 分页信息
     */
    public PageView getmPageView() {
        return pageView;
    }
}
