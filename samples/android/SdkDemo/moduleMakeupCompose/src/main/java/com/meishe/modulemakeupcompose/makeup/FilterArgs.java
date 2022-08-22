package com.meishe.modulemakeupcompose.makeup;

public class FilterArgs {
    private int isBuiltIn;
    private String packageId;
    private int canReplace;
    private String uuid;
    private String value;

    public int getIsBuiltIn() {
        return isBuiltIn;
    }

    public void setIsBuiltIn(int isBuiltIn) {
        this.isBuiltIn = isBuiltIn;
    }

    public String getPackageId() {
        return packageId;
    }

    public void setPackageId(String packageId) {
        this.packageId = packageId;
    }

    public int getCanReplace() {
        return canReplace;
    }

    public void setCanReplace(int canReplace) {
        this.canReplace = canReplace;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public boolean isCanReplace() {
        return getCanReplace() == 1;
    }
}