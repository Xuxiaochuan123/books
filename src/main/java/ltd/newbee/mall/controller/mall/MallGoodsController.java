package ltd.newbee.mall.controller.mall;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import ltd.newbee.mall.entity.*;
import ltd.newbee.mall.service.*;
import ltd.newbee.mall.util.constant.Constants;
import ltd.newbee.mall.controller.base.BaseController;
import ltd.newbee.mall.entity.vo.MallUserVO;
import ltd.newbee.mall.entity.vo.SearchObjVO;
import ltd.newbee.mall.entity.vo.SearchPageCategoryVO;
import ltd.newbee.mall.entity.vo.SearchPageGoodsVO;
import ltd.newbee.mall.util.redis.JedisSearch;
import ltd.newbee.mall.util.MyBeanUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import redis.clients.jedis.search.Document;
import redis.clients.jedis.search.SearchResult;

import java.util.*;

@Slf4j
@Controller
public class MallGoodsController extends BaseController {

    @Autowired
    private GoodsService goodsService;

    @Autowired
    private GoodsCategoryService goodsCategoryService;

    @Autowired
    private ShopCatService shopCatService;

    @Autowired
    private MallUserService userService;

    @Autowired
    private CommentService commentService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private JedisSearch jedisSearch;

    @GetMapping("/search")
    public String searchPage(SearchObjVO searchObjVO, HttpServletRequest request) {
        Page<SearchPageGoodsVO> page = getPage(request, Constants.GOODS_SEARCH_PAGE_LIMIT);
        String keyword = searchObjVO.getKeyword();
        IPage<Goods> iPage = goodsService.findMallGoodsListBySearch(page, searchObjVO);
        request.setAttribute("keyword", keyword);
        request.setAttribute("pageResult", iPage);
        request.setAttribute("orderBy", searchObjVO.getSidx());
        request.setAttribute("order", searchObjVO.getOrder());
        Long goodsCategoryId = searchObjVO.getGoodsCategoryId();
        return goodsCategoryHandler(request, goodsCategoryId);
    }

   /* @GetMapping("/search1")
    public String rsSearch(SearchObjVO searchObjVO, HttpServletRequest request) {
        Page<SearchPageGoodsVO> page = getPage(request, Constants.GOODS_SEARCH_PAGE_LIMIT);
        String keyword = searchObjVO.getKeyword();
        long size = page.getSize();
        SearchResult query;
        IPage<Goods> iPage = new Page<>(page.getCurrent(), size);
        try {
            query = jedisSearch.search(Constants.GOODS_IDX_NAME, searchObjVO, page);
            List<Goods> list = new ArrayList<>();
            for (Document document : query.getDocuments()) {
                Map<String, Object> map = new HashMap<>();
                for (Map.Entry<String, Object> property : document.getProperties()) {
                    String key = property.getKey();
                    Object value = property.getValue();
                    map.put(key, value);
                }
                Goods goods = MyBeanUtil.toBean(map, Goods.class);
                list.add(goods);
            }
            long totalResults = query.getTotalResults();
            iPage.setTotal(totalResults);
            iPage.setRecords(list);
            iPage.setPages((long) Math.ceil(totalResults / (double) size));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            iPage = goodsService.findMallGoodsListBySearch(page, searchObjVO);
        }
        Long goodsCategoryId = searchObjVO.getGoodsCategoryId();
        request.setAttribute("keyword", keyword);
        request.setAttribute("pageResult", iPage);
        request.setAttribute("orderBy", searchObjVO.getSidx());
        request.setAttribute("order", searchObjVO.getOrder());
        return goodsCategoryHandler(request, goodsCategoryId);
    }*/

    private String goodsCategoryHandler(HttpServletRequest request, Long goodsCategoryId) {
        if (goodsCategoryId != null) {
            Goods goods = new Goods();
            goods.setGoodsCategoryId(goodsCategoryId);
            SearchPageCategoryVO searchPageCategoryVO = new SearchPageCategoryVO();
            GoodsCategory thirdCategory = goodsCategoryService.getById(goodsCategoryId);
            GoodsCategory secondCategory = goodsCategoryService.getById(thirdCategory.getParentId());
            List<GoodsCategory> thirdCateGoryList = goodsCategoryService.list(new QueryWrapper<GoodsCategory>()
                    .eq("parent_id", thirdCategory.getParentId()));
            searchPageCategoryVO.setCurrentCategoryName(thirdCategory.getCategoryName());
            searchPageCategoryVO.setSecondLevelCategoryName(secondCategory.getCategoryName());
            searchPageCategoryVO.setThirdLevelCategoryList(thirdCateGoryList);
            request.setAttribute("goodsCategoryId", goodsCategoryId);
            request.setAttribute("searchPageCategoryVO", searchPageCategoryVO);
        }
        return "mall/search";
    }

    @GetMapping("/goods/detail/{goodsId}")
    public String detail(@PathVariable("goodsId") Long goodsId, HttpServletRequest request) {
        Goods goods = goodsService.getById(goodsId);
        request.setAttribute("cartItemId", 0);
        request.setAttribute("goodsCount", 0);
        // 查询购物车中是否有该商品
        MallUserVO userVO = (MallUserVO) session.getAttribute(Constants.MALL_USER_SESSION_KEY);
        if (userVO != null) {
            ShopCat shopCat = shopCatService.selectByUserIdAndGoodsId(userVO.getUserId(), goodsId);
            if (Objects.nonNull(shopCat)) {
                request.setAttribute("cartItemId", shopCat.getCartItemId());
                request.setAttribute("goodsCount", shopCat.getGoodsCount());
            }
        }
        List<Comment> list = this.commentService.list(new LambdaQueryWrapper<Comment>()
                .eq(Comment::getGoodId, goodsId));

        // 封装名字
        for (Comment item : list) {
            MallUser one = this.userService.getOne(new LambdaQueryWrapper<MallUser>()
                    .eq(MallUser::getUserId, item.getUserId()));

            item.setUserName(one.getNickName());
        }
        request.setAttribute("goodsDetail", goods);
        request.setAttribute("commentList", list);

        return "mall/detail";
    }
}
