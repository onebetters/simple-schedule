package com.qianmi.b2b.scheduling.core.extra;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.io.Serializable;

/**
 * <p>Date: 2022-03-25 11:21.</p>
 *
 * @author Administrator
 * @version 0.1.0
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.MINIMAL_CLASS)
public interface TaskExtra extends Serializable {
}
