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
    private final DineInOrdersService dineInOrdersService;
    private final TakeawayOrdersService takeawayOrdersService;
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
        // 1. 验证用户是否登录
        //CommonResponseEnum.UNAUTHORIZED.assertTrue(!StpUtil.isLogin());
        log.info("服务员催单请求，订单ID: {}", orderId);

        // 2. 获取当前登录用户信息
        LoginUser loginUser = LoginUtils.getLoginUser();
        Long userId = loginUser.getUserInfo().getId();
        log.info("当前登录用户ID: {}", userId);

        // 3. 验证用户角色权限（role表中id=4为服务员）
        SysUserVO currentUser = sysUserService.getSysUserByUserId(userId);
        //boolean isWaiter = currentUser.getRoleIds() != null && currentUser.getRoleIds().contains("4");
        //CommonResponseEnum.INVALID_PERMISSION.assertTrue(!isWaiter);

        // 4. 检查是否可以催单（去重机制）
       // CommonResponseEnum.DEBOUNCE.assertTrue(!orderReminderService.canRemind(orderId, userId));
        //log.info("通过去重检查，可以进行催单操作");

        // 5. 查询订单信息
        com.sz.admin.restaurant.pojo.po.Orders order = ordersService.getById(orderId);
        CommonResponseEnum.INVALID_ID.assertNull(order);

        // 6. 构建催单消息
        OrderReminderMessageDTO reminderMessage = buildReminderMessage(order);
        String msg="厨师师傅，订单号为:"+order.getOrderNumber()+"催单，顾客赶时间，辛苦优先安排！";
        // 7. 查询所有厨师用户列表（role表中id=5）
        List<SysUserRole>  c= sysUserRoleService.list();
        System.out.println(c);

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
        message.setTitle("催单处理!!!");
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
        log.info("已向WebSocket服务发送催单消息，订单ID: {}", orderId);

        // 10. 记录催单操作
        orderReminderService.recordReminder(orderId, userId);

        // 11. 记录催单日志
        log.info("服务员 [{}] 对订单 [{}] 进行了催单操作", userId, orderId);

        return ApiResult.success();
    }

    /**
     * 构建催单消息
     *
     * @param order 订单信息
     * @return 催单消息DTO
     */
    private OrderReminderMessageDTO buildReminderMessage(com.sz.admin.restaurant.pojo.po.Orders order) {
        OrderReminderMessageDTO reminderMessage = new OrderReminderMessageDTO();
        reminderMessage.setOrderId(order.getOrderId());
        reminderMessage.setOrderNumber(order.getOrderNumber());
        reminderMessage.setOrderType(order.getOrderType());
        reminderMessage.setTotalAmount(order.getTotalAmount());
        reminderMessage.setReminderTime(LocalDateTime.now());

        // 根据订单类型获取额外信息
        if ("堂食".equals(order.getOrderType())) {
            // 获取堂食订单信息
            com.sz.admin.restaurant.pojo.vo.DineInOrdersVO dineInOrder = dineInOrdersService.detail(order.getOrderId());
            if (dineInOrder != null) {
                reminderMessage.setTableNumber(dineInOrder.getTableId().toString());
            }
        } else if ("外卖".equals(order.getOrderType())) {
            // 获取外卖订单信息
            com.sz.admin.restaurant.pojo.vo.TakeawayOrdersVO takeawayOrder = takeawayOrdersService.detail(order.getOrderId());
            if (takeawayOrder != null) {
                reminderMessage.setCustomerName(takeawayOrder.getCustomerName());
                reminderMessage.setCustomerPhone(takeawayOrder.getCustomerPhone());
            }
        }

        return reminderMessage;
    }
}
