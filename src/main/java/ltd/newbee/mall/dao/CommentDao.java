package ltd.newbee.mall.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import ltd.newbee.mall.entity.Comment;

public interface CommentDao extends BaseMapper<Comment> {
    IPage<Comment> selectListPage(Page<Comment> page, Comment goods);
}
