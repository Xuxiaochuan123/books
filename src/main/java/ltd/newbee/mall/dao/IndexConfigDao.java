package ltd.newbee.mall.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import ltd.newbee.mall.entity.IndexConfig;

import java.util.List;

public interface IndexConfigDao extends BaseMapper<IndexConfig> {

    IPage<IndexConfig> selectListPage(Page<IndexConfig> page, IndexConfig indexConfig);

    List<IndexConfig> selectListIndexConfig(Integer configType, int limit);
}
