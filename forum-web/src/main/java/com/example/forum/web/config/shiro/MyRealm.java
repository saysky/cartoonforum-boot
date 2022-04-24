package com.example.forum.web.config.shiro;

import cn.hutool.core.lang.Validator;
import com.example.forum.core.sys.entity.Permission;
import com.example.forum.core.sys.entity.Role;
import com.example.forum.core.sys.entity.User;
import com.example.forum.core.sys.service.PermissionService;
import com.example.forum.core.sys.service.RoleService;
import com.example.forum.core.sys.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
public class MyRealm extends AuthorizingRealm {

    @Autowired
    @Lazy
    private UserService userService;

    @Autowired
    @Lazy
    private RoleService roleService;

    @Autowired
    @Lazy
    private PermissionService permissionService;


    /**
     * 认证信息(身份验证) Authentication 是用来验证用户身份
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        log.info("认证-->MyShiroRealm.doGetAuthenticationInfo()");
        //1.验证用户名
        User user;
        String account = (String) token.getPrincipal();
        if (Validator.isEmail(account)) {
            user = userService.findByEmail(account);
        } else {
            user = userService.findByUserName(account);
        }
        if (user == null) {
            //用户不存在
            log.info("用户不存在! 登录名:{}, 密码:{}", account, token.getCredentials());
            return null;
        }
        Role role = roleService.findByUserId(user.getId());
        if (role != null) {
            user.setRoleCode(role.getCode());
        }



        //4.封装authenticationInfo，准备验证密码
        SimpleAuthenticationInfo authenticationInfo = new SimpleAuthenticationInfo(
                user, // 用户名
                user.getUserPass(), // 密码
                getName() // realm name
        );
        System.out.println("realName:" + getName());
        return authenticationInfo;
    }


    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {

        SimpleAuthorizationInfo authorizationInfo = new SimpleAuthorizationInfo();
        User user = (User) principals.getPrimaryPrincipal();

        Role role = roleService.findByUserId(user.getId());

        authorizationInfo.addRole(role.getCode());
        List<Permission> permissions = permissionService.listPermissionsByRoleId(role.getId());
        //把权限的URL全部放到authorizationInfo中去
        Set<String> urls = permissions.stream().map(p -> p.getUrl()).collect(Collectors.toSet());
        authorizationInfo.addStringPermissions(urls);

        return authorizationInfo;
    }
}
