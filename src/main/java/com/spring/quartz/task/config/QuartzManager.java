package com.spring.quartz.task.config;

import org.quartz.*;
import org.quartz.impl.matchers.GroupMatcher;
import org.quartz.impl.triggers.SimpleTriggerImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author hu jie
 * @date 2018/10/27
 */
@Component
public class QuartzManager {


    @Autowired
    private Scheduler scheduler;

    /**
     * 添加一个定时任务
     * @param jobName          任务名
     * @param jobGroupName     任务组名
     * @param triggerName      触发器名
     * @param triggerGroupName 触发器组名
     * @param jobClass         任务
     * @param cron             时间设置，参考quartz说明文档
     * @param param       task参数
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public void addJob(String jobName, String jobGroupName, String triggerName, String triggerGroupName,
                       Class jobClass, String cron, Map<String, Object> param) {
        try {
            JobDataMap jobMap = new JobDataMap();
            if(param != null){
                jobMap.putAll(param);
            }
            // 任务名，任务组，任务执行类
            JobDetail jobDetail = JobBuilder.newJob(jobClass).withIdentity(jobName, jobGroupName)
                    .usingJobData(jobMap)
                    .build();
            // 触发器
            TriggerBuilder<Trigger> triggerBuilder = TriggerBuilder.newTrigger();
            // 触发器名,触发器组
            triggerBuilder.withIdentity(triggerName, triggerGroupName);
            triggerBuilder.startNow();
            // 触发器时间设定
            triggerBuilder.withSchedule(CronScheduleBuilder.cronSchedule(cron).withMisfireHandlingInstructionDoNothing());
            // 创建Trigger对象
            CronTrigger trigger = (CronTrigger) triggerBuilder.build();
            // 调度容器设置JobDetail和Trigger
            scheduler.scheduleJob(jobDetail, trigger);
            scheduler.start();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     *  修改一个任务的触发时间
     * @param triggerName      触发器名
     * @param triggerGroupName 触发器组名
     * @param cron             时间设置，cron 表达式
     */
    public void modifyJobTime(String triggerName, String triggerGroupName, String cron) {
        try {
           /* TriggerKey triggerKey1 = scheduler.getTriggersOfJob(JobKey.jobKey("", "")).stream()
                    .map(Trigger::getKey).filter(triggerKey -> triggerKey.getName().equals(triggerName)
                            && triggerKey.getGroup().equals(triggerGroupName)).findFirst().get();*/

            TriggerKey triggerKey = TriggerKey.triggerKey(triggerName, triggerGroupName);
            CronTrigger trigger = (CronTrigger) scheduler.getTrigger(triggerKey);
            if (trigger == null) {
                return;
            }
            String oldTime = trigger.getCronExpression();
            if (!oldTime.equalsIgnoreCase(cron)) {
                // 触发器
                TriggerBuilder<Trigger> triggerBuilder = TriggerBuilder.newTrigger();
                // 触发器名,触发器组
                triggerBuilder.withIdentity(triggerName, triggerGroupName);
                triggerBuilder.startNow();
                // 触发器时间设定
                triggerBuilder.withSchedule(CronScheduleBuilder.cronSchedule(cron));
                // 创建Trigger对象
                trigger = (CronTrigger) triggerBuilder.build();
                // 修改一个任务的触发时间
                scheduler.rescheduleJob(triggerKey, trigger);
                scheduler.start();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 停止一个任务
     * @param jobName 任务名
     * @param jobGroupName 任务组
     */
    public void stopJob(String jobName, String jobGroupName){
        try {
            scheduler.pauseJob(JobKey.jobKey(jobName,jobGroupName));
        } catch (SchedulerException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 重新启动一个任务
     * @param jobName 任务名
     * @param jobGroupName 任务组
     */
    public void restartJob(String jobName, String jobGroupName){
        try {
            scheduler.resumeJob(JobKey.jobKey(jobName,jobGroupName));
        } catch (SchedulerException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 移除一个任务
     * @param jobName 任务名
     * @param jobGroupName 任务组
     */
    public void removeJob(String jobName, String jobGroupName) {
        try {
            // 删除任务
            scheduler.deleteJob(JobKey.jobKey(jobName, jobGroupName));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 获取这在执行的任务
     *  执行这个方法 com.spring.quartz.task.job.TestJob2#executeInternal(org.quartz.JobExecutionContext)
     * @return List<String> 任务名列表
     */
    public List<String> getExecuteJobs(){
        try {
            return scheduler.getCurrentlyExecutingJobs().stream()
                    .map(context -> context.getTrigger().getKey().getName()).collect(Collectors.toList());
        } catch (SchedulerException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 获取所有的任务信息
     * @return List<String>
     */
    public List<String> getStartTaskJob() {
        List<String> jobNameList = new ArrayList<>();
        try {
            for (String groupName : scheduler.getJobGroupNames()) {
                for (JobKey jobKey : scheduler.getJobKeys(GroupMatcher.jobGroupEquals(groupName))) {
                    String jobName = jobKey.getName();
                    jobNameList.add(jobName);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return jobNameList;
    }

    /**
     * 在startTime时执行调试，endTime结束执行调度，重复执行repeatCount次，每隔repeatInterval秒执行一次
     *
     * @param name           Quartz SimpleTrigger 名称
     * @param startTime      调度开始时间
     * @param endTime        调度结束时间
     * @param repeatInterval 执行时间隔间，单位：秒
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public void startTaskJobByTime(String name, String group, Date startTime, Date endTime,
                                   int repeatInterval,  Map<String, Object> param, Class jobClass) {

            if (StringUtils.isEmpty(name)) {
                name = UUID.randomUUID().toString();
            }
            JobDataMap jobMap = new JobDataMap();
            if(param != null){
                jobMap.putAll(param);
            }
            // 任务名，任务组，任务执行类
            JobDetail jobDetail = JobBuilder.newJob(jobClass).withIdentity(name, group).usingJobData(jobMap).build();
            // endAt  和 withRepeatCount  设置了endAt  ，withRepeatCount 是不生效的，会按照endAt 执行
            SimpleTrigger trigger = TriggerBuilder.newTrigger().withIdentity(name, group)
                    .startAt(startTime)
                    .endAt(endTime)
                    .withSchedule(
                            SimpleScheduleBuilder.simpleSchedule()
                                    .withIntervalInSeconds(repeatInterval)
                                    .repeatForever()
                    )
                    .build();
            try {
                scheduler.scheduleJob(jobDetail, trigger);
                scheduler.start();
            } catch (SchedulerException e) {
                throw new RuntimeException(e);
            }
    }


    private boolean isValidExpression(final Date startTime) {
        SimpleTriggerImpl trigger = new SimpleTriggerImpl();
        trigger.setStartTime(startTime);
        Date date = trigger.computeFirstFireTime(null);
        return date != null && date.after(new Date());
    }

    /**
     * 修改执行频率
     *
     * @param jobName 任务名称
     * @param groupName 任务组名
     * @param time    每隔多少秒执行一次
     */
    public void restJob(String jobName, String groupName, Date startTime, Date endTime, long time) {
        TriggerKey triggerKey = new TriggerKey(jobName, groupName);
        SimpleTriggerImpl simpleTrigger = null;
        try {
            simpleTrigger = (SimpleTriggerImpl) scheduler.getTrigger(triggerKey);
        } catch (SchedulerException e) {
            throw new RuntimeException(e);
        }
        if (startTime != null) {
            simpleTrigger.setStartTime(startTime);
        }
        if (endTime != null) {
            simpleTrigger.setEndTime(endTime);
        }
        //将秒转为毫秒
        simpleTrigger.setRepeatInterval(time * 1000);
        try {
            scheduler.rescheduleJob(triggerKey, simpleTrigger);
        } catch (SchedulerException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 启动所有定时任务
     */
    public void startJobs() {
        try {
            scheduler.start();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * scheduler 被关闭 定时任务系统不能使用
     */
    public void shutdownJobs() {
        try {
            if (!scheduler.isShutdown()) {
                scheduler.shutdown();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 停止所有的任务
     */
    public void stopAll(){
        try {
            scheduler.pauseAll();
        } catch (SchedulerException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 重启所有任务
     */
    public void restartAll(){
        try {
            scheduler.resumeAll();
        } catch (SchedulerException e) {
            throw new RuntimeException(e);
        }
    }

}
