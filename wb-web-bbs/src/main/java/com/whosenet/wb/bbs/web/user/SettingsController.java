package com.whosenet.wb.bbs.web.user;

import com.whosenet.wb.bbs.web.BaseController;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Created by ruo on 2014/11/26.
 */
@Controller
public class SettingsController extends BaseController {

    /**
     * 用户设置
     */
    @RequestMapping(value = "/settings", method = RequestMethod.GET)
    public String list( ModelMap model) {
        return "user/settings";
    }

}
