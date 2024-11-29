package ltd.newbee.mall.controller.mall;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.AlipayConfig;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;

import ltd.newbee.mall.config.MallConfig;
import ltd.newbee.mall.entity.Order;
import ltd.newbee.mall.entity.OrderItem;
import ltd.newbee.mall.entity.vo.*;

import ltd.newbee.mall.service.OrderItemService;
import ltd.newbee.mall.service.OrderService;
import ltd.newbee.mall.service.ShopCatService;
import ltd.newbee.mall.util.security.Md5Utils;


import ltd.newbee.mall.util.constant.Constants;
import ltd.newbee.mall.controller.base.BaseController;

import ltd.newbee.mall.util.enums.OrderStatusEnum;
import ltd.newbee.mall.util.enums.PayStatusEnum;
import ltd.newbee.mall.util.enums.PayTypeEnum;
import ltd.newbee.mall.exception.BusinessException;
import ltd.newbee.mall.util.redis.RedisCache;
import ltd.newbee.mall.util.task.OrderUnPaidTask;
import ltd.newbee.mall.util.task.TaskService;
import ltd.newbee.mall.util.MyBeanUtil;
import ltd.newbee.mall.util.R;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

@Slf4j
@Controller
public class MallOrderController extends BaseController {
    @Autowired
    private ShopCatService shopCatService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private OrderItemService orderItemService;
    @Autowired
    private TaskService taskService;

    @Autowired
    private RedisCache redisCache;

    @Autowired
    private MallConfig mallConfig;

    @ResponseBody
    @GetMapping("saveOrder")
    public R saveOrder(Long couponUserId, HttpSession session) {
        MallUserVO mallUserVO = (MallUserVO) session.getAttribute(Constants.MALL_USER_SESSION_KEY);
        List<ShopCatVO> shopcatVOList = shopCatService.getShopCatVOList(mallUserVO.getUserId());
        // 购物车中无数据则跳转至错误页
        if (CollectionUtils.isEmpty(shopcatVOList)) {
            throw new BusinessException("购物车中无数据");
        }
        String orderNo = orderService.saveOrder(mallUserVO, couponUserId, shopcatVOList);
        return R.success().add("orderNo", orderNo);
    }


    @ResponseBody
    @GetMapping("/saveOrder/result/{orderNo}")
    public R saveOrderResult(@PathVariable("orderNo") String orderNo) {
        String result = redisCache.getCacheObject(Constants.SAVE_ORDER_RESULT_KEY + orderNo);
        if (!Constants.SUCCESS.equals(result)) {
            throw new BusinessException(result == null ? Constants.ERROR : result);
        }
        return R.success();
    }

    @GetMapping("/orders/{orderNo}")
    public String orderDetailPage(HttpServletRequest request, @PathVariable("orderNo") String orderNo, HttpSession session) {
        MallUserVO mallUserVO = (MallUserVO) session.getAttribute(Constants.MALL_USER_SESSION_KEY);
        LambdaQueryWrapper<Order> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.eq(Order::getOrderNo, orderNo);
        Order order = orderService.getOne(queryWrapper);
        if (order == null || !order.getUserId().equals(mallUserVO.getUserId())) {
            throw new BusinessException("订单项异常");
        }
        return renderOrderDetail(request, order);
    }


    @GetMapping("/orders")
    public String orderListPage(HttpServletRequest request, HttpSession session) {
        Page<OrderListVO> page = getPage(request, Constants.ORDER_SEARCH_PAGE_LIMIT);
        MallUserVO mallUserVO = (MallUserVO) session.getAttribute(Constants.MALL_USER_SESSION_KEY);
        Order order = new Order();
        order.setUserId(mallUserVO.getUserId());
        IPage<OrderListVO> iPage = orderService.selectMyOrderPage(page, order);
        List<OrderListVO> orderListVOS = iPage.getRecords();
        for (OrderListVO orderListVO : orderListVOS) {
            orderListVO.setOrderStatusString(OrderStatusEnum.getOrderStatusEnumByStatus(orderListVO.getOrderStatus()).getName());
        }
        List<Long> longs = orderListVOS.stream().map(OrderListVO::getOrderId).collect(Collectors.toList());
        List<OrderItem> orderItems = orderItemService.list(new QueryWrapper<OrderItem>().in(CollectionUtils.isNotEmpty(longs), "order_id", longs));
        Map<Long, List<OrderItem>> longListMap = orderItems.stream().collect(Collectors.groupingBy(OrderItem::getOrderId));
        for (OrderListVO orderListVO : orderListVOS) {
            if (longListMap.containsKey(orderListVO.getOrderId())) {
                List<OrderItem> orderItemList = longListMap.get(orderListVO.getOrderId());
                List<OrderItemVO> itemVOList = MyBeanUtil.copyList(orderItemList, OrderItemVO.class);
                orderListVO.setNewBeeMallOrderItemVOS(itemVOList);
            }
        }
        request.setAttribute("orderPageResult", iPage);
        request.setAttribute("path", "orders");
        return "mall/my-orders";
    }


    @PutMapping("/orders/{orderNo}/cancel")
    @ResponseBody
    public R cancelOrder(@PathVariable("orderNo") String orderNo, HttpSession session) {
        MallUserVO mallUserVO = (MallUserVO) session.getAttribute(Constants.MALL_USER_SESSION_KEY);
        Order order = judgeOrderUserId(orderNo, mallUserVO.getUserId());
        // 判断订单状态
        if (order.getOrderStatus() != OrderStatusEnum.OREDER_PAID.getOrderStatus()
                || order.getPayStatus() != PayStatusEnum.PAY_SUCCESS.getPayStatus()) {
            throw new BusinessException("订单无法关闭");
        }
        order.setOrderStatus((byte) OrderStatusEnum.ORDER_CLOSED_BY_MALLUSER.getOrderStatus());
        return R.result(orderService.updateById(order), "订单状态修改异常");
    }

    @PutMapping("/orders/{orderNo}/finish")
    @ResponseBody
    public R finishOrder(@PathVariable("orderNo") String orderNo, HttpSession session) {
        MallUserVO mallUserVO = (MallUserVO) session.getAttribute(Constants.MALL_USER_SESSION_KEY);
        Order order = judgeOrderUserId(orderNo, mallUserVO.getUserId());
        // 判断订单状态
        if (order.getOrderStatus() != OrderStatusEnum.OREDER_EXPRESS.getOrderStatus()
                || order.getPayStatus() != PayStatusEnum.PAY_SUCCESS.getPayStatus()) {
            throw new BusinessException("订单结算异常");
        }
        order.setOrderStatus((byte) OrderStatusEnum.ORDER_SUCCESS.getOrderStatus());
        return R.result(orderService.updateById(order), "订单状态更新异常");
    }

    @GetMapping("/selectPayType")
    public String selectPayType(HttpServletRequest request, @RequestParam("orderNo") String orderNo, HttpSession session) {
        MallUserVO mallUserVO = (MallUserVO) session.getAttribute(Constants.MALL_USER_SESSION_KEY);
        Order order = judgeOrderUserId(orderNo, mallUserVO.getUserId());
        // 判断订单状态
        if (order.getOrderStatus() != OrderStatusEnum.ORDER_PRE_PAY.getOrderStatus()
                || order.getPayStatus() != PayStatusEnum.PAY_ING.getPayStatus()) {
            throw new BusinessException("订单结算异常");
        }
        request.setAttribute("orderNo", orderNo);
        request.setAttribute("totalPrice", order.getTotalPrice());
        return "mall/pay-select";
    }

    @GetMapping("/payPage")
    public String payOrder(HttpServletRequest request, @RequestParam("orderNo") String orderNo, HttpSession session, @RequestParam("payType") int payType) throws UnsupportedEncodingException {
        MallUserVO mallUserVO = (MallUserVO) session.getAttribute(Constants.MALL_USER_SESSION_KEY);
        Order order = judgeOrderUserId(orderNo, mallUserVO.getUserId());
        request.setAttribute("orderNo", orderNo);
        request.setAttribute("totalPrice", order.getTotalPrice());
            return "mall/wxpay";
    }

    @RequestMapping(value = "/paySuccess", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public R paySuccess(Byte payType, String orderNo) {
        log.info("微信paySuccess通知数据记录：orderNo: {}, payType：{}", orderNo, payType);
        MallUserVO mallUserVO = (MallUserVO) session.getAttribute(Constants.MALL_USER_SESSION_KEY);
        Order order = judgeOrderUserId(orderNo, mallUserVO.getUserId());
        order.setOrderStatus((byte) OrderStatusEnum.OREDER_PAID.getOrderStatus());
        order.setPayType(payType);
        order.setPayStatus((byte) PayStatusEnum.PAY_SUCCESS.getPayStatus());
        order.setPayTime(new Date());
        order.setUpdateTime(new Date());
        if (!orderService.updateById(order)) {
            throw new BusinessException("订单状态更新异常！");
        }
        // 支付成功清空订单支付超期任务
        taskService.removeTask(new OrderUnPaidTask(order.getOrderId()));
        return R.success();
    }

    @GetMapping("/returnOrders/{orderNo}/{userId}")
    public String returnOrderDetailPage(HttpServletRequest request, @PathVariable String orderNo, @PathVariable Long userId) {
        log.info("微信return通知数据记录：orderNo: {}, 当前登陆用户：{}", orderNo, userId);
        Order order = judgeOrderUserId(orderNo, userId);
        // 刷新页面，判断订单状态是否为已支付
        return renderOrderDetail(request, order);
    }


    /**
     * 判断订单关联用户id和当前登陆用户是否一致
     *
     * @param orderNo 订单编号
     * @param userId  用户ID
     * @return 返回订单对象
     */
    private Order judgeOrderUserId(String orderNo, Long userId) {
        Order order = orderService.getOne(new QueryWrapper<Order>().eq("order_no", orderNo));
        // 判断订单userId
        if (order == null || !order.getUserId().equals(userId)) {
            throw new BusinessException("当前订单用户异常");
        }
        return order;
    }

    /**
     * 渲染订单详情
     *
     * @param request 请求对象
     * @param order   订单详情
     * @return 模板路径
     */
    private String renderOrderDetail(HttpServletRequest request, Order order) {
        List<OrderItem> orderItems = orderItemService.list(new QueryWrapper<OrderItem>().eq("order_id", order.getOrderId()));
        if (CollectionUtils.isEmpty(orderItems)) {
            throw new BusinessException("订单项异常");
        }
        List<OrderItemVO> itemVOList = MyBeanUtil.copyList(orderItems, OrderItemVO.class);
        OrderDetailVO orderDetailVO = new OrderDetailVO();
        BeanUtils.copyProperties(order, orderDetailVO);
        orderDetailVO.setOrderStatusString(OrderStatusEnum.getOrderStatusEnumByStatus(orderDetailVO.getOrderStatus()).getName());
        orderDetailVO.setPayTypeString(PayTypeEnum.getPayTypeEnumByType(orderDetailVO.getPayType()).getName());
        orderDetailVO.setNewBeeMallOrderItemVOS(itemVOList);
        request.setAttribute("orderDetailVO", orderDetailVO);

        return "mall/order-detail";
    }

}
