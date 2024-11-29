package ltd.newbee.mall.controller.admin;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.servlet.http.HttpServletRequest;
import ltd.newbee.mall.controller.base.BaseController;
import ltd.newbee.mall.entity.Comment;
import ltd.newbee.mall.entity.Goods;
import ltd.newbee.mall.entity.MallUser;
import ltd.newbee.mall.service.CommentService;
import ltd.newbee.mall.service.MallUserService;
import ltd.newbee.mall.util.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigurationPackage;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("admin/comment")
public class AdminCommentController extends BaseController {

    @Autowired
    private CommentService commentService;

    @Autowired
    private MallUserService userService;

    private static final String PREFIX = "admin/comment";

    @GetMapping
    public String index(HttpServletRequest request) {
        request.setAttribute("path", "comment");
        return PREFIX + "/comment";
    }

    /**
     * 列表
     */
    @GetMapping("/list")
    @ResponseBody
    public IPage<Comment> list(Comment comment, HttpServletRequest request) {
        Page<Comment> page = getPage(request);


        IPage<Comment> commentIPage = commentService.selectPage(page, comment);
        // 设置用户名
        for (Comment item : commentIPage.getRecords()) {

            MallUser one = this.userService.getOne(new LambdaQueryWrapper<MallUser>()
                    .eq(MallUser::getUserId, item.getUserId()));
            item.setUserName(one.getNickName());

        }
        return commentIPage;
    }


    /**
     * 删除
     */
    @PostMapping("/del/{id}")
    public R del(@PathVariable String id) {

        int i = this.commentService.getBaseMapper().deleteById(id);

        return R.success();


    }



}
