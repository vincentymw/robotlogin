package com.example.robotlogin.utils;

import com.example.robotlogin.beans.User;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.util.Date;
import java.util.HashMap;

public class JwtTokenUtil {
    private static final String SECRET_KEY = "jwtsecretkey";

    private static final long EXPIRATION = 3600L;

    private static byte[] secret_key = SECRET_KEY.getBytes();

    public static String createToken(User user){
        HashMap<String,Object> claims = new HashMap<>();
        claims.put("user",user);
        return Jwts.builder()
                //用HS512算法对JWT进行签名, SECRET_KEY为我们的密钥
                .signWith(SignatureAlgorithm.HS512, SECRET_KEY)
                //签发人
                .setIssuer("ye")
                //设置角色名
                .setClaims(claims)
                //与用户有关信息
                .setSubject(user.getUsername())
                //签发时间
                .setIssuedAt(new Date())
                //设置过期时间
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION * 1000))
                .compact();

        }

    public static String getUsername(String token){
        return parseToken(token).getSubject();
    }

    private static Claims parseToken(String token){
        return Jwts.parser()
                //用约定密钥解密
                .setSigningKey(SECRET_KEY)
                .parseClaimsJws(token)
                .getBody();
    }
}
