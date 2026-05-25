package com.example.check_sec.common.enums;

public enum FilingLevel {
    LEVEL_2("第二级"),
    LEVEL_3("第三级"),
    LEVEL_4("第四级"),
    LEVEL_5("第五级");

    private final String label;

    FilingLevel(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
