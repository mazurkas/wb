package com.whosenet.wb.core.service;



/**
 * Utils-Service工具类.
 *
 * @author <a href="http://ruo.whosenet.com">ruo.whosenet.com</>
 * @version 1.0
 */
import com.whosenet.wb.core.web.Filter;
import com.whosenet.wb.core.web.Order;
import com.whosenet.wb.core.web.Page;
import com.whosenet.wb.core.web.PageView;

import javax.persistence.LockModeType;
import java.io.Serializable;
import java.util.List;

public interface BaseService <T, ID extends Serializable> {


    public long getCount();

    /**
     * 清除一级缓存的数据
     */
    public void clear();

    /**
     * 设置为游离状态
     *
     * @param entity 实体对象
     */
    void detach(T entity);

    /**
     * 同步数据
     */
    void flush();

    /**
     * 锁定实体对象
     *
     * @param entity 实体对象
     * @param lockModeType 锁定方式
     */
    void lock(T entity, LockModeType lockModeType);

    /**
     * 保存实体
     * 
     * @param entity 实体id
     */
    public void save(T entity);


    /**
     * 刷新实体对象
     *
     * @param entity 实体对象
     * @param lockModeType 锁定方式
     */
    void refresh(T entity, LockModeType lockModeType);

    /**
     * 更新实体
     * 
     * @param entity 实体id
     */
    public T update(T entity);

    public void updateEntity(T entity, ID id);

    /**
     * 删除实体对象
     *
     * @param id ID
     */
    void delete(ID id);

    /**
     * 移除实体对象
     *
     * @param entity
     *            实体对象
     */
    void delete(T entity);


    /**
     * 删除实体对象
     *
     * @param ids ID
     */
    void delete(ID... ids);

    /**
     * 获取实体
     * @return
     */
    public T find(ID entityId);

    /**
     * 查询记录数目.
     * 
     * @param whereJPQL where 条件
     * @param queryParams 查询参数
     * @return 条数
     */
    public long getQueryCount(String whereJPQL, Object[] queryParams);

    public long getQueryCount(List<Filter> filters);

    /**
     * 获取分页数据
     * @return
     */
    public List<T> getListData(int firstIndex, int maxResult, String whereJPQL, Object[] queryParams, List<Order> orderBy);

    public List<T> getListData(int firstIndex, int maxResult, String whereJPQL, Object[] queryParams);

    public List<T> getListData(int firstIndex, int maxResult, List<Order> orderBy);

    public List<T> getListData(String whereJPQL, Object[] queryParams, List<Order> orderBy);

    public List<T> getListData(String whereJPQL, Object[] queryParams);

    public List<T> getListData(int firstIndex, int maxResult);

    public List<T> getListData(List<Order> orderBy);

    public List<T> getListData();

    public List<T> findList(ID... ids);

    public List<T> findList(int firstIndex, int maxResult, List<Filter> filters, List<Order> orderBy);

    public List<T> findList(int firstIndex, int maxResult, List<Filter> filters);

    public List<T> findList(List<Filter> filters, List<Order> orderBy);

    public List<T> findList(List<Filter> filters);


    /**
     * 获取分页数据,带页面封装
     * @return
     */
    public Page<T> getScrollData(PageView pageView);




}
