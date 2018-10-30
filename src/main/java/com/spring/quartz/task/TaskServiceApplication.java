package com.spring.quartz.task;

import com.spring.quartz.task.config.QuartzManager;
import com.spring.quartz.task.dao.UserDao;
import com.spring.quartz.task.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

import java.util.List;

@SpringBootApplication
@EntityScan(basePackages = "com.spring.quartz.task.model")
public class TaskServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(TaskServiceApplication.class, args);
    }

    @Autowired
    private UserDao userDao;

    @Bean
    public CommandLineRunner commandLineRunner(ApplicationContext context){
        return args -> {
            QuartzManager bean = context.getBean(QuartzManager.class);
//            bean.addJob("testJob1", Constants.DEFAULT_GROUP_NAME,"testTrigger1",Constants.DEFAULT_TRIGGER_GROUP_NAME, TestJob1.class,"0/1 * * * * ?",null);
//            bean.addJob("testJob2", Constants.DEFAULT_GROUP_NAME,"testTrigger2",Constants.DEFAULT_TRIGGER_GROUP_NAME, TestJob2.class,"0/10 * * * * ?",null);
            List<User> all = userDao.findAll();
            System.out.println(all.toString());
        };
    }

}
