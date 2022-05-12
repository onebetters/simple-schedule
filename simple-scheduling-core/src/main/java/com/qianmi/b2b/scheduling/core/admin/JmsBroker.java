package com.qianmi.b2b.scheduling.core.admin;

import lombok.Data;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;

/**
 * <p>Date: 2022-03-25 18:02.</p>
 *
 * @author Administrator
 * @version 0.1.0
 */
@Data
public class JmsBroker implements Serializable {
    private static final long serialVersionUID = -6772303702459964349L;

    /**
     * 编码
     */
    @NotNull
    private String key;

    /**
     * url
     */
    @NotNull
    private String url;

    /**
     * 一些factory构建方法
     */
    @NotNull
    private String factory;
}
