package com.whosenet.wb.portal.web;

import com.whosenet.wb.core.utils.SpringUtils;
import com.whosenet.wb.core.web.Message;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.annotation.Resource;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.util.Set;

/**
 * Utils-后台公用Controller.
 *
 * @author <a href="http://ruo.whosenet.com">ruo.whosenet.com</>
 * @version 1.0
 */
public class BaseController {


    /** 错误视图 */
    protected static final String ERROR_VIEW = "/admin/common/error";

    /** 错误消息 */
    protected static Message ERROR_MESSAGE = Message.error("admin.message.error");

    /** 成功消息 */
    protected static final Message SUCCESS_MESSAGE = Message.success("admin.message.success");

    /** "验证结果"参数名称 */
    private static final String CONSTRAINT_VIOLATIONS_ATTRIBUTE_NAME = "constraintViolations";

    @Resource(name = "validator")
    private Validator validator;

    /**
     * 数据绑定
     *
     * @param binder
     *            WebDataBinder
     */
    @InitBinder
    protected void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(String.class, new StringTrimmerEditor(true));
       // binder.registerCustomEditor(Date.class, new DateEditor(true));
    }

    /**
     * 数据验证
     *
     * @param target
     *            验证对象
     * @param groups
     *            验证组
     * @return 验证结果
     */
    protected boolean isValid(Object target, Class<?>... groups) {
        Set<ConstraintViolation<Object>> constraintViolations = validator.validate(target, groups);
        if (constraintViolations.isEmpty()) {
            return true;
        } else {
            RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
            requestAttributes.setAttribute(CONSTRAINT_VIOLATIONS_ATTRIBUTE_NAME, constraintViolations, RequestAttributes.SCOPE_REQUEST);
            //-Start By Mei<Json返回方式同样添加校验信息>
            String str="";
            for(ConstraintViolation cv:constraintViolations){
                      str=str+cv.getPropertyPath()+":"+cv.getMessage()+",";
            }
            //-end by Mei
            ERROR_MESSAGE=Message.error(str);
            return false;
        }
    }

    /**
     * 数据验证
     *
     * @param type 类型
     * @param property 属性
     * @param value 值
     * @param groups 验证组
     * @return 验证结果
     */
    protected boolean isValid(Class<?> type, String property, Object value, Class<?>... groups) {
        Set<?> constraintViolations = validator.validateValue(type, property, value, groups);
        if (constraintViolations.isEmpty()) {
            return true;
        } else {
            RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
            requestAttributes.setAttribute(CONSTRAINT_VIOLATIONS_ATTRIBUTE_NAME, constraintViolations, RequestAttributes.SCOPE_REQUEST);
            return false;
        }
    }


    /**
     * 获取国际化消息
     *
     * @param code 代码
     * @param args 参数
     * @return 国际化消息
     */
    protected String message(String code, Object... args) {
        return SpringUtils.getMessage(code, args);
    }

    /**
     * 添加瞬时消息
     *
     * @param redirectAttributes RedirectAttributes
     * @param message 消息
     */
    protected void addFlashMessage(RedirectAttributes redirectAttributes, Message message) {
        if (redirectAttributes != null && message != null) {
           // redirectAttributes.addFlashAttribute(FlashMessageDirective.FLASH_MESSAGE_ATTRIBUTE_NAME, message);
        }
    }


}
