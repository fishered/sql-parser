package cn.fisher.sqlparser.parser.build;

import com.idss.asm.demand.biz.parser.AbstractBuildParse;
import com.idss.asm.demand.biz.parser.context.BuildParseContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

/**
 * @author fisher
 * @date 2023-08-02: 15:04
 */
@Component
@Slf4j
public class RepeatElementBuild extends AbstractBuildParse implements Ordered {

    @Override
    protected boolean isSupport(BuildParseContext buildParseContext) {
        return !CollectionUtils.isEmpty(buildParseContext.getContexts());
    }

    @Override
    protected BuildParseContext process(BuildParseContext buildParseContext) {
        return buildParseContext;
    }

    @Override
    protected void listener() {

    }

    @Override
    public int getOrder() {
        return 4;
    }
}
