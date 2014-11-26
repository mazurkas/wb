package com.whosenet.wb.bbs.web.user;

import com.whosenet.wb.bbs.web.BaseController;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;


@Controller
public class ProfileController extends BaseController {

    /**
     * 用户仪表盘
     */
    @RequestMapping(value = "/profile", method = RequestMethod.GET)
    public String list( ModelMap model) {
        return "user/profile";
    }



}
