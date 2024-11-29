package ltd.newbee.mall.util.recommend.service;

import ltd.newbee.mall.entity.Goods;
import ltd.newbee.mall.util.recommend.dto.ProductDTO;
import ltd.newbee.mall.util.recommend.dto.RelateDTO;

import java.util.List;

public interface RecommendService {

    List<ProductDTO> getProductData();

    List<RelateDTO> getRelateData();

    List<Goods> recommendGoods(Long userId, Integer num);
}
