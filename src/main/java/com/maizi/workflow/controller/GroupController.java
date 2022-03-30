/*
 * @Descripttion: 
 * @version: v1.0
 * @Author: yubo
 * @Date: 2022-03-30 13:33:41
 * @LastEditors: yubo
 * @LastEditTime: 2022-03-30 13:40:02
 */
package com.maizi.workflow.controller;

import com.maizi.workflow.security.SecurityUtil;

import org.activiti.api.process.runtime.ProcessRuntime;
import org.activiti.api.runtime.shared.query.Page;
import org.activiti.api.runtime.shared.query.Pageable;
import org.activiti.api.task.model.Task;
import org.activiti.api.task.model.builders.TaskPayloadBuilder;
import org.activiti.api.task.runtime.TaskRuntime;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GroupController {

    private Logger logger = LoggerFactory.getLogger(GroupController.class);

    @Autowired
    private ProcessRuntime processRuntime;

    @Autowired
    private TaskRuntime taskRuntime;

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private SecurityUtil securityUtil;

    @GetMapping(value = "testGroup")
    public void testGroup() {

        // Using Security Util to simulate a logged in user
        securityUtil.logInAs("rose");

        // Let's create a Group Task (not assigned, all the members of the group can
        // claim it)
        // Here 'salaboy' is the owner of the created task
        logger.info("> Creating a Group Task for 'activitiTeam'");
        taskRuntime.create(TaskPayloadBuilder.create()
                .withName("First Team Task")
                .withDescription("This is something really important")
                .withCandidateGroup("activitiTeam")
                .withPriority(10)
                .build());

        // Let's log in as 'other' user that doesn't belong to the 'activitiTeam' group
        securityUtil.logInAs("admin");

        // Let's get all my tasks (as 'other' user)
        logger.info("> Getting all the tasks");
        Page<Task> tasks = taskRuntime.tasks(Pageable.of(0, 10));

        // No tasks are returned
        logger.info(">  Other cannot see the task: " + tasks.getTotalItems());

        // Now let's switch to a user that belongs to the activitiTeam
        securityUtil.logInAs("tom");

        // Let's get 'erdemedeiros' tasks
        logger.info("> Getting all the tasks");
        tasks = taskRuntime.tasks(Pageable.of(0, 10));

        // 'erdemedeiros' can see and claim the task
        logger.info(">  erdemedeiros can see the task: " + tasks.getTotalItems());

        String availableTaskId = tasks.getContent().get(0).getId();

        // Let's claim the task, after the claim, nobody else can see the task and
        // 'erdemedeiros' becomes the assignee
        logger.info("> Claiming the task");
        taskRuntime.claim(TaskPayloadBuilder.claim().withTaskId(availableTaskId).build());

        // Let's complete the task
        logger.info("> Completing the task");
        taskRuntime.complete(TaskPayloadBuilder.complete().withTaskId(availableTaskId).build());

    }

}
