package com.spring.quartz.task.job;

import cn.hutool.core.date.DateUtil;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;

/**
 * @author hu jie
 * @date 2018/10/27
 */
public class TestJob2 extends QuartzJobBean {

    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        String name = context.getTrigger().getKey().getName();
        System.out.println("任务名：" + name+"执行时间：" + DateUtil.now());
    }
}
