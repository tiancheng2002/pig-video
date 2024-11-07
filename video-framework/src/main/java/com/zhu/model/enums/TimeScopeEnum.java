package com.zhu.model.enums;

import org.springframework.util.ObjectUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum  TimeScopeEnum {

    TIME_ALL(0,0),
    TIME_ONE_DAY(-1,1),
    TIME_SEVEN_DAYS(-7,2),
    TIME_HALF_YEAR(-180,3);

    private final Integer time;

    private final Integer value;

    TimeScopeEnum(Integer time, Integer value) {
        this.time = time;
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
    public static TimeScopeEnum getEnumByValue(Integer value) {
        if (ObjectUtils.isEmpty(value)) {
            return null;
        }
        for (TimeScopeEnum anEnum : TimeScopeEnum.values()) {
            if (anEnum.value==value) {
                return anEnum;
            }
        }
        return null;
    }

    public Integer getValue() {
        return value;
    }

    public Integer getTime() {
        return time;
    }

}
