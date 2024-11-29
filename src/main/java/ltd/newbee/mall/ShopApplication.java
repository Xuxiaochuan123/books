package ltd.newbee.mall;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ShopApplication {

    /**
     * # 1. 解压项目图片
     * 将图片文件文件夹内压缩包解压到D盘upload文件夹中，D:\\upload
     *
     *
     * ## 后台用户
     * 管理员：
     * 打开浏览器输入：http://localhost:84/shop/admin/login
     * 账号：admin
     * 密码：123456
     *
     *
     * ## 普通用户2
     * 打开浏览器输入：http://localhost:84/shop
     * 账号：17607605333
     * 密码：123456
     *
     *
     * 运行环境：redis + jdk17 + idea2021往后
     *
     *
     *
     * @param args
     */
    public static void main(String[] args) {
        SpringApplication.run(ShopApplication.class, args);
    }
}
