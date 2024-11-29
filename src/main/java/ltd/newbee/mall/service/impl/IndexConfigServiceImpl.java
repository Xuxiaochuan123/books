package ltd.newbee.mall.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import ltd.newbee.mall.dao.IndexConfigDao;
import ltd.newbee.mall.entity.Goods;
import ltd.newbee.mall.entity.IndexConfig;
import ltd.newbee.mall.service.GoodsService;
import ltd.newbee.mall.service.IndexConfigService;
import ltd.newbee.mall.util.enums.IndexConfigTypeEnum;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class IndexConfigServiceImpl extends ServiceImpl<IndexConfigDao, IndexConfig> implements IndexConfigService {

    @Resource
    private IndexConfigDao indexConfigDao;

    @Resource
    private GoodsService goodsService;

    @Override
    public IPage<IndexConfig> selectPage(Page<IndexConfig> page, IndexConfig indexConfig) {
        return indexConfigDao.selectListPage(page, indexConfig);
    }

    @Override
    public List<Goods> listIndexConfig(IndexConfigTypeEnum indexGoodsHot, int limit) {
        List<IndexConfig> list = indexConfigDao.selectListIndexConfig(indexGoodsHot.getType(), limit);
        List<Long> collect = list.stream().map(IndexConfig::getGoodsId).collect(Collectors.toList());

        if (collect.size() == 0) {
            return new ArrayList<>();
        }
        return goodsService.listByIds(collect);
    }

}
