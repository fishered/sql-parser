package cn.fisher.sqlparser.parser.build;

import com.idss.asm.demand.biz.parser.AbstractBuildParse;
import com.idss.asm.demand.biz.parser.context.BuildParseContext;
import com.idss.asm.demand.biz.parser.context.ParseContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author fisher
 * @date 2023-07-27: 10:34
 * parenthesis build
 */
@Component
@Slf4j
public class ParenthesisBuild extends AbstractBuildParse implements Ordered {

    @Override
    protected boolean isSupport(BuildParseContext buildParseContext) {
        return !CollectionUtils.isEmpty(buildParseContext.getContexts());
    }

    @Override
    protected BuildParseContext process(BuildParseContext buildParseContext) {
        List<ParseContext> contexts = buildParseContext.getContexts();
        if (contexts.size() < 1){
            throw new IllegalArgumentException("构建节点不足，数据异常！");
        }

        //only first and last parenthesis will be remove
        long count = contexts.stream().filter(e -> e.isLeftParenthesis() || e.isRightParenthesis()).count();
        if (count > 2){
            return buildParseContext;
        }
        ParseContext first = contexts.stream().findFirst().get();
        ParseContext last = contexts.stream().skip(contexts.size() - 1).findFirst().get();
        if (first.isLeftParenthesis() && last.isRightParenthesis()){
            // Remove the end element first to prevent array offset calculation
            LinkedList<ParseContext> linkedContext = contexts.stream().collect(Collectors.toCollection(LinkedList::new));
            linkedContext.removeFirst();
            linkedContext.removeLast();
            contexts = new ArrayList<ParseContext>(linkedContext);
        }
        return BuildParseContext.builder().contexts(contexts).build();
    }

    @Override
    protected void listener() {
        log.warn("Parenthesis process finish...");
    }

    @Override
    public int getOrder() {
        return 2;
    }
}
