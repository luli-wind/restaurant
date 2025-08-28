package com.sz.admin.system.service.impl;

import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.core.update.UpdateChain;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.sz.admin.restaurant.pojo.po.OrderDetail;
import com.sz.admin.restaurant.pojo.po.Orders;
import com.sz.admin.system.mapper.SysMessageMapper;
import com.sz.admin.system.pojo.dto.sysmessage.Message;
import com.sz.admin.system.pojo.dto.sysmessage.PayloadBody;
import com.sz.admin.system.pojo.dto.sysmessage.SysMessageListDTO;
import com.sz.admin.system.pojo.po.SysMessage;
import com.sz.admin.system.pojo.po.SysMessageUser;
import com.sz.admin.system.pojo.po.SysUserRole;
import com.sz.admin.system.pojo.vo.sysmessage.MessageCountVO;
import com.sz.admin.system.pojo.vo.sysmessage.SysMessageVO;
import com.sz.admin.system.service.SysMessageService;
import com.sz.admin.system.service.SysMessageUserService;
import com.sz.admin.system.service.SysUserRoleService;
import com.sz.core.common.entity.LoginUser;
import com.sz.core.common.entity.PageResult;
import com.sz.core.common.entity.SocketMessage;
import com.sz.core.common.entity.TransferMessage;
import com.sz.core.common.enums.CommonResponseEnum;
import com.sz.core.common.enums.MessageTransferScopeEnum;
import com.sz.core.common.enums.SocketChannelEnum;
import com.sz.core.util.BeanCopyUtils;
import com.sz.core.util.JsonUtils;
import com.sz.core.util.PageUtils;
import com.sz.core.util.Utils;
import com.sz.redis.WebsocketRedisService;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.sz.security.core.util.LoginUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static com.sz.admin.system.pojo.po.table.SysMessageTableDef.SYS_MESSAGE;
import static com.sz.admin.system.pojo.po.table.SysMessageUserTableDef.SYS_MESSAGE_USER;

/**
 * <p>
 * 消息管理 服务实现类
 * </p>
 *
 * @author sz-admin
 * @since 2025-04-21
 */
@Service
@RequiredArgsConstructor
public class SysMessageServiceImpl extends ServiceImpl<SysMessageMapper, SysMessage> implements SysMessageService {

    private final SysMessageUserService sysMessageUserService;

    private final WebsocketRedisService websocketRedisService;

    private final SysUserRoleService sysUserRoleService;

    @Override
    public void create(Message dto) {
        SysMessage sysMessage = BeanCopyUtils.copy(dto, SysMessage.class);
        save(sysMessage);
        Long messageId = sysMessage.getId();
        List<Object> receiverIds = dto.getReceiverIds();
        sysMessageUserService.batchInsert(messageId, receiverIds);

        PayloadBody body = new PayloadBody();
        body.setTitle(dto.getTitle());
        body.setContent(dto.getContent());
        SocketMessage message = SocketMessage.builder().data(JsonUtils.toJsonString(body)).channel(SocketChannelEnum.MESSAGE)
                .scope(MessageTransferScopeEnum.SOCKET_CLIENT).build();
        TransferMessage msg = TransferMessage.builder().message(message).fromUser(dto.getSenderId().toString()).toPushAll(false).toUsers(dto.getReceiverIds())
                .build();
        websocketRedisService.sendServiceToWs(msg);
    }

    @Override
    public PageResult<SysMessageVO> page(SysMessageListDTO dto) {
        Page<SysMessageVO> page = pageAs(PageUtils.getPage(dto), buildQueryWrapper(dto), SysMessageVO.class);
        return PageUtils.getPageResult(page);
    }

    @Override
    public List<SysMessageVO> list(SysMessageListDTO dto) {
        QueryWrapper queryWrapper = buildQueryWrapper(dto);
        queryWrapper.where(SYS_MESSAGE_USER.IS_READ.eq("F"));
        return listAs(queryWrapper, SysMessageVO.class);
    }

    @Override
    public SysMessageVO detail(Object id) {
        SysMessage sysMessage = getById((Serializable) id);
        Long userId = Objects.requireNonNull(LoginUtils.getLoginUser()).getUserInfo().getId();
        CommonResponseEnum.INVALID_ID.assertNull(sysMessage);
        SysMessageVO messageVO = BeanCopyUtils.copy(sysMessage, SysMessageVO.class);
        String isRead = sysMessageUserService.getIsRead(sysMessage.getId(), userId);
        messageVO.setIsRead(isRead);
        read(Utils.getLongVal(id));
        return messageVO;
    }

    @Override
    public MessageCountVO countMyUnreadMessages() {
        long all = count(buildQueryWrapper(new SysMessageListDTO("")).where(SYS_MESSAGE_USER.IS_READ.eq("F")));
        long todo = count(buildQueryWrapper(new SysMessageListDTO("todo")).where(SYS_MESSAGE_USER.IS_READ.eq("F")));
        long msg = count(buildQueryWrapper(new SysMessageListDTO("msg")).where(SYS_MESSAGE_USER.IS_READ.eq("F")));
        return new MessageCountVO(all, todo, msg);
    }

    @Override
    public void sendInventoryAlert() {
        LoginUser loginUser = LoginUtils.getLoginUser();
        Long userId = loginUser.getUserInfo().getId();
        Message message =new Message();
        message.setMessageTypeCd("msg");
        message.setSenderId(userId);
        message.setTitle("库存警告!!!");
        message.setContent("部分库存即将不足，请及时联系供应商，补充库存");
        List receiverIds =new ArrayList<>();
        receiverIds.add("1");
        message.setReceiverIds(receiverIds);
        create(message);
    }

    @Override
    public void sendInventoryInsufficient(List<OrderDetail> orderDetailList,Orders orders) {
        LoginUser loginUser = LoginUtils.getLoginUser();
        Long userId = loginUser.getUserInfo().getId();
        Message message =new Message();
        message.setMessageTypeCd("msg");
        message.setSenderId(userId);
        message.setTitle("库存不足，无法完成订单!!!");
        StringBuilder msg = new StringBuilder();
        msg.append("订单号为:"+orders.getOrderNumber()+"，以下菜品:");
        for (OrderDetail orderDetail : orderDetailList) {
            msg.append(orderDetail.getDishName()+" ");
        }
        msg.append(",库存不足，无法制作，请向客户致歉，请客户更换订单。谢谢配合工作");
        message.setContent(msg.toString());
        List<SysUserRole> allUserRole = sysUserRoleService.list();

        List<Object> receiverIds = new ArrayList<>();
        for (SysUserRole sysUserRole : allUserRole) {
            if(sysUserRole.getRoleId().equals(4L)){
                receiverIds.add(sysUserRole.getUserId());
            }
        }
        receiverIds.add("1");//添加管理员
        message.setReceiverIds(receiverIds);
        create(message);
    }

    private static QueryWrapper buildQueryWrapper(SysMessageListDTO dto) {
        Long userId = Objects.requireNonNull(LoginUtils.getLoginUser()).getUserInfo().getId();
        QueryWrapper wrapper = QueryWrapper.create()
                .select(SYS_MESSAGE.ID, SYS_MESSAGE.MESSAGE_TYPE_CD, SYS_MESSAGE.SENDER_ID, SYS_MESSAGE.TITLE, SYS_MESSAGE.CONTENT, SYS_MESSAGE.CREATE_TIME,
                        SYS_MESSAGE_USER.IS_READ)
                .from(SYS_MESSAGE).join(SYS_MESSAGE_USER).on(SYS_MESSAGE_USER.MESSAGE_ID.eq(SYS_MESSAGE.ID)).where(SYS_MESSAGE_USER.RECEIVER_ID.eq(userId))
                .where(SYS_MESSAGE.MESSAGE_TYPE_CD.eq(dto.getMessageTypeCd()).when(Utils.isNotNull(dto.getMessageTypeCd())));
        if (Utils.isNotNull(dto.getReadType())) {
            if ("read".equals(dto.getReadType())) {
                wrapper.where(SYS_MESSAGE_USER.IS_READ.eq("T"));
            } else if ("unread".equals(dto.getReadType())) {
                wrapper.where(SYS_MESSAGE_USER.IS_READ.eq("F"));
            }
        }
        wrapper.orderBy(SYS_MESSAGE.CREATE_TIME.desc());
        return wrapper;
    }

    public void read(Long messageId) {
        Long userId = Objects.requireNonNull(LoginUtils.getLoginUser()).getUserInfo().getId();
        UpdateChain.of(SysMessageUser.class).set(SYS_MESSAGE_USER.IS_READ, "T").set(SYS_MESSAGE_USER.READ_TIME, LocalDateTime.now())
                .where(SYS_MESSAGE_USER.MESSAGE_ID.eq(messageId)).where(SYS_MESSAGE_USER.RECEIVER_ID.eq(userId)).update();
        SocketMessage message = SocketMessage.builder().data(null).channel(SocketChannelEnum.READ).scope(MessageTransferScopeEnum.SOCKET_CLIENT).build();
        TransferMessage msg = new TransferMessage();
        msg.setMessage(message);
        msg.setFromUser(userId.toString());
        msg.setToPushAll(false);
        msg.setToUsers(List.of(userId.toString()));
        websocketRedisService.sendServiceToWs(msg);
    }

}
