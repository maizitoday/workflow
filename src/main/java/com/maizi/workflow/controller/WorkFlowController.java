/*
 * @Descripttion: 
 * @version: v1.0
 * @Author: yubo
 * @Date: 2022-03-12 15:54:35
 * @LastEditors: yubo
 * @LastEditTime: 2022-03-30 16:01:27
 */
package com.maizi.workflow.controller;

import java.util.HashMap;
import java.util.List;

import com.maizi.workflow.entity.Role;
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
public class WorkFlowController {

    private Logger logger = LoggerFactory.getLogger(WorkFlowController.class);

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
        securityUtil.logInAs("system"); // ROLE_ACTIVITI_USER 和
        Page<org.activiti.api.process.model.ProcessDefinition> processDefinitionPage = processRuntime
                .processDefinitions(Pageable.of(0, 10));
        System.out.println("可用的流程定义数量：" + processDefinitionPage.getTotalItems());
        for (org.activiti.api.process.model.ProcessDefinition pd : processDefinitionPage.getContent()) {
            System.out.println("流程定义：" + pd);
        }

    }

    /**
     * @Description: 方法说明....
     * @Date: 2022-03-23 20:24:46
     * @param {*}
     * @return {*}
     * @LastEditors: Do not edit
     */
    @GetMapping(value = "startProcess1")
    public void startProcess1() {
        securityUtil.logInAs("system");

        Role role = new Role();
        role.setRoleType(2);
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("userA", "system");
        map.put("userB", "system"); // 需要和xml文件里面的进行对应起来
        map.put("group1", "root_role");
        map.put("role", 2);
        org.activiti.engine.runtime.ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(
                "my_test",
                "1002", map);
        System.out.println("流程实例：" + processInstance);
    }

    /**
     * @Description: 方法说明....
     * @Date: 2022-03-13 13:59:01
     * @param {*}
     * @return {*}
     * @LastEditors: Do not edit
     */
    @GetMapping(value = "task1")
    public void testTask1() {
        securityUtil.logInAs("system");
        // 查看系统中的所有任务
        List<org.activiti.engine.task.Task> list = taskService.createTaskQuery().processDefinitionKey("my_test").list();
        for (org.activiti.engine.task.Task task : list) {
            System.out.println("----------------------------");
            System.out.println("流程实例id：" + task.getProcessInstanceId());
            System.out.println("任务id：" + task.getId());
            System.out.println("任务负责人：" + task.getAssignee());
            System.out.println("任务名称：" + task.getName());
        }

        Page<Task> taskPage = taskRuntime.tasks(Pageable.of(0, 10));
        if (taskPage.getTotalItems() > 0) {
            for (Task task : taskPage.getContent()) {
                // taskRuntime.claim(TaskPayloadBuilder.claim().withTaskId(task.getId()).build());
                System.out.println("任务：" + task);
                taskRuntime.complete(TaskPayloadBuilder.complete().withTaskId(task.getId()).build());
            }
        }
        Page<Task> taskPage2 = taskRuntime.tasks(Pageable.of(0, 10));
        if (taskPage2.getTotalItems() > 0) {
            System.out.println("任务: " + taskPage2.getContent());
        }
    }

    /**
     * @Description: 方法说明....
     * @Date: 2022-03-30 13:54:03
     * @param {*}
     * @return {*}
     * @LastEditors: Do not edit
     */
    @GetMapping(value = "test-groupA")
    public void testGroupA() {
        securityUtil.logInAs("rose");
        Role role = new Role();
        role.setRoleType(2);
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("assignee", null);
        // map.put("candidateGroups", "activitiTeam");
        map.put("role", 2);
        org.activiti.engine.runtime.ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(
                "maizi_process",
                "1001", map);
        System.out.println("流程实例：" + processInstance);

        // Let's log in as 'other' user that doesn't belong to the 'activitiTeam' group
        securityUtil.logInAs("admin");

        // Let's get all my tasks (as 'other' user)
        logger.info("> Getting all the tasks");
        Page<Task> tasks = taskRuntime.tasks(Pageable.of(0, 10));

        // No tasks are returned
        logger.info(">  Other cannot see the task: " + tasks.getTotalItems());
    }

    /**
     * @Description: 方法说明....
     * @Date: 2022-03-30 15:21:19
     * @param {*}
     * @return {*}
     * @LastEditors: Do not edit
     */
    @GetMapping(value = "test-group-taskA")
    public void testGroupTask() {
        // Now let's switch to a user that belongs to the activitiTeam
        securityUtil.logInAs("rose");

        // Let's get 'erdemedeiros' tasks
        logger.info("> Getting all the tasks");
        Page<Task> tasks = taskRuntime.tasks(Pageable.of(0, 10));

        // 'erdemedeiros' can see and claim the task
        logger.info(">  erdemedeiros can see the task: " + tasks.getTotalItems());

        String availableTaskId = tasks.getContent().get(0).getId();

        securityUtil.logInAs("tom");

        List<org.activiti.engine.task.Task> list = taskService.createTaskQuery().processDefinitionKey("maizi_process")
                .taskCandidateGroup("activitiTeam").list();
        org.activiti.engine.task.Task task = list.get(0);
        System.out.println("task:" + task.getId());

        String taskId = task.getId();
        // 拾取组任务
        task = taskService.createTaskQuery()
                .taskId(taskId)
                .taskCandidateUser("tom")// 根据候选人查询
                .singleResult();
        if (task != null) {
            // 拾取任务
            taskService.claim(taskId, "tom");
            System.out.println("任务拾取成功");
        }
    }

    /**
     * @Description: 方法说明....
     * @Date: 2022-03-30 16:01:44
     * @param {*}
     * @return {*}
     * @LastEditors: Do not edit
     */
    @GetMapping(value = "test-group-task-complete")
    public void handlerGroupTask() {
        // 查询个人待办任务
        securityUtil.logInAs("tom");
        List<org.activiti.engine.task.Task> list = taskService.createTaskQuery().processDefinitionKey("maizi_process")
                .taskAssignee("tom").list();
        for (org.activiti.engine.task.Task temTask : list) {
            System.out.println("----------------------------");
            System.out.println("流程实例id：" + temTask.getProcessInstanceId());
            System.out.println("任务id：" + temTask.getId());
            System.out.println("任务负责人：" + temTask.getAssignee());
            System.out.println("任务名称：" + temTask.getName());
            String taskId = temTask.getId();
            taskService.complete(taskId);
        }
    }

    /**
     * @Description: 方法说明....
     * @Date: 2022-03-23 22:18:12
     * @param {*}
     * @return {*}
     * @LastEditors: Do not edit
     */
    @GetMapping(value = "startProcess2")
    public void startProcess2() {
        securityUtil.logInAs("rose");
        Role role = new Role();
        role.setRoleType(2);
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("userC", "rose");
        map.put("userD", "rose");
        map.put("role", 1);
        org.activiti.engine.runtime.ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(
                "my_test",
                "1003", map);
        System.out.println("流程实例：" + processInstance);
    }

    /**
     * @Description: 方法说明....
     * @Date: 2022-03-23 22:20:25
     * @param {*}
     * @return {*}
     * @LastEditors: Do not edit
     */
    @GetMapping(value = "task2")
    public void testTask2() {
        securityUtil.logInAs("rose");
        // 查看系统中的所有任务
        List<org.activiti.engine.task.Task> list = taskService.createTaskQuery().processDefinitionKey("my_test").list();
        for (org.activiti.engine.task.Task task : list) {
            System.out.println("----------------------------");
            System.out.println("流程实例id：" + task.getProcessInstanceId());
            System.out.println("任务id：" + task.getId());
            System.out.println("任务负责人：" + task.getAssignee());
            System.out.println("任务名称：" + task.getName());
        }

        Page<Task> taskPage = taskRuntime.tasks(Pageable.of(0, 10));
        if (taskPage.getTotalItems() > 0) {
            for (Task task : taskPage.getContent()) {
                // taskRuntime.claim(TaskPayloadBuilder.claim().withTaskId(task.getId()).build());
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
