package com.maint.system.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.maint.common.util.PageResultBean;
import com.maint.common.util.ResultBean;
import com.maint.system.model.UserOnline;
import com.maint.system.service.UserOnlineService;

import javax.annotation.Resource;
import java.util.List;

@Controller
@RequestMapping("/online")
public class UserOnlineController {

    @Resource
    private UserOnlineService userOnlineService;

    @GetMapping("/index")
    public String index() {
        return "online/user-online-list";
    }

    @GetMapping("/list")
    @ResponseBody
    public PageResultBean<UserOnline> online() {
        List<UserOnline> list = userOnlineService.list();
        return new PageResultBean<>(list.size(), list);
    }

    @PostMapping("/kickout")
    @ResponseBody
    public ResultBean forceLogout(String sessionId) {
        userOnlineService.forceLogout(sessionId);
        return ResultBean.success();
    }
}
