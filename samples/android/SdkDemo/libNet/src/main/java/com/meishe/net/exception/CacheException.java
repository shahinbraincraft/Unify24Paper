/*
 * Copyright 2016 jeasonlzy.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.meishe.net.exception;

/**
 * ================================================
 * 作    者：jeasonlzy.Github地址：https://github.com/jeasonlzy
 * 版    本：1.0
 * 创建日期：16/8/28
 * 描    述：
 * 修订历史：
 * ================================================
 * 缓存异常类
 * Cache exception class
 */
public class CacheException extends Exception {
    private static final long serialVersionUID = 845628123701073013L;

    /**
     * Non or expire cache exception.
     * 非或过期的缓存异常
     * @param cacheKey the cache key 缓存键
     * @return the cache exception 缓存异常
     */
    public static CacheException NON_OR_EXPIRE(String cacheKey) {
        return new CacheException("cacheKey = " + cacheKey + " ,can't find cache by cacheKey, or cache has expired!");
    }

    /**
     * Non and 304 cache exception.
     * Non和304 cache异常。
     * @param cacheKey the cache key 缓存键
     * @return the cache exception 缓存异常
     */
    public static CacheException NON_AND_304(String cacheKey) {
        return new CacheException("the http response code is 304, but the cache with cacheKey = " + cacheKey + " is null or expired!");
    }

    public CacheException(String detailMessage) {
        super(detailMessage);
    }
}
