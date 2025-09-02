package com.sz.mysql;

import cn.dev33.satoken.exception.NotWebContextException;
import cn.dev33.satoken.stp.StpUtil;
import com.mybatisflex.annotation.InsertListener;
import com.mybatisflex.annotation.SetListener;
import com.mybatisflex.annotation.UpdateListener;
import com.sz.core.common.entity.LoginUser;
import com.sz.security.core.util.LoginUtils;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 数据表变更事件处理器。
 * <p>
 * 约定：数据表必须包含以下字段：
 * <ul>
 * <li>`create_id` - int 类型，创建者 ID</li>
 * <li>`create_time` - datetime 类型，创建时间</li>
 * <li>`update_id` - int 类型，更新者 ID</li>
 * <li>`update_time` - datetime 类型，更新时间</li>
 * </ul>
 *
 * @since 2023-12-08
 * @version 1.0
 */
@Slf4j
public class EntityChangeListener implements InsertListener, UpdateListener, SetListener {

    @Override
    public void onInsert(Object o) {
        log.debug("EntityChangeListener.onInsert called for entity: {}", o.getClass().getSimpleName());
        setPropertyIfPresent(o, "createTime", LocalDateTime.now());
        setPropertyIfPresent(o, "updateTime", LocalDateTime.now());
        if (isNotLogin()) {
            log.debug("User is not logged in, skipping user ID setting for entity: {}", o.getClass().getSimpleName());
            return;
        }
        try {
            Long userId = StpUtil.getLoginIdAsLong();
            log.debug("Setting createId and updateId to {} for entity: {}", userId, o.getClass().getSimpleName());
            setPropertyIfPresent(o, "createId", userId);
            setPropertyIfPresent(o, "updateId", userId);
        } catch (Exception e) {
            // 访客请求时无法获取用户ID，忽略异常
            log.debug("Guest request, unable to get user ID: {}", e.getMessage());
        }
        try {
            LoginUser loginUser = LoginUtils.getLoginUser();
            if (loginUser != null) {
                List<Long> deptOptions = loginUser.getDepts();
                if (!deptOptions.isEmpty()) {
                    setPropertyIfPresent(o, "deptScope", deptOptions);
                }
            }
        } catch (Exception e) {
            // 访客请求时无法获取登录用户信息，忽略异常
            log.debug("Guest request, unable to get login user info: {}", e.getMessage());
        }
    }

    @Override
    public void onUpdate(Object o) {
        log.debug("EntityChangeListener.onUpdate called for entity: {}", o.getClass().getSimpleName());
        setPropertyIfPresent(o, "updateTime", LocalDateTime.now());
        if (isNotLogin()) {
            log.debug("User is not logged in, skipping updateId setting for entity: {}", o.getClass().getSimpleName());
            return;
        }
        try {
            Long userId = StpUtil.getLoginIdAsLong();
            log.debug("Setting updateId to {} for entity: {}", userId, o.getClass().getSimpleName());
            setPropertyIfPresent(o, "updateId", userId);
        } catch (Exception e) {
            // 访客请求时无法获取用户ID，忽略异常
            log.debug("Guest request, unable to get user ID: {}", e.getMessage());
        }
    }

    @Override
    public Object onSet(Object entity, String property, Object value) {
        return value;
    }

    private void setPropertyIfPresent(Object object, String propertyName, Object propertyValue) {
        try {
            Class<?> clazz = object.getClass();
            String setterName = "set" + Character.toUpperCase(propertyName.charAt(0)) + propertyName.substring(1);
            Method setterMethod = null;

            // 遍历所有方法，寻找匹配的 setter
            for (Method method : clazz.getDeclaredMethods()) {
                if (method.getName().equals(setterName) && method.getParameterCount() == 1) {
                    Class<?> parameterType = method.getParameterTypes()[0];
                    if (parameterType.isAssignableFrom(propertyValue.getClass())) {
                        setterMethod = method;
                        break;
                    }
                }
            }

            if (setterMethod != null) {
                setterMethod.invoke(object, propertyValue);
            } else {
                log.warn("Failed to set property '{}': Setter method not found or type mismatch.", propertyName);
            }
        } catch (IllegalAccessException | InvocationTargetException e) {
            log.warn(" Fill EntityChangeField failed; Error accessing property {}: {}", propertyName, e.getMessage());
        }
    }

    private boolean isNotLogin() {
        try {
            return !StpUtil.isLogin();
        } catch (NotWebContextException e) {
            // 处理非 Web 环境异常，返回未登录
            return true;
        } catch (Exception e) {
            // 记录所有其他异常，并返回未登录
            log.error("[EntityChangeListener] Unexpected error during login check: {}", e.getMessage(), e);
            return true;
        }
    }

}
