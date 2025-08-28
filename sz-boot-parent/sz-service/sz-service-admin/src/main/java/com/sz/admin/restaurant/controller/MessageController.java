package com.sz.admin.restaurant.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.sz.admin.restaurant.pojo.dto.OrderReminderMessageDTO;
import com.sz.admin.restaurant.service.DineInOrdersService;
import com.sz.admin.restaurant.service.OrderReminderService;
import com.sz.admin.restaurant.service.OrdersService;
import com.sz.admin.restaurant.service.TakeawayOrdersService;
import com.sz.admin.system.pojo.po.SysMessage;
import com.sz.admin.system.pojo.po.SysUser;
import com.sz.admin.system.pojo.po.SysUserRole;
import com.sz.admin.system.pojo.vo.sysuser.SysUserVO;
import com.sz.admin.system.service.SysMessageService;
import com.sz.admin.system.service.SysMessageUserService;
import com.sz.admin.system.service.SysUserRoleService;
import com.sz.admin.system.service.SysUserService;
import com.sz.core.common.entity.ApiResult;
import com.sz.core.common.entity.LoginUser;
import com.sz.core.common.entity.SocketMessage;
import com.sz.core.common.entity.TransferMessage;
import com.sz.core.common.enums.CommonResponseEnum;
import com.sz.core.common.enums.SocketChannelEnum;
import com.sz.core.util.JsonUtils;
import com.sz.redis.WebsocketRedisService;
import com.sz.security.core.util.LoginUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.core.NestedExceptionUtils.buildMessage;

/**
 * <p>
 * 通知 Controller
 * </p>
 *
 * @author sz-admin
 * @since 2025-08-26
 */
@Tag(name = "通知")
@RestController
@RequestMapping("message")
@RequiredArgsConstructor
@Slf4j
public class MessageController {
    private final SysUserService sysUserService;
    private final OrdersService ordersService;
    private final WebsocketRedisService websocketRedisService;
    private final OrderReminderService orderReminderService;
    private final SysMessageUserService sysMessageUserService;
    private final SysMessageService sysMessageService;
    private final SysUserRoleService sysUserRoleService;

    /**
     * 服务员催单，向全体厨师发送消息
     *
     * @param orderId 订单ID
     * @return 操作结果
     */
    @Operation(summary = "服务员催单")
    @PostMapping("/reminder/{orderId}")
    public ApiResult<Void> orderReminder(@PathVariable Long orderId) {


        // 2. 获取当前登录用户信息
        LoginUser loginUser = LoginUtils.getLoginUser();
        Long userId = loginUser.getUserInfo().getId();

        // 3. 验证用户角色权限（role表中id=4为服务员）
        SysUserVO currentUser = sysUserService.getSysUserByUserId(userId);
        //boolean isWaiter = currentUser.getRoleIds() != null && currentUser.getRoleIds().contains("4");
        //CommonResponseEnum.INVALID_PERMISSION.assertTrue(!isWaiter);

        // 4. 检查是否可以催单（去重机制）
       CommonResponseEnum.DEBOUNCE.assertTrue(!orderReminderService.canRemind(orderId, userId));


        // 5. 查询订单信息
        com.sz.admin.restaurant.pojo.po.Orders order = ordersService.getById(orderId);
        CommonResponseEnum.INVALID_ID.assertNull(order);

        // 6. 构建催单消息
        String msg="厨师师傅，订单号为:"+order.getOrderNumber()+"催单，顾客赶时间，辛苦优先安排！";
        // 7. 查询所有厨师用户列表（role表中id=5）
        List<SysUserRole> allUserRole = sysUserRoleService.list();

        List<Object> receiverIds = new ArrayList<>();
        for (SysUserRole sysUserRole : allUserRole) {
            if(sysUserRole.getRoleId().equals(5L)){
                receiverIds.add(sysUserRole.getUserId());
            }
        }

        SysMessage message = new SysMessage();
        message.setMessageTypeCd("msg");
        message.setSenderId(userId);
        message.setTitle("催单通知!!!");
        message.setContent(msg);
        sysMessageService.save(message);
        sysMessageUserService.batchInsert(message.getId(), receiverIds);

        // 8. 构造WebSocket消息
        SocketMessage socketMessage = SocketMessage.builder()
                .channel(SocketChannelEnum.MESSAGE)
                .scope(com.sz.core.common.enums.MessageTransferScopeEnum.SOCKET_CLIENT)
                .data(JsonUtils.toJsonString(message))
                .build();

        TransferMessage transferMessage = TransferMessage.builder()
                .message(socketMessage)
                .toUsers(receiverIds)
                .build();

        // 9. 通过WebSocket服务向所有在线厨师推送催单消息
        websocketRedisService.sendServiceToWs(transferMessage);


        // 10. 记录催单操作
        orderReminderService.recordReminder(orderId, userId);


        return ApiResult.success();
    }

}
