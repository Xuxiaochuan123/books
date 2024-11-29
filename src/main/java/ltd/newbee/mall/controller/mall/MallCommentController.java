package ltd.newbee.mall.controller.mall;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import ltd.newbee.mall.entity.Comment;
import ltd.newbee.mall.entity.MallUser;
import ltd.newbee.mall.entity.Order;
import ltd.newbee.mall.entity.OrderItem;
import ltd.newbee.mall.entity.vo.MallUserVO;
import ltd.newbee.mall.service.CommentService;
import ltd.newbee.mall.service.MallUserService;
import ltd.newbee.mall.service.OrderItemService;
import ltd.newbee.mall.service.OrderService;
import ltd.newbee.mall.util.R;
import ltd.newbee.mall.util.constant.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigurationPackage;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;

@Slf4j
@RestController
public class MallCommentController {

    @Autowired
    private MallUserService userService;

    @Autowired
    private CommentService commentService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderItemService orderItemService;
    // 商品获取评论
    @GetMapping("/comment/{goodId}")
    public R getGoodComment(@PathVariable("goodId") long goodId){

        List<Comment> list = this.commentService.list(new LambdaQueryWrapper<Comment>()
                .eq(Comment::getGoodId, goodId));

        // 封装名字
        for (Comment item : list) {
            MallUser one = this.userService.getOne(new LambdaQueryWrapper<MallUser>()
                    .eq(MallUser::getUserId, item.getUserId()));

            item.setUserName(one.getNickName());
        }

        return R.result(true).add("data",list);

    }

    // 进行评论
    @PostMapping("/userComment/{orderNo}/{comment}")
    public R userComment(
                         @PathVariable String comment,
                         @PathVariable long orderNo,
                         HttpSession session) {
        MallUserVO mallUserVO = (MallUserVO) session.getAttribute(Constants.MALL_USER_SESSION_KEY);

        Long userId = mallUserVO.getUserId();

        // 获取订单id
        Order order = this.orderService.getOne(new LambdaQueryWrapper<Order>()
                .eq(Order::getOrderNo, orderNo));

        //获取订单小项
        List<OrderItem> list = this.orderItemService.list(new LambdaQueryWrapper<OrderItem>()
                .eq(OrderItem::getOrderId, order.getOrderId()));


        // 用户只能评论一次
        for (OrderItem item : list) {
            Comment one = this.commentService.getOne(new LambdaQueryWrapper<Comment>()
                    .eq(Comment::getGoodId, item.getGoodsId())
                    .eq(Comment::getUserId, userId)
                    .eq(Comment::getOrderId,order.getOrderId()));

            if(one != null) {
                return R.result(false,"只能评论一次");
            }

        }



        // 确认收货才能评价
        Order order1 = this.orderService.getOne(new LambdaQueryWrapper<Order>()
                .eq(Order::getOrderId, order.getOrderId()));

        if (order.getOrderStatus() != 4) {
            return R.result(false,"订单完成才能评论");
        }

        // 插入数据
        for (OrderItem orderItem : list) {
            Comment c = new Comment();
            c.setContent(comment);
            c.setUserId(userId);
            c.setGoodId(orderItem.getGoodsId());
            c.setCreateTime(new Date());
            c.setOrderId(order.getOrderId());
            this.commentService.save(c);
        }
        return R.success();
    }


    // 查看我的评论
    @GetMapping("/comment/user")
    public R commentUser(HttpSession session){
        MallUserVO mallUserVO = (MallUserVO) session.getAttribute(Constants.MALL_USER_SESSION_KEY);

        Long userId = mallUserVO.getUserId();


        List<Comment> list = this.commentService.list(new LambdaQueryWrapper<Comment>()
                .eq(Comment::getUserId, userId));

        return R.success().add("data",list);
    }

    // 删除评论
    @GetMapping("/comment/del/{id}")
    public R delComment(@PathVariable("id") long id) {
        boolean b = this.commentService.removeById(id);
        return R.success();
    }


}
