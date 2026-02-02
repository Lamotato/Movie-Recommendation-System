package com.project.platform.mapper;

import com.project.platform.entity.Admin;
import java.util.List;
import java.util.Map;

/**
* @author TOYA
* @description 针对表【admin(管理员表)】的数据库操作Mapper
* @createDate 2026-01-12 14:45:33
* @Entity com.project.platform.entity.Admin
*/
public interface AdminMapper {

    /**
     * 分页查询
     */
    List<Admin> queryPage(int offset, int pageSize, Map<String, Object> query);

    /**
     * 查询总数
     */
    int queryCount(Map<String, Object> query);

    /**
     * 根据ID查询
     */
    Admin selectById(Integer id);

    /**
     * 根据用户名查询
     */
    Admin selectByUserName(String username);

    /**
     * 根据手机号查询
     */
    Admin selectByTel(String tel);

    /**
     * 查询所有
     */
    List<Admin> list();

    /**
     * 插入
     */
    int insert(Admin record);

    /**
     * 更新
     */
    int updateById(Admin record);

    /**
     * 批量删除
     */
    int removeByIds(List<Integer> ids);

}
