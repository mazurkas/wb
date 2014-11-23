
package com.whosenet.wb.core.web;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.HashCodeBuilder;

import javax.servlet.http.HttpServletRequest;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Utils-分页内容Entity.
 *
 * @author <a href="http://ruo.whosenet.com">ruo.whosenet.com</>
 * @version 1.0
 */
public class PageView <T> {

    private static final long serialVersionUID = -3930180379790344299L;

    /** 默认页码 */
    private static final int DEFAULT_PAGE_NUMBER = 1;

    /** 默认每页记录数 */
    private static final int DEFAULT_PAGE_SIZE = 20;

    /** 最大每页记录数 */
    private static final int MAX_PAGE_SIZE = 1000;

    /** 页码 */
    private int pageNum = DEFAULT_PAGE_NUMBER;

    /** 每页记录数 */
    private int numPerPage = DEFAULT_PAGE_SIZE;

    /** 排序属性 */
    private String orderField;

    /** 筛选 */
    private List<Filter> filters = new ArrayList<Filter>();


    /** 排序 */
    private List<Order> orders = new ArrayList<Order>();




    public PageView(){

    }

    /**
     * 初始化一个新创建的Pageable对象
     *
     * @param pageNum
     *            页码
     * @param numPerPage
     *            每页记录数
     */
    public PageView(Integer pageNum, Integer numPerPage) {
        if (pageNum != null && pageNum >= 1) {
            this.pageNum = pageNum;
        }
        if (numPerPage != null && numPerPage >= 1 && numPerPage <= MAX_PAGE_SIZE) {
            this.numPerPage = numPerPage;
        }
    }

    /**
     * 获取页码
     *
     * @return 页码
     */
    public int getPageNum() {
        return pageNum;
    }

    /**
     * 设置页码
     *
     * @param pageNum
     *            页码
     */
    public void setPageNum(int pageNum) {
        if (pageNum < 1) {
            pageNum = DEFAULT_PAGE_NUMBER;
        }
        this.pageNum = pageNum;
    }

    /**
     * 获取每页记录数
     *
     * @return 每页记录数
     */
    public int getNumPerPage() {
        return numPerPage;
    }

    /**
     * 设置每页记录数
     *
     * @param numPerPage
     *            每页记录数
     */
    public void setNumPerPage(int numPerPage) {
        if (numPerPage < 1 || numPerPage > MAX_PAGE_SIZE) {
            numPerPage = DEFAULT_PAGE_SIZE;
        }
        this.numPerPage = numPerPage;
    }



    /**
     * 获取排序属性
     *
     * @return 排序属性
     */
    public String getOrderField() {
        return orderField;
    }

    /**
     * 设置排序属性
     *
     * @param orderField
     *            排序属性
     */
    public void setOrderField(String orderField) {
        this.orderField = orderField;
    }

    public List<Order> getOrders() {
        return orders;
    }

    public void setOrders(List<Order> orders) {
        this.orders = orders;
    }

    /**
     * 获取筛选
     *
     * @return 筛选
     */
    public List<Filter> getFilters() {
        return filters;
    }

    /**
     * 设置筛选
     *
     * @param filters
     *            筛选
     */
    public void setFilters(List<Filter> filters) {
        this.filters = filters;
    }

    /**
     * 参数类型
     * */
    private Object getObject(String type, String beginORend, String value){
        Object result = null;
        if("int".equals(type)){
            result = Integer.parseInt(value);
        }else if("bool".equals(type)){
            if("0".equals(value.trim())){
                result = false;
            }else{
                result = true;
            }
        }else if("long".equals(type)){
            result = Long.parseLong(value);
        }else if("short".equals(type)){
            result = Short.parseShort(value);
        }else if("float".equals(type)){
            result = Float.parseFloat(value);
        }else if("date".equals(type)){
            DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            Calendar rightNow = Calendar.getInstance();
            try {
                rightNow.setTime(format.parse(value));
                if("end".equals(beginORend)){
                    rightNow.set(Calendar.DATE, rightNow.get(Calendar.DATE)+1);
                }
                result = rightNow.getTime();
            } catch (ParseException e) {
                //throw new BusinessException("日期类型参数不匹配！");
            }
        }else if("datetime".equals(type)){
            DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Calendar rightNow = Calendar.getInstance();
            try {
                rightNow.setTime(format.parse(value));
                if("end".equals(beginORend)){
                    rightNow.set(Calendar.DATE, rightNow.get(Calendar.DATE)+1);
                }
                result = rightNow.getTime();
            } catch (ParseException e) {
                //throw new BusinessException("日期类型参数不匹配！");
            }
        }else{
            result = value;
        }
        return result;
    }

    /** 从request 中填充filter */
    public void filterQuery(HttpServletRequest request){
        Map<String, String[]> params = request.getParameterMap();
        for(Map.Entry<String, String[]> entry : params.entrySet()){
            Filter filter=null;
            String[] keys = entry.getKey().split("_");
            String[] values = entry.getValue();
            if(null!=values && values.length>0 && StringUtils.isNotBlank(values[0]+"") && keys.length>=2){
                String propertyName = keys[keys.length-1].trim();
                String compare = keys[0].trim().toLowerCase();
                String propertyType = null;
                String beginORend = null;
                if(keys.length>=3) propertyType = keys[1].trim().toLowerCase();
                if(keys.length==4) beginORend = keys[2].trim().toLowerCase();

                if(compare.equals("like")){
                    filter=new Filter(propertyName, Filter.Operator.like,getObject(propertyType, beginORend, values[0]));
                }
                if(compare.equals("eq")){
                    filter=new Filter(propertyName, Filter.Operator.eq,getObject(propertyType, beginORend, values[0]));
                }
                if(compare.equals("ne")){
                    filter=new Filter(propertyName, Filter.Operator.ne,getObject(propertyType, beginORend, values[0]));
                }
                if(compare.equals("gt")){
                    filter=new Filter(propertyName, Filter.Operator.gt,getObject(propertyType, beginORend, values[0]));
                }
                if(compare.equals("lt")){
                    filter=new Filter(propertyName, Filter.Operator.lt,getObject(propertyType, beginORend, values[0]));
                }
                if(compare.equals("ge")){
                    filter=new Filter(propertyName, Filter.Operator.ge,getObject(propertyType, beginORend, values[0]));
                }
                if(compare.equals("le")){
                    filter=new Filter(propertyName, Filter.Operator.le,getObject(propertyType, beginORend, values[0]));
                }
                if(compare.equals("in")){
                    filter=new Filter(propertyName, Filter.Operator.in,getObject(propertyType, beginORend, values[0]));
                }
                if(compare.equals("isNull")){
                    filter=new Filter(propertyName, Filter.Operator.isNull,getObject(propertyType, beginORend, values[0]));
                }
                this.getFilters().add(filter);
                request.setAttribute(entry.getKey().replace(".","_")+"",getObject(propertyType, beginORend, values[0]));

            }
        }

    }

    public void addFilter(Filter filter){
       if(filter==null) return;
       if(filter.getValue()==null)return;
       else  this.getFilters().add(filter);
    }



    /** 排序 SQL*/
    public  LinkedHashMap<String, String> getOrderSQL(){
       return null;
    }



    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(getPageNum()).append(getNumPerPage()).append(getOrderField()).append(getFilters()).append(getOrderSQL()).toHashCode();
    }

}
