package ltd.newbee.mall.util.constant;

public class Constants {

    public static final String SUCCESS = "success";
    public static final String ERROR = "error";

    public static final Byte CATEGORY_LEVEL_ONE = 1;
    public static final Byte CATEGORY_LEVEL_TWO = 2;
    public static final Byte CATEGORY_LEVEL_THREE = 3;

    public static final String CATEGORIES = "categories";
    public static final String CAROUSELS = "carousels";
    public static final String HOT_GOODSES = "hotGoodses";
    public static final String NEW_GOODSES = "newGoodses";
    public static final String RECOMMEND_GOODSES = "recommendGoodses";

    /**
     * 首页热卖商品数量
     */
    public final static int INDEX_GOODS_HOT_NUMBER = 4;
    /**
     * 首页新品数量
     */
    public final static int INDEX_GOODS_NEW_NUMBER = 5;
    /**
     * 首页推荐商品数量
     */
    public final static int INDEX_GOODS_RECOMMOND_NUMBER = 10;

    /**
     * 项目缓存前缀
     */
    public static final String CACHE_PREFIX = "mall:";

    /**
     * session中user的key
     */
    public static final String MALL_USER_SESSION_KEY = "mallUser";

    /**
     * 验证码key
     */
    public static final String MALL_VERIFY_CODE_KEY = "mallVerifyCode";


    /**
     * 搜索分页的默认条数(每页10条)
     */
    public static final int GOODS_SEARCH_PAGE_LIMIT = 20;

    /**
     * 我的订单列表分页的默认条数(每页3条)
     */
    public static final int ORDER_SEARCH_PAGE_LIMIT = 3;

    /**
     * 字符编码
     */
    public static final String UTF_ENCODING = "UTF-8";


    /**
     * 当前页
     */
    public static final String PAGE_NUMBER = "pageNumber";

    /**
     * 分页大小
     */
    public static final String PAGE_SIZE = "pageSize";



    /**
     * 排序方式 asc或者desc
     */
    public static final String ORDER_DESC = "desc";
    public static final String SORT_ASC = "asc";


    /**
     * redisSearch索引名称
     */
    public static final String GOODS_IDX_NAME = "idx:goods";

    /**
     * redisSearch商品hash前缀
     */
    public static final String GOODS_IDX_PREFIX = CACHE_PREFIX + "goods:";

    /**
     * redisSearch商品索引语言
     */
    public static final String GOODS_IDX_LANGUAGE = "chinese";

    public static final String SAVE_ORDER_RESULT_KEY = CACHE_PREFIX + "save_order_result_key:";
}
