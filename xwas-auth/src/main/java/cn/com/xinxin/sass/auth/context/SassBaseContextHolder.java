package cn.com.xinxin.sass.auth.context;

/*
 *
 * Copyright 2020 www.xinxindigits.com
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software
 * and associated documentation files (the "Software"),to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so,
 * subject to the following conditions:
 *
 * The above copyright notice and this permission notice
 * shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A
 * PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 * HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 * Redistribution and selling copies of the software are prohibited, only if the authorization from xinxin digits
 * was obtained.Neither the name of the xinxin digits; nor the names of its contributors may be used to
 * endorse or promote products derived from this software without specific prior written permission.
 *
 */

import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * @author: zhouyang
 * @created: 09/05/2020.
 * @updater:
 * @description:
 */
public class SassBaseContextHolder {

    private static final String JWT_KEY_USER_ID = "JWT_KEY_USER_ID";

    private static final String JWT_KEY_ACCOUNT = "JWT_KEY_ACCOUNT";

    private static final String JWT_KEY_TENANT = "JWT_KEY_TENANT";


    private static final ThreadLocal<Map<String, String>> THREAD_LOCAL = new ThreadLocal<>();

    public static void set(String key, Object value) {
        Map<String, String> map = getLocalMap();
        map.put(key, value == null ? StringUtils.EMPTY : value.toString());
    }

    public static <T> T get(String key, Class<T> type) {
        Map<String, String> map = getLocalMap();
        return (T)map.get(key);
    }

    public static <T> T get(String key, Class<T> type, Object def) {
        Map<String, String> map = getLocalMap();
        return (T) map.getOrDefault(key, String.valueOf(def == null ? "" : def));
    }

    public static String get(String key) {
        Map<String, String> map = getLocalMap();
        return map.getOrDefault(key, "");
    }

    public static Map<String, String> getLocalMap() {
        Map<String, String> map = THREAD_LOCAL.get();
        if (map == null) {
            map = new HashMap<>(10);
            THREAD_LOCAL.set(map);
        }
        return map;
    }

    public static void setLocalMap(Map<String, String> threadLocalMap) {
        THREAD_LOCAL.set(threadLocalMap);
    }


    /**
     * 账号id
     *
     * @return
     */
    public static Long getUserId() {
        return get(JWT_KEY_USER_ID, Long.class, 0L);
    }

    public static String getUserIdStr() {
        return String.valueOf(getUserId());
    }

    /**
     * 账号id
     *
     * @param userId
     */
    public static void setUserId(Long userId) {
        set(JWT_KEY_USER_ID, userId);
    }


    /**
     * 账号表中的account
     *
     * @return
     */
    public static String getAccount() {
        return get(JWT_KEY_ACCOUNT, String.class);
    }

    /**
     * 账号表中的account
     *
     * @param account
     */
    public static void setAccount(String account) {
        set(JWT_KEY_ACCOUNT, account);
    }


    public static String getTenantId() {
        return get(JWT_KEY_TENANT, String.class);
    }

    public static void setTenantId(String val) {
        set(JWT_KEY_TENANT, val);
    }


    public static void remove() {
        if (THREAD_LOCAL != null) {
            THREAD_LOCAL.remove();
        }
    }


}
