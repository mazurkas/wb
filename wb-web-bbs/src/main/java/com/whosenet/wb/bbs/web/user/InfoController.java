package com.whosenet.wb.bbs.web.user;

import com.whosenet.wb.bbs.web.BaseController;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Created by ruo on 2014/11/26.
 */
@Controller
public class InfoController  extends BaseController{

    /**
     * 用户首页,其他用户可见
     */
    @RequestMapping(value = "/user/{username}", method = RequestMethod.GET)
    public String list(@PathVariable String username, ModelMap model) {
        return "user/info";
    }

}
