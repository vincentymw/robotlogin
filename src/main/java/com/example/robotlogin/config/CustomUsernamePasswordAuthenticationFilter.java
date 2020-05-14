package com.example.robotlogin.config;

import com.alibaba.fastjson.JSON;
import com.example.robotlogin.beans.dto.LoginDto;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


public class CustomUsernamePasswordAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        if (MediaType.APPLICATION_JSON_VALUE.equals(request.getContentType())
                || MediaType.APPLICATION_JSON_UTF8_VALUE.equals((request.getContentType()))) {
            LoginDto loginDto;
            try {
                //获取输入流
                ServletInputStream inputStream = request.getInputStream();
                loginDto = JSON.parseObject(inputStream, LoginDto.class);
            } catch (IOException e) {
                loginDto = new LoginDto("", "");
            }
            UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(
                    loginDto.getUsername(), loginDto.getPassword()
            );
            // 继承自父类的处理
            setDetails(request, authRequest);
            return this.getAuthenticationManager().authenticate(authRequest);
        } else {
            //表单处理
            return super.attemptAuthentication(request, response);
        }
    }
}
