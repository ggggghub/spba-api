package com.example.spba.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.spba.domain.entity.Menu;

import java.util.HashMap;
import java.util.List;

public interface MenuService  extends IService<Menu>
{

    /**
     * 获取菜单列表
     * @param params
     * @return
     */
    List<HashMap> getAll(HashMap params);

    /**
     * 检测参数
     * @param menu
     * @return
     */
    HashMap checkParams(Menu menu);
}