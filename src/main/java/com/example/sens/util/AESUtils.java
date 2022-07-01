package com.example.sens.util;

import sun.misc.BASE64Decoder;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;


/**
 * @author niunafei
 * @function
 * @email niunafei0315@163.com
 * @date 2018/12/12  下午2:32
 */
public class AESUtils
{

    private static final String AES = "AES";
    private static final String CHAR_SET_NAME1 = "UTF-8";
    private static final String CHAR_SET_NAME2 = "ASCII";
    private static final String CIPHER_KEY = "AES/CBC/PKCS5Padding";
    private static final String IV_PARAMETER = "a0.l954b_107x90l";
    private static final String S_KEY = "ax7x90.3k_10li5u";
    public static String decrypt(String value) throws Exception
    {
        SecretKeySpec skeySpec = new SecretKeySpec(S_KEY.getBytes(CHAR_SET_NAME2), AES);
        Cipher cipher = Cipher.getInstance(CIPHER_KEY);
        IvParameterSpec iv = new IvParameterSpec(IV_PARAMETER.getBytes());
        cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);
        // 先用base64解密
        return new String(cipher.doFinal(new BASE64Decoder().decodeBuffer(value)), CHAR_SET_NAME1);
    }

}