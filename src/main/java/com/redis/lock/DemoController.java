package com.redis.lock;

import com.redis.lock.entity.User;
import com.redis.lock.service.DemoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName DemoController
 * @Desc todo
 * @Author yihec
 * @Date 2020/6/29 0029 10:35
 **/
@Controller
@RequestMapping(value = "/")
public class DemoController {
    @Autowired
    DemoService demoService;

    @RequestMapping(value = "test1", method = RequestMethod.GET)
    @ResponseBody
    public String test1(){
        demoService.updateData();
        return "";
    }

    @RequestMapping(value = "test2", method = RequestMethod.GET)
    @ResponseBody
    public String test2(){
        demoService.updateData2();
        return "";
    }


}
