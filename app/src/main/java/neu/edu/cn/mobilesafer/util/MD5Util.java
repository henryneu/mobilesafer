package neu.edu.cn.mobilesafer.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by neuHenry on 2017/8/18.
 */

public class MD5Util {

    /**
     * 对指定的字符串进行MD5加密处理
     * @param password   待加密的原始密码值
     * @return MD5加盐加密后的密码值
     */
    public static String encodePassword(String password) {
        try {
            // 密码加盐处理
            password = password + "mobilesafer";
            // 获取MessageDigest实例，并指定加密算法类型
            MessageDigest digest = MessageDigest.getInstance("MD5");
            // 将需要加密的字符串转换成byte数组后进行随机哈希过程
            byte[] bs = digest.digest(password.getBytes());
            StringBuffer buffer = new StringBuffer();
            // 循环遍历byte类型数组，让其生成32位字符串
            for (byte b : bs) {
                int i = b & 0xff;
                String str = Integer.toHexString(i);
                if (str.length() < 2) {
                    str = "0" + str;
                }
                buffer.append(str);
            }
            return buffer.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }
}
