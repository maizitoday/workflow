/*
 * @Descripttion: 
 * @version: v1.0
 * @Author: yubo
 * @Date: 2022-03-12 15:54:35
 * @LastEditors: yubo
 * @LastEditTime: 2022-03-13 11:44:43
 */
package com.maizi.workflow.controller;

import java.util.List;

import com.maizi.workflow.entity.Student;
import com.maizi.workflow.service.StudentServiceImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class StudentController {

    @Autowired
    private StudentServiceImpl studentService;

    @GetMapping(value = "list")
    public List<Student> list() {
        return studentService.list();
    }

}
