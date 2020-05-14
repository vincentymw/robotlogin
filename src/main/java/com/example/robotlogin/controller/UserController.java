package com.example.robotlogin.controller;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.example.robotlogin.beans.User;
import com.example.robotlogin.beans.dto.ChatDto;
import com.example.robotlogin.beans.dto.RegisterDto;
import com.example.robotlogin.service.UserService;
import com.example.robotlogin.utils.DataValidateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.RestTemplate;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/api")
public class UserController {
    private final UserService userService;

    @Value("${chatBot.url}")
    private String url;

    @Autowired
    public UserController(UserService userService){this.userService=userService;}

    @PostMapping("/auth/register")
    public ResponseEntity<Map<String,Object>> register(@RequestBody @Valid RegisterDto registerDto, Errors error){
        ResponseEntity<Map<String,Object>> responseEntity = DataValidateUtil.validate(error);
        if(responseEntity!=null){return responseEntity;}

        HashMap<String,Object> map = new HashMap<>(4);
        int i = userService.selectCount(new EntityWrapper<User>().eq("username",registerDto.getUsername()));
        if(i>0){
            map.put("message","用户名已存在");
            return new ResponseEntity<>(map, HttpStatus.BAD_REQUEST);
        }

        boolean register = userService.register(registerDto);

        if(register){
            map.put("message","注册成功");
            return new ResponseEntity<>(map,HttpStatus.OK);
        }else{
            map.put("message","注册失败");
            return new ResponseEntity<>(map,HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/chat")
    public ResponseEntity<String> chat(@RequestBody ChatDto chatDto){

        RestTemplate client = new RestTemplate();
        //新建Http头，add方法可以添加参数
        HttpHeaders headers = new HttpHeaders();
        //设置请求发送方式
        HttpMethod method = HttpMethod.POST;
        // 以json的方式提交
        headers.setContentType(MediaType.APPLICATION_JSON);
        //将请求头部和参数合成一个请求
        Map<String,String> map = new HashMap<>();
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        map.put("sender",auth.getName());
        map.put("message",chatDto.getMessage());
        HttpEntity<Map<String, String>> requestEntity = new HttpEntity<>(map, headers);
        //执行HTTP请求，将返回的结构使用String 类格式化（可设置为对应返回值格式的类）
        ResponseEntity<String> response = client.exchange(url, method, requestEntity,String.class);
        return response;
//        JSONObject responseEntity = JSON.parseObject(response.getBody());
//        Map<String,Object> responseMap = responseEntity.getInnerMap();
//        return new ResponseEntity<JSONArray>(response.getBody(),HttpStatus.OK);
    }

    @GetMapping("/auth/info")
    public ResponseEntity<Map<String, Object>> Info() {
        Map<String, Object> map = new HashMap<>(4);
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        map.put("status", 200);
        map.put("username", username);
        return new ResponseEntity<>(map, HttpStatus.OK);
    }
}
