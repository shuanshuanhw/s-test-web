package com.example.stestweb;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.*;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @Author qt
 * @Date 2021/3/24
 * @Description 登录失败处理
 */
@Component("loginFailureHandler")
class LoginFailureHandler extends SimpleUrlAuthenticationFailureHandler {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    @Resource
    private ObjectMapper objectMapper;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        logger.info("登录失败");
        response.setContentType("application/json;charset=UTF-8");
        //这里写你登录失败后的逻辑，可加验证码验证等
        String errorInfo = "";
        if (exception instanceof BadCredentialsException ||
                exception instanceof UsernameNotFoundException) {
            errorInfo = "账户名或者密码输入错误!";
        } else if (exception instanceof LockedException) {
            errorInfo = "账户被锁定，请联系管理员!";
        } else if (exception instanceof CredentialsExpiredException) {
            errorInfo = "密码过期，请联系管理员!";
        } else if (exception instanceof AccountExpiredException) {
            errorInfo = "账户过期，请联系管理员!";
        } else if (exception instanceof DisabledException) {
            errorInfo = "账户被禁用，请联系管理员!";
        } else {
            errorInfo = "登录失败!";
        }
        logger.info("登录失败原因：" + errorInfo);
        //ajax请求认证方式
        JSONObject resultObj = new JSONObject();
        resultObj.put("code", HttpStatus.UNAUTHORIZED.value());
        resultObj.put("msg",errorInfo);
        resultObj.put("exception",objectMapper.writeValueAsString(exception));
        response.getWriter().write(resultObj.toString());

        //表单认证方式
        //this.saveException(request, exception);
        //this.getRedirectStrategy().sendRedirect(request, response, "/login?error=true");
    }
}