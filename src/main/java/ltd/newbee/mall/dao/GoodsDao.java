package ltd.newbee.mall.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import ltd.newbee.mall.entity.Goods;
import ltd.newbee.mall.entity.vo.SearchObjVO;
import ltd.newbee.mall.entity.vo.SearchPageGoodsVO;

import java.util.List;

public interface GoodsDao extends BaseMapper<Goods> {
    IPage<Goods> selectListPage(Page<Goods> page, Goods goods);
    List<Goods> selectGoodsListByIds(List<Long> ids);

    List<Goods> selectUpGoodsList();

    IPage<Goods> findMallGoodsListBySearch(Page<SearchPageGoodsVO> page, SearchObjVO searchObjVO);

    boolean addStock(Long goodsId, Integer number);

    boolean reduceStock(Long goodsId, Integer number);

}
