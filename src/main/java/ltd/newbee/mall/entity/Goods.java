package ltd.newbee.mall.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.io.Serializable;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("goods_info")
public class Goods extends BaseEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 2420217592493206057L;

    @TableId(type = IdType.AUTO)
    private Long goodsId;

    private String goodsName;

    private String goodsIntro;

    private Long goodsCategoryId;

    private String goodsCoverImg;

    private String goodsCarousel;

    private Integer originalPrice;

    private Integer sellingPrice;

    private Integer stockNum;

    private String tag;

    private Byte goodsSellStatus;

    private String goodsDetailContent;

}
