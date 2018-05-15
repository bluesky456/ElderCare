package com.ovvi.remotelocation.utils;

import org.jasypt.encryption.pbe.StandardPBEByteEncryptor;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.jasypt.encryption.pbe.config.EnvironmentStringPBEConfig;

import android.text.TextUtils;

/**
 * 
 * @description
 * @author liuyunlong
 * @date 2017年3月13日上午11:54:05
 * 
 */
public class EncryptorUtil {

    private EncryptorUtil() {
    }

    private static final StandardPBEStringEncryptor encryptor;

    static {
        encryptor = new StandardPBEStringEncryptor();
        EnvironmentStringPBEConfig config = new EnvironmentStringPBEConfig();
        config.setAlgorithm(StandardPBEByteEncryptor.DEFAULT_ALGORITHM);
        config.setPassword("ISDGJudd09346GNQPU=836");
        encryptor.setConfig(config);
    }

    public static final String encrypt(String str) {
        if (TextUtils.isEmpty(str)) {
            return "";
        }
        return null2EmptyString(encryptor.encrypt(str));
    }

    /**
     * @param encrypt
     * @return
     * @author liuyunlong
     * @version 2017年3月13日上午11:49:08
     */
    private static String null2EmptyString(String encrypt) {
        String s = encrypt;
        if (TextUtils.isEmpty(encrypt)) {
            s = "";
        }
        return s.trim();
    }

    public static final String decrypt(String str) {
        return null2EmptyString(encryptor.decrypt(str));
    }

    public static void main(String[] args) {
        String encryptRes = encrypt("root");
        System.out.println("encrypt result: " + encryptRes);
        System.out.println(decrypt(encryptRes));
    }
}
