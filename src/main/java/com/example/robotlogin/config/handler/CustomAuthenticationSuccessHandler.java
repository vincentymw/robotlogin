package com.example.robotlogin.config.handler;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.example.robotlogin.beans.User;
import com.example.robotlogin.service.UserService;
import com.example.robotlogin.utils.JwtTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {
    private UserService userService;

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
            throws IOException {

        User user = userService.selectOne(new EntityWrapper<User>().eq("username", authentication.getName()));
        user.setPassword(null);
        String token = JwtTokenUtil.createToken(user);
        response.addHeader("token", "token");
        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_OK);
        Map<String, Object> map = new HashMap<>(4);
        map.put("status", 200);
        map.put("message", "登录成功");
        map.put("user", user);
        map.put("token", token);
        //返回json提示信息
        response.getWriter().write(JSON.toJSONString(map));
        response.getWriter().flush();
    }
}
