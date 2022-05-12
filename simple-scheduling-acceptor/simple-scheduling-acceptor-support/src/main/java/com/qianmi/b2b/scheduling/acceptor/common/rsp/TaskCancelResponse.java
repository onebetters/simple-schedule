package com.qianmi.b2b.scheduling.acceptor.common.rsp;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * <p>Date: 2022-04-06 16:59.</p>
 *
 * @author Administrator
 * @version 0.1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskCancelResponse implements Serializable {
    private static final long serialVersionUID = 4102974666357763364L;

    private List<String> ids;
}
