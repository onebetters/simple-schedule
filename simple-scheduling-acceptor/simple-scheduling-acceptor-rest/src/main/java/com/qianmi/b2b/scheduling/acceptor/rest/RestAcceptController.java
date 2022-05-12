package com.qianmi.b2b.scheduling.acceptor.rest;

import com.qianmi.b2b.scheduling.acceptor.common.req.TaskCancelRequest;
import com.qianmi.b2b.scheduling.acceptor.common.req.TaskResetRequest;
import com.qianmi.b2b.scheduling.acceptor.common.req.TaskSubmitRequest;
import com.qianmi.b2b.scheduling.acceptor.common.rsp.TaskCancelResponse;
import com.qianmi.b2b.scheduling.acceptor.common.rsp.TaskResetResponse;
import com.qianmi.b2b.scheduling.core.TaskRemoveType;
import com.qianmi.b2b.scheduling.core.TaskElement;
import com.qianmi.b2b.scheduling.core.TaskRepository;
import com.qianmi.b2b.scheduling.core.TaskSchedule;
import com.qianmi.b2b.scheduling.core.schedule.TaskScheduleSelector;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * <p>Date: 2022-03-25 14:49.</p>
 *
 * @author Administrator
 * @version 0.1.0
 */
@Slf4j
@RequiredArgsConstructor

@RestController
@RequestMapping("api/schedule/task")
public class RestAcceptController {

    private final TaskRepository taskRepository;
    private final TaskScheduleSelector taskScheduleSelector;

    @PostMapping
    public TaskElement submit(@RequestBody @Valid TaskSubmitRequest request) {
        final TaskElement task = request.toTask();
        final TaskSchedule schedule = taskScheduleSelector.getInstance(task.getTemplate());
        return schedule.submit(task);
    }

    @DeleteMapping
    public TaskCancelResponse cancel(@RequestBody @Valid TaskCancelRequest request) {
        final TaskSchedule schedule = taskScheduleSelector.getInstance(request.getTemplate());
        final Stream<String> taskIds = taskRepository.findByIndex(schedule.getName(),
                request.getTemplate(),
                request.getIndex()
        ).map(TaskElement::getId);
        final List<String> cancelIds = taskIds.map(taskId -> schedule.cancel(taskId, TaskRemoveType.CANCELED))
                .map(op -> op.orElse(null))
                .filter(Objects::nonNull)
                .map(TaskElement::getId)
                .collect(Collectors.toList());
        return new TaskCancelResponse(cancelIds);
    }

    @PatchMapping
    public TaskResetResponse reset(@RequestBody @Valid TaskResetRequest request) {
        if (!request.shouldReset()) {
            log.debug("入参信息不需要重置任务，比如时间变更为0。taskReq={}", request);
            return TaskResetResponse.empty();
        }

        final TaskSchedule schedule = taskScheduleSelector.getInstance(request.getTemplate());
        final Stream<String> taskIds = taskRepository.findByIndex(schedule.getName(),
                request.getTemplate(),
                request.getIndex()
        ).map(TaskElement::getId);
        final List<String> cancelIds = new LinkedList<>();
        final List<String> submitIds = new LinkedList<>();
        taskIds.forEach(taskId -> schedule.cancel(taskId, TaskRemoveType.CANCELED_BY_RESET).ifPresent(task -> {
            cancelIds.add(task.getId());

            final TaskElement newTask = task.resetDelayMs(request.getDelayMsDiff());
            schedule.submit(newTask);

            submitIds.add(newTask.getId());
        }));

        return new TaskResetResponse(cancelIds, submitIds);
    }
}
