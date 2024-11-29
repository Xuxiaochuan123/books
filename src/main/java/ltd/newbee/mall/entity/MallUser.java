package ltd.newbee.mall.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

@TableName("user")
@Data
public class MallUser implements Serializable {

    @Serial
    private static final long serialVersionUID = -4514041413899643970L;

    @TableId(type = IdType.AUTO)
    private Long userId;

    private String nickName;

    private String loginName;

    private String passwordMd5;

    private String realName;

    private String phone;

    private String introduceSign;

    private String address;

    private Byte isDeleted;

    private Byte lockedFlag;

    private Date createTime;
}
