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
 * 创建日期：16/9/11
 * 描    述：
 * 修订历史：
 * ================================================
 * 存储异常类
 * Storage exception class
 */
public class StorageException extends Exception {

    private static final long serialVersionUID = 178946465L;

    public StorageException() {
    }

    public StorageException(String detailMessage) {
        super(detailMessage);
    }

    public StorageException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }

    public StorageException(Throwable throwable) {
        super(throwable);
    }

    /**
     * Not available storage exception.
     * 不可用的存储异常
     * @return the storage exception
     */
    public static StorageException NOT_AVAILABLE() {
        return new StorageException("SDCard isn't available, please check SD card and permission: WRITE_EXTERNAL_STORAGE, and you must pay attention to Android6.0 RunTime Permissions!");
    }
}
