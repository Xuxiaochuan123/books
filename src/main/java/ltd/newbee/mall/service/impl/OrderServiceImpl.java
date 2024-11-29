package ltd.newbee.mall.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ltd.newbee.mall.dao.OrderDao;
import ltd.newbee.mall.entity.*;
import ltd.newbee.mall.entity.vo.*;
import ltd.newbee.mall.util.constant.Constants;


import ltd.newbee.mall.service.*;
import ltd.newbee.mall.util.event.OrderEvent;

import ltd.newbee.mall.exception.BusinessException;
import ltd.newbee.mall.util.redis.RedisCache;
import ltd.newbee.mall.util.task.TaskService;
import ltd.newbee.mall.util.NumberUtil;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class OrderServiceImpl extends ServiceImpl<OrderDao, Order> implements OrderService {

    @Resource
    private OrderDao orderDao;

    @Resource
    private GoodsService goodsService;

    @Resource
    private ShopCatService shopCatService;

    @Resource
    private OrderItemService orderItemService;

    @Resource
    private TaskService taskService;

    @Resource
    private RedisCache redisCache;

    @Resource
    private ApplicationEventPublisher applicationEventPublisher;

    @Override
    public IPage<OrderListVO> selectMyOrderPage(Page<OrderListVO> page, Order order) {
        return orderDao.selectListVOPage(page, order);
    }

    @Override
    public IPage<Order> selectPage(Page<Order> page, OrderVO orderVO) {
        IPage<Order> orderIPage = orderDao.selectListPage(page, orderVO);
        return orderIPage;
    }

    @Override
    public String saveOrder(MallUserVO mallUserVO, Long couponUserId, List<ShopCatVO> shopcatVOList) {
        List<Long> goodsIdList = shopcatVOList.stream().map(ShopCatVO::getGoodsId).collect(Collectors.toList());
        List<Long> cartItemIdList = shopcatVOList.stream().map(ShopCatVO::getCartItemId).collect(Collectors.toList());
        // 检查是否包含已下架商品
        saveOrderCheck(shopcatVOList, goodsIdList, cartItemIdList, couponUserId);
        // 生成订单号
        String orderNo = NumberUtil.genOrderNo();
        // 生成订单、删除购物车、扣减库存
        saveOrder(mallUserVO, couponUserId, shopcatVOList, orderNo);
        // 所有操作成功后，将订单号返回
        return orderNo;
    }

    private void saveOrder(MallUserVO mallUserVO, Long couponUserId, List<ShopCatVO> shopcatVOList, String orderNo) {
        applicationEventPublisher.publishEvent(new OrderEvent(orderNo, mallUserVO, couponUserId, shopcatVOList));
    }

    /**
     * 下单检查
     *
     * @param shopcatVOList
     * @param goodsIdList
     * @param cartItemIdList
     * @param couponUserId
     */
    private void saveOrderCheck(List<ShopCatVO> shopcatVOList, List<Long> goodsIdList, List<Long> cartItemIdList, Long couponUserId) {
        List<Goods> goods = goodsService.listByIds(goodsIdList);
        List<Goods> collect = goods.stream().filter(goods1 -> goods1.getGoodsSellStatus() == 1).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(collect)) {
            throw new BusinessException(collect.get(0).getGoodsName() + "已下架，无法生成订单");
        }
        Map<Long, Goods> goodsMap = goods.stream().collect(Collectors.toMap(Goods::getGoodsId, goods1 -> goods1));
        for (ShopCatVO shopCatVO : shopcatVOList) {
            // 查出的商品中不存在购物车中的这条关联商品数据，直接返回错误提醒
            if (!goodsMap.containsKey(shopCatVO.getGoodsId())) {
                throw new BusinessException("购物车中商品数据异常");
            }
            // 存在数量大于库存的情况，直接返回错误提醒
            if (shopCatVO.getGoodsCount() > goodsMap.get(shopCatVO.getGoodsId()).getStockNum()) {
                throw new BusinessException("购物车中:" + goodsMap.get(shopCatVO.getGoodsId()).getGoodsName() + " 库存不足");
            }
        }

        int priceTotal = 0;
        for (ShopCatVO shopCatVO : shopcatVOList) {
            priceTotal += shopCatVO.getGoodsCount() * shopCatVO.getSellingPrice();
        }

        // 总价异常
        if (priceTotal <= 0) {
            throw new BusinessException("订单价格异常");
        }

        if (CollectionUtils.isEmpty(goodsIdList)
                || CollectionUtils.isEmpty(cartItemIdList)
                || CollectionUtils.isEmpty(goods)) {
            throw new BusinessException("结算异常");
        }
    }



    @Override
    public List<DayTransactionAmountVO> countMallTransactionAmount(Integer dayNum) {
        if (dayNum < 0) {
            return Collections.emptyList();
        }
        return orderDao.countMallTransactionAmount(dayNum);
    }
}
