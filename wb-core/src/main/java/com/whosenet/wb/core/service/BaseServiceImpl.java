package com.whosenet.wb.core.service;


import com.whosenet.wb.core.utils.BeanTool;
import com.whosenet.wb.core.utils.GenericsUtils;
import com.whosenet.wb.core.web.Filter;
import com.whosenet.wb.core.web.Order;
import com.whosenet.wb.core.web.Page;
import com.whosenet.wb.core.web.PageView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.*;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Utils-公用Service.
 *
 * @author <a href="http://ruo.whosenet.com">ruo.whosenet.com</>
 * @version 1.0
 */
@Transactional
@SuppressWarnings("unchecked")
public  class BaseServiceImpl<T, ID extends Serializable> implements BaseService<T, ID> {

    MessageSourceAccessor messageSource;
    /** 别名数 */
    private static volatile long aliasCount = 0;


    final protected void debug(String msg){
        if(logger.isDebugEnabled()){
            logger.debug(msg);
        }
    }

    final protected void info(String msg){
        if(logger.isInfoEnabled()){
            logger.info(msg);
        }
    }

    final protected String getMessage(String key){
        return this.messageSource.getMessage(key);
    }

    final protected String getMessage(String key,Object[] args){
        return this.messageSource.getMessage(key,args);
    }

    @Autowired
    public void setMessageSource(MessageSource messageSource) {
        this.messageSource = new MessageSourceAccessor(messageSource, Locale.getDefault());
    }

    protected Class<T> entityClass = GenericsUtils.getSuperClassGenricType(this.getClass());

    @PersistenceContext
    protected EntityManager em;

    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Transactional(readOnly=true,propagation= Propagation.NOT_SUPPORTED)
    public long getCount() {
        TypedQuery<Long> query = em.createQuery("select count("+ getCountField(this.entityClass) +") from "+ getEntityName(this.entityClass)+ " o", Long.class);
        return query.getSingleResult();
    }

    /**
     * 获取实体的名称
     */
    protected String getEntityName(Class<T> clazz) {
        String entityName = clazz.getSimpleName();
        Entity entity = clazz.getAnnotation(Entity.class);
        if(entity.name()!=null && !"".equals(entity.name())){
            entityName = entity.name();
        }
        return entityName;
    }

    /**
     * 获取统计属性,该方法是为了解决hibernate解析联合主键select count(o) from Xxx o语句BUG而增加,hibernate对此jpql解析后的sql为select count(field1,field2,...),显示使用count()统计多个字段是错误的
     */
    protected String getCountField(Class<T> clazz) {
        String out = "o";
        try {
            PropertyDescriptor[] propertyDescriptors = Introspector.getBeanInfo(clazz).getPropertyDescriptors();
            for(PropertyDescriptor propertydesc : propertyDescriptors){
                Method method = propertydesc.getReadMethod();
                if(method!=null && method.isAnnotationPresent(EmbeddedId.class)){
                    PropertyDescriptor[] ps = Introspector.getBeanInfo(propertydesc.getPropertyType()).getPropertyDescriptors();
                    out = "o."+ propertydesc.getName()+ "." + (!ps[1].getName().equals("class")? ps[1].getName(): ps[0].getName());
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return out;
    }

    /**
     * 设置查询参数
     * @param query
     * @param queryParams
     */
    protected void setQueryParams(Query query, Object[] queryParams) {
        if(queryParams!=null && queryParams.length>0){
            for(int i=0; i<queryParams.length; i++){
                query.setParameter(i+1, queryParams[i]);
            }
        }
    }

    /**
     * 组装order by语句
     * @param orderBy
     * @return
     */
    protected String buildOrderBy(List<Order> orderBy) {
        StringBuffer orderByQL = new StringBuffer("");
        if(orderBy!=null && orderBy.size()>0){
            orderByQL.append(" order by ");
            for(Order order:orderBy){
                orderByQL.append("o.").append(order.getProperty()).append(" ").append(order.getDirection()).append(",");
            }
            orderByQL.deleteCharAt(orderByQL.length()-1);
        }
        return orderByQL.toString();
    }


    /** 将 Filter生成SQL */
    public String buildFilter(List<Filter> filters){

        StringBuilder sb=new StringBuilder();
        for(int i=0;i<filters.size();i++){
            Filter filter=filters.get(i);
            if(filter.getOperator()==Filter.Operator.like&& filter.getValue() != null&& filter.getValue() instanceof String){
                sb.append("o.").append(filter.getProperty()).append(" LIKE ").append("?"+(i+1));
            }
            if(filter.getOperator()==Filter.Operator.eq&& filter.getValue() != null){
                sb.append("o.").append(filter.getProperty()).append(" = ").append("?"+(i+1));
            }
            if(filter.getOperator()==Filter.Operator.ne&& filter.getValue() != null){
                sb.append("o.").append(filter.getProperty()).append(" != ").append("?"+(i+1));
            }
            if(filter.getOperator()==Filter.Operator.gt&& filter.getValue() != null){
                sb.append("o.").append(filter.getProperty()).append(" > ").append("?"+(i+1));
            }
            if(filter.getOperator()==Filter.Operator.lt&& filter.getValue() != null){
                sb.append("o.").append(filter.getProperty()).append(" < ").append("?"+(i+1));
            }
            if(filter.getOperator()==Filter.Operator.ge&& filter.getValue() != null){
                sb.append("o.").append(filter.getProperty()).append(" >= ").append("?"+(i+1));
            }
            if(filter.getOperator()==Filter.Operator.le&& filter.getValue() != null){
                sb.append("o.").append(filter.getProperty()).append(" <= ").append("?"+(i+1));
            }
            if(filter.getOperator()==Filter.Operator.in&& filter.getValue() != null){
                sb.append("o.").append(filter.getProperty()).append(" in ").append("?"+(i+1));
            }
            if(filter.getOperator()==Filter.Operator.isNull&& filter.getValue() != null){
                sb.append("o.").append(filter.getProperty()).append(" is null ");
            }
            if(i<filters.size()-1){
                sb.append(" and ");
            }
        }
        return sb.toString();
    }


    public Object[]  getQueryParams(List<Filter> filters){

         Object[] queryParams=new Object[filters.size()];
          for(int i=0;i<filters.size();i++){
              Filter filter=filters.get(i);
              if(filter.getOperator()== Filter.Operator.like){
                  queryParams[i]="%"+filters.get(i).getValue()+"%";
              }else{
                  queryParams[i]=filters.get(i).getValue();
              }
          }
          return queryParams;
    }


    /*******  Parent Method ***/

    /** 清楚一级缓存 */
    public void clear(){
        em.clear();
    }


    /**
     * 锁定实体对象
     *
     * @param entity  实体对象
     * @param lockModeType 锁定方式
     */
    public void lock(T entity, LockModeType lockModeType) {
        if (entity != null && lockModeType != null) {
            em.lock(entity, lockModeType);
        }
    }

    /** 设为游离状态 */
    public void detach(T entity) {
        em.detach(entity);
    }



    public void refresh(T entity) {
        if (entity != null) {
            em.refresh(entity);
        }
    }


    public void refresh(T entity, LockModeType lockModeType) {
        if (entity != null) {
            if (lockModeType != null) {
                em.refresh(entity, lockModeType);
            } else {
                em.refresh(entity);
            }
        }
    }



    /**  同步数据 */
    public void flush() {
        em.flush();
    }

    @Transactional
    public void save(T entity) {
        em.persist(entity);
    }

    @Transactional
    public T update(T entity) {
        return em.merge(entity);
    }

    @Transactional
    public void updateEntity(T entity,ID id){
        T ordEntity= (T) em.find(entity.getClass(),id);
        BeanTool.copyNonNullProperties(entity, ordEntity);
    }

    @Transactional
    public void delete(ID id) {
        em.remove(em.getReference(this.entityClass, id));
    }

    @Transactional
    public void delete(ID... ids) {
        if (ids != null) {
            for (ID id : ids) {
                em.remove(em.getReference(this.entityClass, id));
            }
        }
    }

    @Transactional
    public void delete(T entity) {
        em.remove(entity);
    }



    @Transactional(readOnly=true,propagation=Propagation.NOT_SUPPORTED)
    public T find(ID entityId) {
        if(entityId==null) throw new RuntimeException(this.entityClass.getName()+ ":传入的实体id不能为空");
        return em.find(this.entityClass, entityId);
    }

       /*
     *   Query for Count
     * */

    @Transactional(readOnly=true,propagation=Propagation.NOT_SUPPORTED)
    public long getQueryCount(String whereJPAL, Object[] queryParams) {
        String entityName = getEntityName(this.entityClass);
        TypedQuery<Long> query = em.createQuery("select count("+ getCountField(this.entityClass)+ ") from "+ entityName+ " o "+(whereJPAL==null || "".equals(whereJPAL.trim())? "": "where "+ whereJPAL), Long.class);
        setQueryParams(query, queryParams);
        return query.getSingleResult();
    }

    @Transactional(readOnly=true,propagation=Propagation.NOT_SUPPORTED)
    public long getQueryCount(List<Filter> filters) {
        String entityName = getEntityName(this.entityClass);
        TypedQuery<Long> query = em.createQuery("select count("+ getCountField(this.entityClass)+ ") from "+ entityName+ " o "+(filters==null || filters.size()==0? "": "where "+ buildFilter(filters)), Long.class);
        setQueryParams(query, getQueryParams(filters));
        return query.getSingleResult();
    }




    @Transactional(readOnly=true,propagation=Propagation.NOT_SUPPORTED)
    public List<T> findList(int firstIndex, int maxResult, List<Filter> filters, List<Order> orderBy) {
        String entityName = getEntityName(this.entityClass);
        TypedQuery<T> query = em.createQuery("select o from "+ entityName+ " o "+(filters==null ||filters.size()==0? "": "where "+ buildFilter(filters))+ buildOrderBy(orderBy), this.entityClass);
        setQueryParams(query, getQueryParams(filters));
        if(firstIndex!=-1 && maxResult!=-1) query.setFirstResult(firstIndex).setMaxResults(maxResult);
        return query.getResultList();
    }




    @Transactional(readOnly=true,propagation=Propagation.NOT_SUPPORTED)
    public List<T> findList(int firstIndex, int maxResult, List<Filter> filters) {
        return findList(firstIndex, maxResult, filters, null);
    }


    @Transactional(readOnly=true,propagation=Propagation.NOT_SUPPORTED)
    public List<T> findList(List<Filter> filters, List<Order> orderBy) {
        return findList(-1, -1, filters, orderBy);
    }

    @Transactional(readOnly=true,propagation=Propagation.NOT_SUPPORTED)
    public List<T> findList(List<Filter> filters) {
        return findList(filters, null);
    }








    @Transactional(readOnly=true,propagation=Propagation.NOT_SUPPORTED)
    public List<T> getListData(int firstIndex, int maxResult, String whereJPQL, Object[] queryParams) {
        return getListData(firstIndex, maxResult, whereJPQL, queryParams, null);
    }


    @Transactional(readOnly=true,propagation=Propagation.NOT_SUPPORTED)
    public List<T> getListData(int firstIndex, int maxResult, List<Order> orderBy) {
        return getListData(firstIndex, maxResult, null, null, orderBy);
    }

    @Transactional(readOnly=true,propagation=Propagation.NOT_SUPPORTED)
    public List<T> getListData(int firstIndex, int maxResult) {
        return getListData(firstIndex, maxResult, null);
    }

    //
    @Transactional(readOnly=true,propagation=Propagation.NOT_SUPPORTED)
    public List<T> getListData(int firstIndex, int maxResult, String whereJPQL, Object[] queryParams, List<Order> orderBy) {
        String entityName = getEntityName(this.entityClass);
        TypedQuery<T> query = em.createQuery("select o from "+ entityName+ " o "+(whereJPQL==null || "".equals(whereJPQL.trim())? "": "where "+ whereJPQL)+ buildOrderBy(orderBy), this.entityClass);
        setQueryParams(query, queryParams);
        if(firstIndex!=-1 && maxResult!=-1) query.setFirstResult(firstIndex).setMaxResults(maxResult);
        return query.getResultList();
    }


    @Transactional(readOnly=true,propagation=Propagation.NOT_SUPPORTED)
    public List<T> getListData(String whereJPQL, Object[] queryParams, List<Order> orderBy) {
        return getListData(-1, -1, whereJPQL, queryParams, orderBy);
    }


    @Transactional(readOnly=true,propagation=Propagation.NOT_SUPPORTED)
    public List<T> getListData(String whereJPQL, Object[] queryParams) {
        return getListData(whereJPQL, queryParams, null);
    }



    @Transactional(readOnly=true,propagation=Propagation.NOT_SUPPORTED)
    public List<T> getListData(List<Order> orderBy) {
        return getListData(null, null, orderBy);
    }


    @Transactional(readOnly=true,propagation=Propagation.NOT_SUPPORTED)
    public List<T> getListData() {
        return getListData(null, null);
    }



    /*
     *   自动装载分页
     * */

    @Transactional(readOnly=true,propagation=Propagation.NOT_SUPPORTED)
    public Page<T> getScrollData(PageView pageView) {
        Long count=getQueryCount(pageView.getFilters()) ;
        int firstIndex=(pageView.getPageNum()-1)*pageView.getNumPerPage();
        int maxResult=pageView.getNumPerPage();
        List<T> content= findList(firstIndex, maxResult, pageView.getFilters(), pageView.getOrders());
        return  new Page<T>(content,count,pageView);
    }

    @Transactional(readOnly = true)
    public List<T> findList(ID... ids) {
        List<T> result = new ArrayList<T>();
        if (ids != null) {
            for (ID id : ids) {
                T entity = find(id);
                if (entity != null) {
                    result.add(entity);
                }
            }
        }
        return result;
    }






}
