package com.whosenet.wb.core.utils;


import com.whosenet.wb.core.entity.BaseEntity;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapperImpl;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.beans.PropertyDescriptor;

/**
 * Utils-Bean.
 *
 * @author <a href="http://ruo.whosenet.com">ruo.whosenet.com</>
 * @version 1.0
 */
public class BeanTool {
	@PersistenceContext protected EntityManager em;
    public static void copyProperties(Object srcBean,Object targtBean){
        BeanUtils.copyProperties(srcBean,targtBean);
    }

    public static void copyPropertiesIgnore(Object srcBean,Object targtBean,String... ignoreProps){
        BeanUtils.copyProperties(srcBean,targtBean,ignoreProps);
    }

    public static void copyPropertiesInclude(Object srcBean,Object targtBean,String... includeProps){
        BeanWrapperImpl target = new BeanWrapperImpl(targtBean);
        BeanWrapperImpl src = new BeanWrapperImpl(srcBean);
        for (String prop : includeProps) {
        	String[] props = StringUtils.split(prop, ".");
        	Object dept = src.getPropertyValue(props[0]);
    		target.setPropertyValue(props[0], dept);	
        	/*if(props.length<=1){
        		Object pv = src.getPropertyValue(prop);
                target.setPropertyValue(prop,pv);
        	}else{
    			Object dept = src.getPropertyValue(props[0]);
        		target.setPropertyValue(props[0], dept);		
        	}*/
        }
    }

    public static void copyNonNullProperties(Object srcBean,Object targtBean){
        BeanWrapperImpl target = new BeanWrapperImpl(targtBean);
        BeanWrapperImpl src = new BeanWrapperImpl(srcBean);
        PropertyDescriptor[] descriptors = src.getPropertyDescriptors();
        for (PropertyDescriptor descriptor : descriptors) {
            if (descriptor.getWriteMethod() != null){
                String name = descriptor.getName();
                Object val = src.getPropertyValue(name);
                if (val!=null) {
                    if(!(val instanceof BaseEntity) || (((BaseEntity) val).getId() != null)){
                        target.setPropertyValue(name,val);
                    }

                }
            }
        }
    }
}
