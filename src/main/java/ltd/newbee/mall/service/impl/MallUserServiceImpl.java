package ltd.newbee.mall.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.AllArgsConstructor;
import ltd.newbee.mall.dao.MallUserDao;
import ltd.newbee.mall.entity.MallUser;


import ltd.newbee.mall.service.MallUserService;
import ltd.newbee.mall.util.security.Md5Utils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@AllArgsConstructor
public class MallUserServiceImpl extends ServiceImpl<MallUserDao, MallUser> implements MallUserService {

    private MallUserDao mallUserDao;

    @Override
    public IPage<MallUser> selectPage(Page<MallUser> page, MallUser mallUser) {
        return mallUserDao.selectListPage(page, mallUser);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean register(String loginName, String password) {
        MallUser mallUser = new MallUser();
        mallUser.setLoginName(loginName);
        mallUser.setNickName(UUID.randomUUID().toString().substring(0, 5));
        mallUser.setPasswordMd5(password);
        if (!save(mallUser)) {
            return false;
        }

        return true;

    }
}
