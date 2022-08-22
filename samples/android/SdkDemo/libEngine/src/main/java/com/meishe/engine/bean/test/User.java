package com.meishe.engine.bean.test;

import androidx.annotation.NonNull;

import com.google.gson.annotations.Expose;

/**
 * Created by CaoZhiChao on 2020/7/3 17:28
 */
public class User implements Cloneable {

    @Expose(serialize = false, deserialize = true)
    private Integer id;
    //    @SerializedName(value = "name", alternate = {"userName"})
    @Expose(serialize = true, deserialize = false)
    private String name;
    @Expose
//    @SerializedName(value = "age", alternate = {"Age", "a"})
    private Integer age;
    @Expose(serialize = true, deserialize = true)
    private Persion ppp;

    @NonNull
    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public Persion getPpp() {
        return ppp;
    }

    public void setPpp(Persion ppp) {
        this.ppp = ppp;
    }

    @Override
    public String toString() {
        return "User [id=" + id + ", name=" + name + ", age=" + age + " , ppp= " + ppp.toString() + "]";
    }

}
