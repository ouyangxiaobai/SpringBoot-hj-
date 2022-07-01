package com.example.sens.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.sens.common.base.BaseService;
import com.example.sens.entity.Post;

import java.util.List;

/**
 * <pre>
 *     记录/页面业务逻辑接口
 * </pre>
 */
public interface PostService extends BaseService<Post, Long> {

    /**
     * 根据条件获得列表
     *
     * @param condition
     * @return
     */
    Page<Post> findPostByCondition(Post condition, Page<Post> page);


    /**
     * 根据租客userId查询
     *
     * @param userId
     * @return
     */
    Page<Post> findByRentUserId(Long userId, Page<Post> page);


    /**
     * 获得最新房屋
     *
     * @param cityId
     * @param limit
     * @return
     */
    List<Post> getLatestPost(Long cityId, int limit);

    /**
     * 根据状态统计
     *
     * @param postStatus 状态
     * @return
     */
    Integer countByStatus(Integer postStatus);

    /**
     * 获得合租房屋
     *
     * @return
     */
    List<Post> getUnionRentPost(Post post);
}
