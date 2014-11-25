package com.whosenet.wb.bbs.web;


import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/")
public class IndexController extends BaseController {


    /**
     * 列表
     */
    @RequestMapping(value = "/index", method = RequestMethod.GET)
    public String list( ModelMap model) {
        return "/index";
    }

}
