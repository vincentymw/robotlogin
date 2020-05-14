package com.example.robotlogin.service;

import com.baomidou.mybatisplus.service.IService;
import com.example.robotlogin.beans.User;
import com.example.robotlogin.beans.dto.RegisterDto;
import org.springframework.transaction.annotation.Transactional;

@Transactional(rollbackFor = Exception.class)
public interface UserService extends IService<User> {

    boolean register(RegisterDto registerDto);
}
