package ltd.newbee.mall.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

@TableName("order_item")
@Data
public class OrderItem implements Serializable {
    @Serial
    private static final long serialVersionUID = -3556134736699188724L;
    @TableId(type = IdType.AUTO)
    private Long orderItemId;

    private Long orderId;

    private Long seckillId;

    private Long goodsId;

    private String goodsName;

    private String goodsCoverImg;

    private Integer sellingPrice;

    private Integer goodsCount;

    private Date createTime;

}
