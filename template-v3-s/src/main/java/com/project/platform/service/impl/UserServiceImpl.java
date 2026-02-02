package com.project.platform.service.impl;

import com.alibaba.fastjson2.JSONObject;
import com.project.platform.dto.CurrentUserDTO;
import com.project.platform.dto.RetrievePasswordDTO;
import com.project.platform.dto.UpdatePasswordDTO;
import com.project.platform.entity.User;
import com.project.platform.exception.CustomException;
import com.project.platform.mapper.UserMapper;
import com.project.platform.service.UserService;
import com.project.platform.utils.CurrentUserThreadLocal;
import com.project.platform.utils.PasswordUtils;
import com.project.platform.vo.PageVO;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * 用户服务实现类
 */
@Service
public class UserServiceImpl implements UserService {
    @Resource
    private UserMapper userMapper;

    @Override
    public PageVO<User> page(Map<String, Object> query, Integer pageNum, Integer pageSize) {
        PageVO<User> page = new PageVO<>();
        List<User> list = userMapper.queryPage((pageNum - 1) * pageSize, pageSize, query);
        page.setList(list);
        page.setTotal(userMapper.queryCount(query));
        return page;
    }

    @Override
    public User selectById(Integer id) {
        return userMapper.selectById(id);
    }

    @Override
    public List<User> list() {
        return userMapper.list();
    }

    @Override
    public void insert(User entity) {
        check(entity);
        // 密码加密
        if (entity.getPassword() != null) {
            entity.setPassword(PasswordUtils.encode(entity.getPassword()));
        }
        // 默认状态
        if (entity.getStatus() == null) {
            entity.setStatus("active");
        }
        userMapper.insert(entity);
    }

    @Override
    public void updateById(User entity) {
        check(entity);
        // 如果更新了密码，需要加密
        if (entity.getPassword() != null && !entity.getPassword().isEmpty()) {
            entity.setPassword(PasswordUtils.encode(entity.getPassword()));
        }
        userMapper.updateById(entity);
    }

    private void check(User entity) {
        // 检查用户名（邮箱）是否已存在
        User user = userMapper.selectByUserName(entity.getUsername());
        if (user != null && (entity.getId() == null || !user.getId().equals(entity.getId()))) {
            throw new CustomException("用户名（邮箱）已存在");
        }
        // 检查邮箱是否已存在
        if (entity.getEmail() != null) {
            User userByEmail = userMapper.selectByEmail(entity.getEmail());
            if (userByEmail != null && (entity.getId() == null || !userByEmail.getId().equals(entity.getId()))) {
                throw new CustomException("邮箱已存在");
            }
        }
    }

    @Override
    public void removeByIds(List<Integer> ids) {
        userMapper.removeByIds(ids);
    }

    @Override
    public CurrentUserDTO login(String username, String password) {
        User user = userMapper.selectByUserName(username);
        if (user == null) {
            throw new CustomException("用户名或密码错误");
        }
        // 验证密码
        if (!PasswordUtils.matches(password, user.getPassword())) {
            throw new CustomException("用户名或密码错误");
        }
        // 检查状态
        if (!"active".equals(user.getStatus())) {
            throw new CustomException("用户已被禁用");
        }
        CurrentUserDTO currentUserDTO = new CurrentUserDTO();
        BeanUtils.copyProperties(user, currentUserDTO);
        return currentUserDTO;
    }

    @Override
    public void register(JSONObject data) {
        User user = new User();
        user.setUsername(data.getString("username")); // 邮箱作为用户名
        user.setEmail(data.getString("username")); // 邮箱
        user.setPassword(data.getString("password"));
        user.setNickname(data.getString("nickname"));
        user.setAvatarUrl(data.getString("avatarUrl"));
        user.setTel(data.getString("tel"));
        user.setGender(data.getString("gender"));
        user.setStatus("active");
        insert(user);
    }

    @Override
    public void updateCurrentUserInfo(CurrentUserDTO currentUserDTO) {
        User user = userMapper.selectById(currentUserDTO.getId());
        user.setNickname(currentUserDTO.getNickname());
        user.setAvatarUrl(currentUserDTO.getAvatarUrl());
        user.setTel(currentUserDTO.getTel());
        user.setEmail(currentUserDTO.getEmail());
        userMapper.updateById(user);
    }

    @Override
    public void updateCurrentUserPassword(UpdatePasswordDTO updatePassword) {
        User user = userMapper.selectById(CurrentUserThreadLocal.getCurrentUser().getId());
        // 验证旧密码
        if (!PasswordUtils.matches(updatePassword.getOldPassword(), user.getPassword())) {
            throw new CustomException("旧密码不正确");
        }
        // 设置新密码（会自动加密）
        user.setPassword(updatePassword.getNewPassword());
        userMapper.updateById(user);
    }

    @Override
    public void resetPassword(Integer id) {
        User user = userMapper.selectById(id);
        // 重置为默认密码
        user.setPassword(PasswordUtils.encode("123456"));
        userMapper.updateById(user);
    }

    @Override
    public void retrievePassword(RetrievePasswordDTO retrievePasswordDTO) {
        User user = userMapper.selectByTel(retrievePasswordDTO.getTel());
        if (user == null) {
            throw new CustomException("手机号不存在");
        }
        // TODO 校验验证码
        // 设置新密码
        user.setPassword(PasswordUtils.encode(retrievePasswordDTO.getPassword()));
        userMapper.updateById(user);
    }
}
