package com.spring.quartz.task.job;

import cn.hutool.core.date.DateUtil;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;

/**
 * @author hu jie
 * @date 2018/10/27
 */
public class TestJob3 extends QuartzJobBean {

    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {

        System.out.println("某时间段内定时执行：" + DateUtil.now());
    }
}
