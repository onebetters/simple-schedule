package com.qianmi.b2b.scheduling.acceptor.common.req;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.Optional;

/**
 * <p>Date: 2022-04-21 10:11.</p>
 *
 * @author Administrator
 * @version 0.1.0
 */
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum TaskAcceptType {

    SUBMIT("提交任务"), CANCEL("取消任务"), RESET("重置任务延时时间"), REBUILD("销毁重建");

    /**
     * 备注描述信息，给开发运维内部看的（即使没有源代码）
     */
    private final String desc;

    /**
     * 根据字符串名称，转枚举值
     *
     * @param name 任务类型，不区分大小写
     * @return 匹配到的枚举值
     */
    public static Optional<TaskAcceptType> of(String name) {
        if (StringUtils.isBlank(name)) {
            return Optional.empty();
        }
        return Arrays.stream(values()).filter(e -> StringUtils.equalsIgnoreCase(e.name(), name)).findFirst();
    }
}
