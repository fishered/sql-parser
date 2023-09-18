package cn.fisher.sqlparser.parser.build;

import com.idss.asm.demand.biz.parser.AbstractBuildParse;
import com.idss.asm.demand.biz.parser.context.BuildParseContext;
import com.idss.asm.demand.biz.parser.context.ParseContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author fisher
 * @date 2023-07-31: 11:59
 * process special char
 */
@Component
@Slf4j
public class SpecialCharBuild extends AbstractBuildParse implements Ordered {
    @Override
    protected boolean isSupport(BuildParseContext buildParseContext) {
        return !CollectionUtils.isEmpty(buildParseContext.getContexts());
    }

    @Override
    protected BuildParseContext process(BuildParseContext buildParseContext) {
        List<ParseContext> contexts = buildParseContext.getContexts();
        return BuildParseContext.builder().contexts(
                contexts.stream().map(e -> filterSpecial(e)).collect(Collectors.toList())
        ).build();
    }

    @Override
    protected void listener() {

    }

    @Override
    public int getOrder() {
        return 0;
    }

    private ParseContext filterSpecial(ParseContext parseContext){
        ParseContext context = new ParseContext();
        context.setIndex(parseContext.getIndex());
        context.setOpera(parseContext.getOpera());
        if (parseContext.isNode()){
            context.setColumn(processSpecial(parseContext.getColumn()));
            context.setCondition(processSpecial(parseContext.getCondition()));
        }
        return context;
    }

    private static String processSpecial(String special){
        if (StringUtils.isEmpty(special)){
            return StringUtils.EMPTY;
        }
        return special.toUpperCase().replaceAll("\"", "'").trim();

    }

}
