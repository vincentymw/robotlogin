package com.example.robotlogin.config;

import com.example.robotlogin.config.handler.CustomAuthenticationFailHandler;
import com.example.robotlogin.config.handler.CustomAuthenticationSuccessHandler;
import com.example.robotlogin.config.handler.CustomLogoutSuccessHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

@EnableWebSecurity
public class SpringSecurityConfig extends WebSecurityConfigurerAdapter {

    private static final String LOGIN_URL = "/api/auth/login";

    private static final String JWT_LOGIN = "/api/auth/jwtLogin";

    private final CustomAuthenticationSuccessHandler authenticationSuccessHandler;

    private final CustomAuthenticationFailHandler authenticationFailHandler;

    private final CustomLogoutSuccessHandler customLogoutSuccessHandler;

    private final CustomUserDetailsServiceImpl userDetailsService;

    @Autowired
    public SpringSecurityConfig(CustomAuthenticationSuccessHandler authenticationSuccessHandler,
                                CustomUserDetailsServiceImpl userDetailsService,
                                CustomAuthenticationFailHandler authenticationFailHandler,
                                CustomLogoutSuccessHandler customLogoutSuccessHandler) {
        this.authenticationSuccessHandler = authenticationSuccessHandler;
        this.authenticationFailHandler = authenticationFailHandler;
        this.customLogoutSuccessHandler = customLogoutSuccessHandler;
        this.userDetailsService = userDetailsService;
    }

    /**
     * 重用父类的AuthenticationManager
     */
    @Override

    @Bean
    public AuthenticationManager authenticationManagerBean() {
        try {
            return super.authenticationManagerBean();
        } catch (Exception e) {
            System.out.println("wrong");
        }
        return null;
    }


    /**
     * 注入自定义UsernamePasswordAuthenticationFilter过滤器
     *
     * @return Bean
     */
    @Bean
    public CustomUsernamePasswordAuthenticationFilter customUsernamePasswordAuthenticationFilter() {
        CustomUsernamePasswordAuthenticationFilter filter = new CustomUsernamePasswordAuthenticationFilter();
        //重用WebSecurityConfigurerAdapter配置的AuthenticationManager，重点！！！！
        filter.setAuthenticationManager(authenticationManagerBean());
        //设置拦截路径为登录路径
        filter.setFilterProcessesUrl(JWT_LOGIN);
        //设置认证完成后的处理器
        filter.setAuthenticationSuccessHandler(authenticationSuccessHandler);
        filter.setAuthenticationFailureHandler(authenticationFailHandler);

        return filter;
    }

    /**
     * 注入自定义的jwt校验用户过滤器
     *
     * @return
     */
    @Bean
    public JwtPreAuthFilter jwtPreAuthFilter() {
        JwtPreAuthFilter filter = new JwtPreAuthFilter(authenticationManagerBean());
        return filter;
    }

    /**
     * 自定义用户信息获取来源和加密算法
     */
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
    }


    /**
     * 选择加密算法（BCryptPasswordEncoder BCrypt）并注入IOC容器
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Override
    public void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers("/api/auth/register")
                .permitAll();

        http.csrf().disable();

        //登录认证功能
        http.authorizeRequests()
                .and()
                .formLogin()    //表单登录配置
                //登录认证处理路径
                .loginProcessingUrl(JWT_LOGIN)
                .successHandler(authenticationSuccessHandler)
                .failureHandler(authenticationFailHandler)
                .and()
                .logout()
                .logoutUrl("/api/auth/logout")
                .logoutSuccessHandler(customLogoutSuccessHandler)
                .and()
                //先用于login校验
                .addFilter(customUsernamePasswordAuthenticationFilter())
                //再对其他请求过滤
                .addFilter(jwtPreAuthFilter());



        //开启授权认证
        http.authorizeRequests()
                .and()
                .authorizeRequests()
                .anyRequest().authenticated();


        //覆盖UsernamePasswordAuthenticationFilter过滤器对ajax登录和表单登录分别处理
//        http.addFilterAt(customUsernamePasswordAuthenticationFilter(),
//                UsernamePasswordAuthenticationFilter.class);
    }
}
