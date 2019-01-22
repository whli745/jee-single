package com.whli.jee.system.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionLikeType;
import com.whli.jee.core.constant.SysConstants;
import com.whli.jee.core.exception.BusinessException;
import com.whli.jee.core.util.*;
import com.whli.jee.core.web.dao.IBaseDao;
import com.whli.jee.core.web.service.impl.BaseServiceImpl;
import com.whli.jee.system.dao.ISysUserDao;
import com.whli.jee.system.dao.ISysUserRoleDao;
import com.whli.jee.system.entity.SysRole;
import com.whli.jee.system.entity.SysUser;
import com.whli.jee.system.entity.SysUserRole;
import com.whli.jee.system.service.ISysRoleService;
import com.whli.jee.system.service.ISysUserService;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author whli
 * @version 1.0
 * @since 1.0
 */
@Service(value = "sysUserService")
public class SysUserServiceImpl extends BaseServiceImpl<SysUser> implements ISysUserService {

    @Autowired
    private ISysUserDao sysUserDao;
    @Autowired
    private ISysUserRoleDao sysUserRoleDao;
    @Autowired
    private ISysRoleService sysRoleService;
    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public IBaseDao<SysUser> getDao() {
        return sysUserDao;
    }

    //@Transactional
    @Override
    public int add(SysUser entity) {
        if (StringUtils.isNullOrBlank(entity.getLoginName()) || StringUtils.isNullOrBlank(entity.getEmail())
                || StringUtils.isNullOrBlank(entity.getPhone())){
            throw new BusinessException("用户名、邮箱、联系方式不能为空！");
        }

        SysUser temp = findSysUserByLoginName(entity.getLoginName());
        if (BeanUtils.isNotNull(temp)) {
            throw new BusinessException("【"+entity.getLoginName()+"】该用户已存在！");
        }

        temp = findSysUserByEmail(entity.getEmail());
        if (BeanUtils.isNotNull(temp)) {
            throw new BusinessException("【"+entity.getEmail()+"】邮箱已被其他用户绑定！");
        }

        temp = findSysUserByPhone(entity.getPhone());
        if (BeanUtils.isNotNull(temp)) {
            throw new BusinessException( "【"+entity.getPhone()+"】联系方式已被其他用户绑定！");
        }

        entity.setEnable(1);
        entity.setPassword(PwdUtils.md5Encode("123456", entity.getLoginName()));

        return super.add(entity);
    }

    @Override
    public int update(SysUser entity) {
        if (StringUtils.isNotNullOrBlank(entity.getPassword())){
            entity.setPassword(PwdUtils.md5Encode(entity.getPassword(), entity.getLoginName()));
        }
        return super.update(entity);
    }

    /**
     * @Desc 用户登录
     * @Author whli
     * @Version 1.0
     * @Date 2018/6/3 0:20
     * @Params [loginName, password]
     * @Return java.lang.String
     */
    @Override
    public SysUser login(String loginName, String password) {
        if (StringUtils.isNullOrBlank(loginName) || StringUtils.isNullOrBlank(password)){
            throw new BusinessException("用户名或密码不能为空！");
        }

        try {
            Subject subject = SecurityUtils.getSubject();
            UsernamePasswordToken usernamePasswordToken = new UsernamePasswordToken(loginName,
                    PwdUtils.md5Encode(password,loginName));
            subject.login(usernamePasswordToken);
            SysUser sysUser = (SysUser) subject.getPrincipal();
            String token = subject.getSession().getId().toString();
            System.out.println("token == "+token);
            sysUser.setToken(token);
            sysUser.setPassword("");
            //将用户名和ID放入Redis
            JedisUtils.hSet(token, SysConstants.LOGIN_NAME, sysUser.getLoginName());
            JedisUtils.hSet(token, SysConstants.LOGIN_USERID, sysUser.getId());
            JedisUtils.expireDefault(token);
            return sysUser;
        } catch (DisabledAccountException e){
            throw new BusinessException("用户禁止登录或已删除！");
        } catch (AccountException e){
            throw new BusinessException("用户名或密码不正确！");
        } catch (CredentialsException e){
            throw new BusinessException("用户名或密码不正确！");
        } catch (AuthenticationException e) {
            e.printStackTrace();
            throw new BusinessException("用户名或密码不正确！");
        }
    }

    /**
     * @Desc 用户退出登录
     * @Author whli
     * @Version 1.0
     * @Date 2018/6/3 0:20
     * @Params [token]
     * @Return java.lang.String
     */
    @Override
    public void logout(HttpServletRequest request) {
        Subject subject = SecurityUtils.getSubject();
        subject.logout();
    }

    /**
     * 根据登录名检查用户是否存在
     *
     * @param loginName
     * @return
     */
    @Override
    public SysUser findSysUserByLoginName(String loginName) {
        SysUser currentUser = null;
        if (StringUtils.isNotNullOrBlank(loginName)) {
            currentUser = sysUserDao.findSysUserByLoginName(loginName);
        }
        return currentUser;
    }

    @Override
    public SysUser findSysUserByEmail(String email) {
        SysUser currentUser = null;
        if (StringUtils.isNotNullOrBlank(email)) {
            currentUser = sysUserDao.findSysUserByEmail(email);
        }
        return currentUser;
    }

    @Override
    public SysUser findSysUserByPhone(String phone) {
        SysUser currentUser = null;
        if (StringUtils.isNotNullOrBlank(phone)) {
            currentUser = sysUserDao.findSysUserByPhone(phone);
        }
        return currentUser;
    }

    @Override
    public int grantByUser(String userId, List<String> roleIds) {

        if (StringUtils.isNullOrBlank(userId)){
            throw new BusinessException("请选择需要授权的用户！");
        }

        int rows = 0;
        if (CollectionUtils.isNotNullOrEmpty(roleIds)) {
            List<SysUserRole> userRoles = new ArrayList<SysUserRole>();
            for (String roleId : roleIds) {
                SysUserRole userRole = new SysUserRole();
                userRole.setRoleId(roleId);
                userRole.setUserId(userId);
                List<SysUserRole> sysUserRoles = sysUserRoleDao.findAll(userRole);
                if (CollectionUtils.isNotNullOrEmpty(sysUserRoles)){
                    SysRole role = sysRoleService.findByPK(roleId);
                    throw new BusinessException("角色【"+role.getName()+"】已存在！");
                }
                userRoles.add(userRole);
                sysUserRoleDao.add(userRole);
                rows++;
            }
        }
        return rows;
    }

    @Override
    public int importExcel(File file) {
        return super.importExcel(file);
    }

    @Override
    public int resetPassword(SysUser entity) {
        int rows = 0;
        if (CollectionUtils.isNullOrEmpty(entity.getIds())){
            throw new BusinessException("请选择需要重置密码的用户！");
        }
        for (String id : entity.getIds()){
            SysUser user = findByPK(id);
            sysUserDao.resetPassword(id,PwdUtils.md5Encode("123456",user.getLoginName()));
            rows++;
        }

        return rows;
    }

    //@Transactional
    @Override
    public int importExcel(InputStream stream) {
        int rows = 0;
        try {
            List<SysUser> users = ExcelUtils.importExcel(stream,SysUser.class,new String[]{"loginName",
                    "no","name","email","phone"});
            if (CollectionUtils.isNotNullOrEmpty(users)){
                for (SysUser entity : users){
                    rows += this.add(entity);
                }

            }
            return rows;
        } catch (Exception e) {
            throw new BusinessException("导入用户错误："+e.getMessage());
        }
    }

    @Override
    public void exportTemplate(SysUser entity, HttpServletResponse response) {
        try {
            ExcelUtils.exportExcel(response, new BaseExcel() {
                @Override
                public String fileName() {
                    return "用户模板";
                }

                @Override
                public LinkedHashMap<String, String> headers() {
                    LinkedHashMap<String,String> headers = new LinkedHashMap<String, String>();
                    headers.put("loginName","用户名");
                    headers.put("no","工号");
                    headers.put("name","用户姓名");
                    headers.put("email","邮件");
                    headers.put("phone","联系方式");
                    return headers;
                }

                @Override
                public List<Map<String, Object>> datas() {
                    SysUser user = new SysUser();
                    user.setLoginName("whli");
                    user.setEmail("914164909@qq.com");
                    user.setPhone("13000000000");
                    List<SysUser> lines = new ArrayList<SysUser>();
                    lines.add(user);
                    try {
                        CollectionLikeType type = objectMapper.getTypeFactory().constructCollectionLikeType(List.class,Map.class);
                        return objectMapper.readValue(objectMapper.writeValueAsString(lines),type);
                    } catch (IOException e) {
                        throw new BusinessException(e.getMessage());
                    }
                }
            });
        } catch (Exception e) {
            throw new BusinessException("导出用户模板错误："+e.getMessage());
        }
    }
}
