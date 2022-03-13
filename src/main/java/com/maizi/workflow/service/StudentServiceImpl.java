/*
 * @Descripttion: 
 * @version: v1.0
 * @Author: yubo
 * @Date: 2022-03-13 11:41:18
 * @LastEditors: yubo
 * @LastEditTime: 2022-03-13 11:42:20
 */
package com.maizi.workflow.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.maizi.workflow.entity.Student;
import com.maizi.workflow.mapper.StudentMapper;

import org.springframework.stereotype.Service;

@Service
public class StudentServiceImpl extends ServiceImpl<StudentMapper, Student> {

}
