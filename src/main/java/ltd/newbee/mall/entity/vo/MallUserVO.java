package ltd.newbee.mall.entity.vo;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * 商城用户VO对象
 */
@Data
public class MallUserVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 3698456957193931954L;
    private Long userId;

    private String nickName;

    private String loginName;

    private String password;

    private String introduceSign;

    private String address;

    private String realName;

    private String phone;

    private int shopCartItemCount;

    private Byte isDeleted;

    private Byte lockedFlag;

    private Date createTime;
}
