package com.meishe.engine.bean.test;

import androidx.annotation.NonNull;

import com.google.gson.annotations.Expose;

/**
 * Created by CaoZhiChao on 2020/7/3 20:43
 */
public class Persion {
    @Expose
    private boolean abcd = false;

    public Persion(boolean abcd) {
        this.abcd = abcd;
    }

    @NonNull
    @Override
    public String toString() {
        return "Persion [abcd=" + abcd + "]";
    }
}
