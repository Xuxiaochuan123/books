package ltd.newbee.mall.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import ltd.newbee.mall.entity.Order;
import ltd.newbee.mall.entity.vo.DayTransactionAmountVO;
import ltd.newbee.mall.entity.vo.OrderListVO;
import ltd.newbee.mall.entity.vo.OrderVO;
import java.util.List;

public interface OrderDao extends BaseMapper<Order> {

    IPage<OrderListVO> selectListVOPage(Page<OrderListVO> page, Order order);

    IPage<Order> selectListPage(Page<Order> page, OrderVO order);

    List<DayTransactionAmountVO> countMallTransactionAmount(Integer dayNum);
    List<Order> selectOrderIds();
}
