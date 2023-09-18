package cn.fisher.sqlparser.parser.build;

import com.idss.asm.demand.biz.parser.AbstractBuildParse;
import com.idss.asm.demand.biz.parser.context.BuildParseContext;
import com.idss.asm.demand.biz.parser.context.ParenthesisContext;
import com.idss.asm.demand.biz.parser.context.ParseContext;
import com.idss.asm.demand.biz.parser.util.FormatParenthesisUtil;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author fisher
 * @date 2023-07-31: 11:12
 *
 * readme: init format Deduplication and formatting of elements
 */
@Component
public class InitFormatBuild extends AbstractBuildParse implements Ordered {
    @Override
    protected boolean isSupport(BuildParseContext buildParseContext) {
        return !CollectionUtils.isEmpty(buildParseContext.getContexts());
    }

    @Override
    protected BuildParseContext process(BuildParseContext buildParseContext) {
        List<ParseContext> contexts = buildParseContext.getContexts();
        List<ParenthesisContext> parenthesisContexts = FormatParenthesisUtil.splitRegular(contexts);

        if (CollectionUtils.isEmpty(parenthesisContexts)){
            return buildParseContext;
        }

        Set<Integer> useless = new HashSet<>();
        for (int i = parenthesisContexts.size() - 1; i >= 0; i--) {
            ParenthesisContext parenthesisContext = parenthesisContexts.get(i);
            parenthesisContext.isSupportThrowEx();
            parenthesisContext.lengthSupportThrowEx(contexts);

            List<ParseContext> parseContexts = contexts.subList(parenthesisContext.getStart(), parenthesisContext.getEnd() + 1);
            //only one or null
            if (Arrays.asList(0L, 1L).contains(parseContexts.stream().filter(e -> e.isNode()).count())){
                useless.addAll(parseContexts.stream().filter(e -> e.isParenthesis()).map(ParseContext::getIndex).collect(Collectors.toSet()));
            }
        }
        if (!CollectionUtils.isEmpty(useless)){
            return BuildParseContext.builder()
                    .contexts(contexts.stream().filter(e -> !useless.contains(e.getIndex())).collect(Collectors.toList()))
                    .build();
        }
        return buildParseContext;
    }

    @Override
    protected void listener() {

    }

    @Override
    public int getOrder() {
        return 1;
    }
}
