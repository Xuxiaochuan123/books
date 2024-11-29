package ltd.newbee.mall.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import ltd.newbee.mall.entity.Comment;

public interface CommentService extends IService<Comment> {
    IPage<Comment> selectPage(Page<Comment> page, Comment comment);
}
