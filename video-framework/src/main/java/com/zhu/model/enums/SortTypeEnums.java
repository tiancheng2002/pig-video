package com.zhu.model.enums;

import org.springframework.util.ObjectUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum  SortTypeEnums {

    SORT_COMPREHENSIVE("",0),
    SORT_PUBLISH_TIME("createTime",1),
    SORT_FAVOUR_NUM("favourNum",2);

    private final String text;

    private final Integer value;

    SortTypeEnums(String text, Integer value) {
        this.text = text;
        this.value = value;
    }

    /**
     * 获取值列表
     *
     * @return
     */
    public static List<Integer> getValues() {
        return Arrays.stream(values()).map(item -> item.value).collect(Collectors.toList());
    }

    /**
     * 根据 value 获取枚举
     *
     * @param value
     * @return
     */
    public static SortTypeEnums getEnumByValue(Integer value) {
        if (ObjectUtils.isEmpty(value)) {
            return null;
        }
        for (SortTypeEnums anEnum : SortTypeEnums.values()) {
            if (anEnum.value==value) {
                return anEnum;
            }
        }
        return null;
    }

    public Integer getValue() {
        return value;
    }

    public String getText() {
        return text;
    }


}
