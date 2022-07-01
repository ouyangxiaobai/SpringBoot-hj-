package com.example.sens.dto;

import lombok.Data;

import java.util.List;

/**
 * @author xxx
 * @date 2021/3/12 4:53 下午
 */
@Data
public class PostQueryCondition {

    /**
     * 用户ID
     */
    private Long userId;


    /**
     * 分类ID
     */
    private Long cateId;

    /**
     * 预订的日期集合
     */
    private List<String> dateList;
}
