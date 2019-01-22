package com.whli.jee.job.service;

import com.whli.jee.core.web.service.IBaseService;
import com.whli.jee.job.entity.Scheduler;
import org.springframework.transaction.annotation.Transactional;

/**
 * <p>类功能<p>
 *
 * @author whli
 * @version 2018/12/24 11:15
 */
public interface ISchedulerService extends IBaseService<Scheduler> {

    /**
     * 开始任务
     * @param entity
     * @return
     */
    @Transactional
    public Boolean startTask(Scheduler entity);

    /**
     * 暂停任务
     * @param entity
     * @return
     */
    @Transactional
    public Boolean stopTask(Scheduler entity);

    /**
     * 开始所有任务
     * @return
     */
    @Transactional
    public Boolean startAll();

    /**
     * 停止所有任务
     * @return
     */
    @Transactional
    public Boolean stopAll();
}
