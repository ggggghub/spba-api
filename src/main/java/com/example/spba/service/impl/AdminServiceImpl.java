package com.example.spba.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.spba.dao.AdminMapper;
import com.example.spba.domain.entity.Admin;
import com.example.spba.domain.entity.LoginLog;
import com.example.spba.domain.entity.Role;
import com.example.spba.service.*;
import com.example.spba.utils.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.util.*;

@Service
public class AdminServiceImpl extends ServiceImpl<AdminMapper, Admin> implements AdminService
{

    @Autowired
    private RoleService roleService;

    @Autowired
    private MenuService menuService;

    @Autowired
    private LoginLogService loginLogService;

    @Autowired
    private TaskService taskService;

    @Override
    public HashMap checkLogin(HashMap params)
    {
        HashMap result = new HashMap();
        result.put("status", false);

        HashMap info = this.baseMapper.getInfo(params);
        if (info == null || info.get("status").equals(0)) {
            result.put("message", "登录失败");
            return result;
        }
        if (!DigestUtils.md5DigestAsHex((params.get("password") + info.get("safe").toString()).getBytes()).equals(info.get("password"))) {
            result.put("message", "密码错误");
            return result;
        }

        // 验证角色状态
        HashMap where = new HashMap();
        where.put("status", 1);
        where.put("role_ids", JSONUtil.parse(info.get("role")).toBean(List.class));
        List<Role> roles = roleService.getAll(where);
        if (roles.size() == 0) {
            result.put("message", "登录失败");
            return result;
        }

        // 登录
        StpUtil.login(info.get("id"));

        // 更新登录信息
        updateLogin(Long.valueOf(info.get("id").toString()), params.get("ip").toString());



        HashMap data = new HashMap<>();
        data.put("avatar", info.get("avatar"));
        data.put("username", info.get("username"));
        data.put("token", StpUtil.getTokenValue());
        result.put("data", data);
        result.put("status", true);
        result.put("identity_number", info.get("identity_number"));
        result.put("company_name", info.get("company_name"));

        return result;
    }

    @Override
    public Page<HashMap> getList(Page page, HashMap params) {
        return this.baseMapper.getList(page, params);
    }

    @Override
    public HashMap getInfo(HashMap params) {
        return this.baseMapper.getInfo(params);
    }

    @Override
    public List<HashMap> getRoleAdminAll(Integer roleId) {
        return this.baseMapper.getRoleAdminAll(roleId);
    }

    @Override
    public List<HashMap> getPermissionList(Integer adminId)
    {
        List<HashMap> list = new ArrayList<>();
        Admin admin = this.getById(adminId);
        if (admin.getStatus().equals(0)) {
            return list;
        }

        HashMap where = new HashMap();
        where.put("status", 1);
        where.put("role_ids", JSONUtil.parse(admin.getRole()).toBean(List.class));
        List<Role> roles = roleService.getAll(where);

        Integer root = 0;
        List<Integer> menuIds = new ArrayList<>();
        for (Role role : roles) {
            if (role.getRoot().equals(1)) {
                root = 1;
                break;
            }
            for (Object id : JSONUtil.parse(role.getPermission()).toBean(List.class)) {
                menuIds.add(Integer.valueOf(id.toString()));
            }
        }
        if (root.equals(0) && menuIds.size() == 0) {
            return list;
        }

        HashMap query = new HashMap();
        query.put("status", 1);
        if (root.equals(0)) {
            query.put("menu_ids", menuIds);
        }
        List<HashMap> menus = menuService.getAll(query);

        return menus;
    }
// 注册
@Override
public boolean register(Map<String, Object> params) {
    String username = (String) params.get("username");
    String password = (String) params.get("password");
    String identityNumber = (String) params.get("identity_number");
    String companyName = (String) params.get("company_name");


    if (username == null || password == null || identityNumber == null || companyName == null) {
        throw new RuntimeException("缺少必要参数");
    }

    // 用户名唯一校验
    long count1 = this.count(new QueryWrapper<Admin>().eq("username", username));
    if (count1 > 0) {
        throw new RuntimeException("用户名已存在");
    }

    // 唯一识别号唯一校验
    long count2 = this.count(new QueryWrapper<Admin>().eq("identity_number", identityNumber));
    if (count2 > 0) {
        throw new RuntimeException("唯一识别号已存在");
    }

    String str = String.valueOf(this.count(new QueryWrapper<Admin>().eq("company_name", companyName)));
    if (Integer.parseInt(str) > 0) {
        throw new RuntimeException("公司名已存在");
    }

    // 构建 Admin
    Admin admin = new Admin();
    admin.setUsername(username);
    admin.setCompanyName(companyName);
    // 生成 4 位随机 salt
    String safe = String.valueOf(System.currentTimeMillis()).substring(8, 12);
    admin.setSafe(safe);
    admin.setPassword(DigestUtils.md5DigestAsHex((password + safe).getBytes()));

    admin.setIdentityNumber(identityNumber);
    admin.setStatus(1);
    admin.setRole(JSONUtil.parse("[3]").toString());


    //调用文件存储
    String path = FileUtils.createUserFolder(admin.getIdentityNumber());
    taskService.createTask(
                            identityNumber,
                            companyName,
                            path + "\\E-H 实质性程序工作底稿（企业会计准则版）" + "\\HQ所得税费用.xlsx");
    return this.save(admin);
}


    private void updateLogin(Long id, String ip)
    {
        Admin update = new Admin();
        update.setId(id);
        update.setLoginIp(ip);
        update.setLoginTime(new Date());
        this.baseMapper.updateById(update);

        LoginLog log = new LoginLog();
        log.setAdminId(Integer.valueOf(id.toString()));
        log.setLoginIp(ip);
        loginLogService.save(log);
    }
}