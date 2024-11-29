package ltd.newbee.mall.entity;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

@Data
@TableName
public class Comment implements Serializable {

    @Serial
    private static final long serialVersionUID = 2420217592493206057L;

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    private String content;

    private long goodId;

    private Date createTime;

    private long orderId;

    @TableField(exist = false)
    private String userName;

    @TableField(exist = false)
    private Date time;



}
