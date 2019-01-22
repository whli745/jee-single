package com.whli.jee.system.service.impl;

import com.whli.jee.core.util.CollectionUtils;
import com.whli.jee.core.util.StringUtils;
import com.whli.jee.core.web.dao.IBaseDao;
import com.whli.jee.core.web.entity.BaseTree;
import com.whli.jee.core.web.service.impl.BaseServiceImpl;
import com.whli.jee.system.dao.ISysMenuDao;
import com.whli.jee.system.dao.ISysRoleMenuDao;
import com.whli.jee.system.entity.SysMenu;
import com.whli.jee.system.entity.SysRoleMenu;
import com.whli.jee.system.service.ISysMenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author whli
 * @version 1.0
 * @since 1.0
 */
@Service("sysMenuService")
public class SysMenuServiceImpl extends BaseServiceImpl<SysMenu> implements ISysMenuService {

    @Autowired
    private ISysMenuDao sysMenuDao;
    @Autowired
    private ISysRoleMenuDao sysRoleMenuDao;

    @Override
    public IBaseDao<SysMenu> getDao() {
        return sysMenuDao;
    }

    /**
     * 根据登录用户查询菜单
     *
     * @param userId
     * @param parentId
     * @return
     */
    public List<SysMenu> findMenusByUserId(String userId, String parentId) {
        List<SysMenu> menus = null;
        if (StringUtils.isNotNullOrBlank(userId)) {
            menus = sysMenuDao.findMenusByUserId(userId, parentId);
            addChild(userId, menus);
        }
        return menus;
    }

    private void addChild(String userId, List<SysMenu> parents) {
        for (int i = 0, m = parents.size(); i < m; i++) {
            List<SysMenu> childrens = sysMenuDao.findMenusByUserId(userId, parents.get(i).getId());
            if (CollectionUtils.isNotNullOrEmpty(childrens)) {
                parents.get(i).setMenus(childrens);
                addChild(userId, childrens);
            }
        }
    }

    /**
     * 查询登录用户的按钮
     * @param userId
     * @param parentUrl
     * @return
     */
    @Override
    public List<SysMenu> findButtonsByParentUrlAndUserId(String userId, String parentUrl) {
        List<SysMenu> menus = null;
        if (StringUtils.isNotNullOrBlank(userId)) {
            menus = sysMenuDao.findButtonsByParentUrlAndUserId(userId, parentUrl);
        }
        return menus;
    }

    /**
     * 以树结构展示菜单
     * @param roleId
     * @return
     */
    @Override
    public List<BaseTree> findByTree(String roleId) {
        List<BaseTree> trees = sysMenuDao.findByTree("");
        if (CollectionUtils.isNotNullOrEmpty(trees)) {
            for (BaseTree tree : trees) {
                addChildrenNode(tree,roleId);
            }
        }
        return trees;
    }

    /**
     * 解析父菜单的子项
     * @param parent
     * @param roleId
     */
    private void addChildrenNode(BaseTree parent,String roleId){
        parent.setOpen(true);
        if(StringUtils.isNotNullOrBlank(roleId)){
            SysRoleMenu roleMenu = sysRoleMenuDao.findByRoleIdAndMenuId(roleId,parent.getId());
            if (roleMenu != null){
                parent.setChecked(true);
            }
        }

        List<BaseTree> childTrees = sysMenuDao.findByTree(parent.getId());
        if (CollectionUtils.isNotNullOrEmpty(childTrees)) {
            parent.setIsParent(true);
            for (BaseTree childTree : childTrees){
                addChildrenNode(childTree,  roleId);
                parent.getChildren().add(childTree);
            }
        }

    }

    /**
     * 查询同级别下是否存在相同序号
     * @param entity
     * @return
     */
    @Override
    public SysMenu findByParentIdAndSort(SysMenu entity){
        return sysMenuDao.findByParentIdAndSort(entity);
    }
}
