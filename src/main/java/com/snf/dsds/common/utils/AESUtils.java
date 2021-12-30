package com.snf.dsds.common.utils;

import com.alibaba.fastjson.JSON;
import com.snf.dsds.bean.User;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

/**
 * @program: dsds
 * @description:
 * @author: zhouyuj
 * @create: 2021-12-22 18:43
 **/
public class AESUtils {

    private static final String AES_ALGORITHM = "AES/ECB/PKCS5Padding";

    // 获取 cipher
    private static Cipher getCipher(byte[] key, int model) throws Exception {
        SecretKeySpec secretKeySpec = new SecretKeySpec(key, "AES");
        Cipher cipher = Cipher.getInstance(AES_ALGORITHM);
        cipher.init(model, secretKeySpec);
        return cipher;
    }

    // AES加密
    public static String encrypt(byte[] data, byte[] key) throws Exception {
        Cipher cipher = getCipher(key, Cipher.ENCRYPT_MODE);
        return Base64.getEncoder().encodeToString(cipher.doFinal(data));
    }

    // AES解密
    public static byte[] decrypt(byte[] data, byte[] key) throws Exception {
        Cipher cipher = getCipher(key, Cipher.DECRYPT_MODE);
        return cipher.doFinal(Base64.getDecoder().decode(data));
    }

    public static void main(String[] args) throws Exception {
        User user =new User();
        user.setId(1L);
        user.setUsername("zyj");
        user.setPassword("123");
        String userJson = JSON.toJSONString(user);
        System.out.println(userJson);
        byte[] data = userJson.getBytes();
        byte[] key = "aaaaaaaaaaaaaaaa".getBytes();
        System.out.println(encrypt(data,key));
    }

}
