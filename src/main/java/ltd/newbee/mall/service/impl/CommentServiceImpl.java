package ltd.newbee.mall.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import ltd.newbee.mall.dao.CommentDao;
import ltd.newbee.mall.entity.Comment;
import ltd.newbee.mall.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class CommentServiceImpl extends ServiceImpl<CommentDao, Comment> implements CommentService {

    @Autowired
    private CommentDao dao;


    @Override
    public IPage<Comment> selectPage(Page<Comment> page, Comment comment) {

        Page<Comment> page1 = this.page(page, null);


        return page1;
    }
}
