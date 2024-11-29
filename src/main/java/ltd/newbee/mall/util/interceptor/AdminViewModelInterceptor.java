package ltd.newbee.mall.util.interceptor;

import com.alibaba.fastjson.JSONObject;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ltd.newbee.mall.config.MallConfig;
import ltd.newbee.mall.config.MallConfig;
import ltd.newbee.mall.util.R;
import ltd.newbee.mall.util.ServletUtil;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

/**
 * 演示模式拦截器
 */
public class AdminViewModelInterceptor implements HandlerInterceptor {

    private final MallConfig mallConfig;


    public AdminViewModelInterceptor(MallConfig mallConfig) {
        this.mallConfig = mallConfig;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object o) {
        if (mallConfig.getViewModel()) {
            ServletUtil.renderString(response, JSONObject.toJSONString(R.error(mallConfig.getViewModelTip())));
            return false;
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {
    }

    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {

    }
}
