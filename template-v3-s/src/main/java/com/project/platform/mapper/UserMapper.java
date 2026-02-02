package com.project.platform.mapper;

import com.project.platform.entity.User;
import java.util.List;
import java.util.Map;

/**
 * 用户表Mapper
 */
public interface UserMapper {

    /**
     * 分页查询
     */
    List<User> queryPage(int offset, int pageSize, Map<String, Object> query);

    /**
     * 查询总数
     */
    int queryCount(Map<String, Object> query);

    /**
     * 根据ID查询
     */
    User selectById(Integer id);

    /**
     * 根据用户名（邮箱）查询
     */
    User selectByUserName(String username);

    /**
     * 根据邮箱查询
     */
    User selectByEmail(String email);

    /**
     * 根据手机号查询
     */
    User selectByTel(String tel);

    /**
     * 查询所有
     */
    List<User> list();

    /**
     * 插入
     */
    int insert(User record);

    /**
     * 更新
     */
    int updateById(User record);

    /**
     * 批量删除
     */
    int removeByIds(List<Integer> ids);

}
