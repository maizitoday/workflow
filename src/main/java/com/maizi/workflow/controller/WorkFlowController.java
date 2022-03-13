/*
 * @Descripttion: 
 * @version: v1.0
 * @Author: yubo
 * @Date: 2022-03-12 15:54:35
 * @LastEditors: yubo
 * @LastEditTime: 2022-03-13 19:22:12
 */
package com.maizi.workflow.controller;

import com.maizi.workflow.security.SecurityUtil;
import com.maizi.workflow.service.StudentServiceImpl;

import org.activiti.api.process.model.ProcessInstance;
import org.activiti.api.process.model.builders.ProcessPayloadBuilder;
import org.activiti.api.process.model.payloads.StartProcessPayload;
import org.activiti.api.process.runtime.ProcessRuntime;
import org.activiti.api.runtime.shared.query.Page;
import org.activiti.api.runtime.shared.query.Pageable;
import org.activiti.api.task.model.Task;
import org.activiti.api.task.model.builders.TaskPayloadBuilder;
import org.activiti.api.task.runtime.TaskRuntime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class WorkFlowController {

    @Autowired
    private StudentServiceImpl studentService;

    @Autowired
    private ProcessRuntime processRuntime;

    @Autowired
    private TaskRuntime taskRuntime;

    @Autowired
    private SecurityUtil securityUtil;

    /**
     * @Description: 方法说明....
     * @Date: 2022-03-13 13:58:06
     * @param {*}
     * @return {*}
     * @LastEditors: Do not edit
     */
    @GetMapping(value = "list")
    public String list() {
        // return studentService.list();
        return "来了";
    }

    // 1. 流程部署
    // 2. 流程定义
    // 3. 流程实例

    /**
     * @Description: 方法说明....查看流程定义
     * @Date: 2022-03-13 13:58:45
     * @param {*}
     * @return {*}
     * @LastEditors: Do not edit
     */
    @GetMapping(value = "my-process")
    public void contextLoads() {
        // securityUtil.logInAs("jack"); // ROLE_ACTIVITI_USER 和
        // ROLE_ACTIVITI_ADMIN的区别是什么
        Page<org.activiti.api.process.model.ProcessDefinition> processDefinitionPage = processRuntime
                .processDefinitions(Pageable.of(0, 10));
        System.out.println("可用的流程定义数量：" + processDefinitionPage.getTotalItems());
        for (org.activiti.api.process.model.ProcessDefinition pd : processDefinitionPage.getContent()) {
            System.out.println("流程定义：" + pd);
        }

    }

    /**
     * @Description: 方法说明....启动流程实例
     * @Date: 2022-03-13 13:58:54
     * @param {*}
     * @return {*}
     * @LastEditors: Do not edit
     */
    @GetMapping(value = "my-process-instance")
    public void testStartProcess() {
        StartProcessPayload startProcessPayload = ProcessPayloadBuilder.start().withProcessDefinitionKey("mydemo")
                .build();
        ProcessInstance start = processRuntime.start(startProcessPayload);
        System.out.println("流程实例：" + start);
    }

    /**
     * @Description: 方法说明....
     * @Date: 2022-03-13 13:59:01
     * @param {*}
     * @return {*}
     * @LastEditors: Do not edit
     */
    @GetMapping(value = "task")
    public void testTask() {
        securityUtil.logInAs("jack");
        Page<Task> taskPage = taskRuntime.tasks(Pageable.of(0, 10));
        if (taskPage.getTotalItems() > 0) {
            for (Task task : taskPage.getContent()) {
                taskRuntime.claim(TaskPayloadBuilder.claim().withTaskId(task.getId()).build());
                System.out.println("任务：" + task);
                taskRuntime.complete(TaskPayloadBuilder.complete().withTaskId(task.getId()).build());
            }
        }
        Page<Task> taskPage2 = taskRuntime.tasks(Pageable.of(0, 10));
        if (taskPage2.getTotalItems() > 0) {
            System.out.println("任务: " + taskPage2.getContent());
        }
    }
}
