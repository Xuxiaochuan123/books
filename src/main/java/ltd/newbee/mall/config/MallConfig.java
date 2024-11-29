package ltd.newbee.mall.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "mall")
public class MallConfig {

    private String serverUrl;
    private Boolean viewModel;
    private String viewModelTip;
    private String uploadDir;
}
