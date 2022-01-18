package com.snf.dsds.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.snf.dsds.common.config.EncryptProperties;
import com.snf.dsds.common.utils.AESUtils;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import sun.security.krb5.internal.PAData;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @program: dsds
 * @description: 登录过滤器
 * @author: zhouyuj
 * @create: 2021-12-21 14:22
 **/
@Slf4j
public class LoginFilter extends UsernamePasswordAuthenticationFilter {

    @SneakyThrows
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException{
        log.info("进入登录过滤器，处理登录逻辑");
        if (!request.getMethod().equals("POST")) {
            throw new AuthenticationServiceException(
                    "Authentication method not supported: " + request.getMethod());
        }
        String origin = request.getHeader("Origin");
        if(StringUtils.isEmpty(origin)){
            origin = request.getHeader("Referer");
        }
        response.setHeader("Access-Control-Allow-Origin", origin);
        response.setHeader("Access-Control-Allow-Credentials", "true");
        if(StringUtils.equals(request.getContentType(),MediaType.APPLICATION_JSON_VALUE) ||
                StringUtils.equals(request.getContentType(),MediaType.APPLICATION_JSON_UTF8_VALUE)){
            log.info("开始json数据登录流程");
            Map<String, String> loginData = new HashMap<>();
            try {
                loginData = new ObjectMapper().readValue(request.getInputStream(), Map.class);
            } catch (IOException e) {
                log.error("获取登录json数据出现错误");
            }
            log.info("获取到的请求参数为【{}】",new ObjectMapper().writeValueAsString(loginData));
            String username = loginData.get(getUsernameParameter());
            String password = loginData.get(getPasswordParameter());
            if (username == null) {
                username = "";
            }
            if (password == null) {
                password = "";
            }
//            password = AESUtils.aesDecrypt(password, EncryptProperties.getKey());
            username = username.trim();
            UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(username, password);
            setDetails(request, authRequest);
            return this.getAuthenticationManager().authenticate(authRequest);
        }else{
            log.info("开始表单数据登录流程");
            return super.attemptAuthentication(request,response);
        }
    }
}
