package com.whli.jee.system.realm;

import com.whli.jee.system.entity.SysUser;
import com.whli.jee.system.service.ISysUserService;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * <p>类或方法描述</p>
 *
 * @author whli
 * @date 2019/1/18 10:46
 */
public class MyShiroRealm extends AuthorizingRealm {

    @Autowired
    private ISysUserService userService;

    /**
     * 权限分配
     * @param principalCollection
     * @return
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        return null;
    }

    /**
     * 身份验证
     * @param authenticationToken
     * @return
     * @throws AuthenticationException
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        UsernamePasswordToken token = (UsernamePasswordToken) authenticationToken;
        String loginName = token.getUsername();
        SysUser loginUser = userService.findSysUserByLoginName(loginName);
        if (loginUser == null){
            return null;
        }

        if (loginUser.getEnable() == 0){
            throw new DisabledAccountException();
        }

        SimpleAuthenticationInfo authenticationInfo = new SimpleAuthenticationInfo(loginUser,
                loginUser.getPassword().toCharArray(),
                getName());
        return authenticationInfo;
    }
}
