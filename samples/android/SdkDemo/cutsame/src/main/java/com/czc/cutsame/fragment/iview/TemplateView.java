package com.czc.cutsame.fragment.iview;

import com.czc.cutsame.bean.Template;
import com.czc.cutsame.bean.TemplateCategory;
import com.meishe.base.model.IBaseView;

import java.util.List;

/**
 * author : lhz
 * date   : 2020/11/3
 * desc   :模板列表view
 * Template list View
 */
public interface TemplateView extends IBaseView {
    void onTemplateCategoryBack(List<TemplateCategory.Category> categoryList);

    void onTemplateListBack(List<Template> templateList);

    void onMoreTemplateBack(List<Template> templateList);

    void onDownloadTemplateSuccess(String templatePath, boolean isTemplate);
}
