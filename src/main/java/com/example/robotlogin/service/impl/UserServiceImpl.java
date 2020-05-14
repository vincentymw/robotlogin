package com.example.robotlogin.service.impl;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.example.robotlogin.beans.User;
import com.example.robotlogin.beans.dto.RegisterDto;
import com.example.robotlogin.mapper.UserMapper;
import com.example.robotlogin.service.UserService;
import com.example.robotlogin.utils.PasswordEncodeUtil;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Override
    public boolean register(RegisterDto registerDto){
        User user = new User();
        user.setUsername(registerDto.getUsername());
        user.setPassword(PasswordEncodeUtil.passwordEncode(registerDto.getPassword()));
        return super.insert(user);
    }
}
