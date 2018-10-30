package com.spring.quartz.task.dao;

import com.spring.quartz.task.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author hu jie
 * @date 2018/10/30
 */
public interface UserDao extends JpaRepository<User,Integer> {
}
