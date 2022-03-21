/*
 * @Descripttion: 
 * @version: v1.0
 * @Author: yubo
 * @Date: 2022-03-13 11:39:19
 * @LastEditors: yubo
 * @LastEditTime: 2022-03-15 12:32:27
 */
package com.maizi.workflow.entity;

import java.util.Date;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @Description: 方法说明....
 * @Date: 2022-03-13 11:39:25
 * @param {*}
 * @return {*}
 * @LastEditors: Do not edit
 */

@Data
@Accessors(chain = true)
// @TableName("student")
public class Student {

    private Integer id;

    private String name;

    private String sex;

    private Integer age;

    private Date date;

    public static void main(String[] args) {

    }

}
