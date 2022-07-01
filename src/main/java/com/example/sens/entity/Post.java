package com.example.sens.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.example.sens.common.base.BaseEntity;
import lombok.Data;


/**
 * @author example
 */
@Data
@TableName("post")
public class Post extends BaseEntity {

    /**
     * 用户ID
     */
    private Long userId;
    /**
     * 房屋标题
     */
    private String postTitle;

    /**
     * 房屋描述
     */
    private String postContent;

    /**
     * 房屋摘要
     */
    private String postSummary;

    /**
     * 缩略图
     */
    private String postThumbnail;

    /**
     * 0 未租出
     * 1 已经租出去
     * 2 已删除
     */
    private Integer postStatus;

    /**
     * 日租金
     */
    private Integer dayPrice;

    /**
     * 月租金
     */
    private Integer monthPrice;

    /**
     * 房间编号
     */
    private String number;

    /**
     * 分类ID
     */
    private Long cateId;

    /**
     * 城市ID
     */
    private Long cityId;

    /**
     * 图片URL
     */
    private String imgUrl;

    /**
     * 富文本
     */
    private String postEditor;

    /**
     * 面积
     */
    private Double area;


    /**
     * 卧室数量
     */
    private Integer roomCount;


    /**
     * 洗手间数量
     */
    private Integer toiletCount;

    /**
     * 押金
     */
    private Integer deposit;

    /**
     * 是否中介
     */
    private boolean agencyFlag;


    /**
     * 房东姓名
     */
    private String ownerName;

    /**
     * 房东联系方式
     */
    private String ownerPhone;

    /**
     * 房屋所属分类
     */
    @TableField(exist = false)
    private Category category;


    /**
     * 房屋所属城市
     */
    @TableField(exist = false)
    private City city;

    /**
     * 房屋房东
     */
    @TableField(exist = false)
    private User user;

    /**
     * 用于搜索
     */
    @TableField(exist = false)
    private Integer minArea;

    /**
     * 用于搜索
     */
    @TableField(exist = false)
    private Integer maxArea;

    /**
     * 用于搜索
     */
    @TableField(exist = false)
    private Integer minPrice;

    /**
     * 用于搜索
     */
    @TableField(exist = false)
    private Integer maxPrice;

}