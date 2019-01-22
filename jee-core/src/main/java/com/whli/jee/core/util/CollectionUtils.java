package com.whli.jee.core.util;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

/**
 * @Desc：<em>自定义Collection集合工具类</em>
 * @Author：whli
 * @Date：2018/5/5 9:56
 */
public class CollectionUtils {

    /**
     * @Desc：<em>判断Collection是否为null或空</em>
     * @Author：whli
     * @Date：2018/5/5
     * @param collection Collection
     * @return boolean
     */
    public static boolean isNullOrEmpty(Collection collection){

        if (collection == null || collection.isEmpty()){
            return true;
        }

        return false;
    }

    /**
     * @Desc：<em>判断Collection是否不为null或空</em>
     * @Author：whli
     * @Date：2018/5/5
     * @param collection Collection
     * @return boolean
     */
    public static boolean isNotNullOrEmpty(Collection collection){

        return !isNullOrEmpty(collection);
    }
}
