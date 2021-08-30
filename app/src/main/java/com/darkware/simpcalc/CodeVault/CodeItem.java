package com.darkware.simpcalc.CodeVault;

public class CodeItem {
    private String code;
    private final boolean isActive;

    CodeItem(String code, boolean isActive) {
        this.code = code;
        this.isActive = isActive;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String newCode) {
        code = newCode;
    }

    public boolean isActive() {
        return isActive;
    }
}
