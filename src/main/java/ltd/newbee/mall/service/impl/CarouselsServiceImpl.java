package ltd.newbee.mall.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.AllArgsConstructor;
import ltd.newbee.mall.entity.Carousels;
import ltd.newbee.mall.service.CarouselsService;
import ltd.newbee.mall.dao.CarouselsDao;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class CarouselsServiceImpl extends ServiceImpl<CarouselsDao, Carousels> implements CarouselsService {

    private CarouselsDao carouselsDao;

    @Override
    public IPage<Carousels> selectPage(Page<Carousels> page, Carousels carousels) {
        return carouselsDao.selectListPage(page, carousels);
    }
}
