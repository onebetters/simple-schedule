package com.qianmi.b2b.scheduling.acceptor.common.rsp;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

/**
 * <p>Date: 2022-04-06 17:00.</p>
 *
 * @author Administrator
 * @version 0.1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskResetResponse implements Serializable {
    private static final long serialVersionUID = 1957074970652433719L;
    private static final TaskResetResponse EMPTY = new TaskResetResponse(Collections.emptyList(),
            Collections.emptyList()
    );

    private List<String> cancelIds;

    private List<String> submitIds;

    public static TaskResetResponse empty() {
        return EMPTY;
    }
}
