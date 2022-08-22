package com.czc.cutsame.bean;

import java.util.List;

/**
 * author : lhz
 * date   : 2020/11/10
 * desc   :模板分类实体类
 * Templates classify entity classes
 */
public class TemplateCategory {
    public List<Category> categories;

    /**
     * The type Category.
     * 类别类型
     */
    public static class Category {
        private int category;
        private String displayName;

        public int getCategory() {
            return category;
        }

        public void setCategory(int category) {
            this.category = category;
        }

        public String getDisplayName() {
            return displayName;
        }

        public void setDisplayName(String displayName) {
            this.displayName = displayName;
        }
    }
}
