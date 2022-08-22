package com.meishe.engine.bean.test;

import java.io.IOException;

/**
 * Created by CaoZhiChao on 2020/7/3 17:27
 */
public class Test1 {
    private static final String TAG = "Test1";

    public static void main(String[] args) throws IOException, CloneNotSupportedException {
        System.out.println("111");
        Object a = 1.0f;
        System.out.println("222: "+ (a instanceof Integer));
        System.out.println("333: "+ (a instanceof Float));
        System.out.println("333: "+ (a instanceof Double));
//        List<User>[] aaa= new ArrayList[10];
//        aaa[1] = new ArrayList<>();
//        System.out.println("1234");
//        User u = new User();
//        u.setId(1);
//        u.setName("name1");
//        u.setAge(18);
//        u.setPpp(new Persion(true));
//        String strnew = new Gson().toJson(u);
//        System.out.println(strnew);
//        String str1 = new GsonBuilder()
//                // 排除@Expose值为false的属性
//                .excludeFieldsWithoutExposeAnnotation()
//                .create().toJson(u);
//        System.out.println(str1);
//        String str = new GsonBuilder()
//                // 排除@Expose值为false的属性
//                .excludeFieldsWithoutExposeAnnotation()
//                .create().toJson(u);
//        System.out.println(str);//{"name":"name","age":18}
//        User fromJson = new GsonBuilder()
//                // 排除@Expose值为false的属性
//                .excludeFieldsWithoutExposeAnnotation()
//                .create()
//                .fromJson(str, User.class);
//        System.out.println(fromJson.toString());//User [id=null, name=null, age=18]
//
//        System.out.println("=============1111=====================");
//        User newUser = (User) u.clone();
//        System.out.println(newUser.toString());

    }
//    private static boolean abc(Number value,Object clazz){
//        return (value instanceof clazz);
//    }

}
