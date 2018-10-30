package com.spring.quartz.task.web;

import cn.hutool.core.date.DateUtil;
import com.spring.quartz.task.config.QuartzManager;
import com.spring.quartz.task.constant.Constants;
import com.spring.quartz.task.job.TestJob3;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author hu jie
 * @date 2018/10/27
 */
@RestController
public class TaskController {

    @Autowired
    private QuartzManager quartzManager;

    @GetMapping("/stop-task")
    public String stopTask(){
        quartzManager.stopJob("testJob1", Constants.DEFAULT_GROUP_NAME);
        return "stop-task";
    }

    @GetMapping("/restart-task")
    public String restartJob(){
        quartzManager.restartJob("testJob1", Constants.DEFAULT_GROUP_NAME);
        return "restart-task";
    }

    @GetMapping("/start-task")
    public String startTask(){
        quartzManager.startJobs();
        return "start-task";
    }

    @GetMapping("/modify-task")
    public String modifyJobCron(){
        quartzManager.modifyJobTime("testTrigger1",Constants.DEFAULT_TRIGGER_GROUP_NAME,"0/5 * * * * ?");
        return "modify-task";
    }

    @GetMapping("/remove-task")
    public String removeTask(){
        quartzManager.removeJob("testJob1", Constants.DEFAULT_GROUP_NAME);
        return "remove-task";
    }

    @GetMapping("/getExecuteJobs")
    public String getExecuteJobs(){
        return  String.join(",", quartzManager.getExecuteJobs());
    }

    @GetMapping("/getTaskList")
    public String getTaskList(){
        List<String> startTaskJob = quartzManager.getStartTaskJob();
        return String.join(",", startTaskJob);
    }

    @GetMapping("/stop-all")
    public String stopAll(){
        quartzManager.stopAll();
        return "stop-all";
    }

    @GetMapping("/restart-all")
    public String restartAll(){
        quartzManager.restartAll();
        return "restart-all";
    }

    @GetMapping("/shutdown")
    public String shutdown(){
        quartzManager.shutdownJobs();
        return "shutdown";
    }

    @GetMapping("/startTaskJobByTime")
    public String startTaskJobByTime(){
        quartzManager.startTaskJobByTime("testJob3",Constants.DEFAULT_GROUP_NAME,DateUtil.parse("2018-10-28 09:59:00","yyyy-MM-dd HH:mm:ss"),
                DateUtil.parse("2018-10-28 10:22:00","yyyy-MM-dd HH:mm:ss"),10,null, TestJob3.class);
        return "startTaskJobByTime";
    }

    @GetMapping("/restJob")
    public String restJobTime(){
        quartzManager.restJob("testJob3", Constants.DEFAULT_GROUP_NAME, null, null, 1);
        return "restJob";
    }


}
