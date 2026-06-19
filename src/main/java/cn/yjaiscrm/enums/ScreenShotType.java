package cn.iyque.enums;

import cn.iyque.domain.SubType;

import java.util.ArrayList;
import java.util.List;

public enum ScreenShotType {
    CHAT(1,"聊天"),
    CONTACTS(2,"通讯录"),
    EMAIL(3,"邮件"),
    FILE(4,"文件"),
    SCHEDULE(5,"日程"),
    OTHER(6,"其他");
    private final int code;
    private final String description;

    ScreenShotType(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public int getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }


    public static List<SubType> getSubTypeListFromEnum() {
        List<SubType> subTypeList = new ArrayList<>();
        // 遍历枚举常量数组
        for (ScreenShotType operation : ScreenShotType.values()) {
            // 将枚举的code和description映射到SubType的subCode和value
            SubType subType = SubType.builder()
                    .subCode(operation.getCode())
                    .value(operation.getDescription())
                    .build();
            subTypeList.add(subType);
        }
        return subTypeList;
    }
}
