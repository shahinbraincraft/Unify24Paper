package com.meishe.modulemakeupcompose.makeup;

import android.content.Context;

public interface BeautyData {
    String getName(Context context);

    Object getImageResource();

    void setFolderPath(String folderPath);

    String getFolderPath();

    boolean isBuildIn();

    void setIsBuildIn(boolean isBuildIn);

    int getBackgroundColor();
}
