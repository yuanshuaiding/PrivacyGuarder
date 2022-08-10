package com.eric.manager.privacy.app.aop;

import java.util.HashMap;
import java.util.Map;

/**
 * @Description: 统计方法耗时AOP，用于asm插入到注解的方法开头与结尾
 * @Author: Eric
 * @Email: yuanshuaiding@163.com
 * @CreateDate: 2022/8/1 11:21
 * @Version: 1.0
 */
public class AOP4MethodCost {
    public static Map<String, Long> mStartTimes = new HashMap<>();

    public static Map<String, Long> mEndTimes = new HashMap<>();

    public static void putStartTime(String methodName, String className) {
        mStartTimes.put(methodName + "," + className, System.currentTimeMillis());
    }

    public static void putEndTime(String methodName, String className) {
        mEndTimes.put(methodName + "," + className, System.currentTimeMillis());
        printlnTime(methodName, className);
    }

    public static void printlnTime(String methodName, String className) {
        String key = methodName + "," + className;
        if (!mStartTimes.containsKey(key) || !mEndTimes.containsKey(key)) {
            System.out.println("className =" + key + "not exist");
        }
        long currTime = mEndTimes.get(key) - mStartTimes.get(key);
        System.out.println("className =" + className + " methodName =" + methodName + "，time consuming " + currTime + "  ms");
    }
}
