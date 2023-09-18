package cn.fisher.sqlparser.parser.build;

import com.idss.asm.demand.biz.parser.AbstractBuildParse;
import com.idss.asm.demand.biz.parser.context.BuildParseContext;
import com.idss.asm.demand.biz.parser.context.ParenthesisContext;
import com.idss.asm.demand.biz.parser.context.ParseContext;
import com.idss.asm.demand.biz.parser.enums.ConnectEnum;
import com.idss.asm.demand.biz.parser.util.FormatParenthesisUtil;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author fisher
 * @date 2023-07-27: 13:46
 */
/**
 * process order
 * readme: process result will be cover, so we must setting this order
 * order Smaller, execute first, for example -> 1, 2, 3
 * @return
 */
@Component
public class ContextSortBuild extends AbstractBuildParse implements Ordered {

    @Override
    protected boolean isSupport(BuildParseContext buildParseContext) {
        return !CollectionUtils.isEmpty(buildParseContext.getContexts());
    }

    @Override
    protected BuildParseContext process(BuildParseContext buildParseContext) {
        List<ParseContext> contexts = buildParseContext.getContexts();
        List<ParenthesisContext> parenthesisContexts = FormatParenthesisUtil.splitRegular(contexts);

        List<ParseContext> process = new ArrayList<>(contexts.size());
        // split regular and process rule
        if (!CollectionUtils.isEmpty(parenthesisContexts)){
            //parenthesis is up
            parenthesisContexts.stream().forEach(e -> {
                process.addAll(processParenthesisOrder(e, contexts));
            });
        }
        // Obtain nodes that have not been processed and are nodes
        List<Integer> processed = Optional.ofNullable(
                process.stream().map(e -> e.getIndex()).distinct().collect(Collectors.toList()))
                        .orElse(Collections.EMPTY_LIST);
        List<ParseContext> notProcessNode = contexts.stream().filter(e -> !processed.contains(e.getIndex()))
                .filter(e -> e.isNode()).collect(Collectors.toList());

        List<ParseContext> parseContexts = processNormal(notProcessNode, contexts);
        if (!CollectionUtils.isEmpty(parseContexts)){
            //if process is null , remove first connect
            if (CollectionUtils.isEmpty(process) && parseContexts.stream().findFirst().get().isConnection()){
                parseContexts.remove(0);
            }
            process.addAll(parseContexts);
        }

        BuildParseContext<ParseContext> context = BuildParseContext.builder().contexts(process).build();
        context.validateThrowEx(buildParseContext);
        return context;
    }

    /**
     * The order in which a paragraph is processed separately
     * @param template
     * @param contexts whole data
     * @return
     */
    private List<ParseContext> processParenthesisOrder(ParenthesisContext template, List<ParseContext> contexts){
        if (Objects.isNull(template) || CollectionUtils.isEmpty(contexts)){
            return contexts;
        }
        template.isSupportThrowEx();
        template.lengthSupportThrowEx(contexts);

        //split whole , end + 1
        List<ParseContext> subbed = contexts.subList(template.getStart(), template.getEnd() + 1);
        List<ParseContext> nodes = subbed.stream().filter(e -> e.isNode()).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(nodes)){
            throw new IllegalArgumentException("要处理的括号中没有对应的节点，请检查语法是否正确！");
        }

        List<ParseContext> process = new ArrayList<>(contexts.size() + 1);
        nodes.sort(Comparator.comparing(ParseContext::getColumn));

        process.add(ParseContext.builder().index(template.getStart()).opera(ConnectEnum.LEFT_PARENTHESIS).build());
        //order and build context
        for (int i = 0; i < nodes.size(); i++) {
            ParseContext node = nodes.get(i);
            if (i == 0){
                process.add(node);
                continue;
            }
            //must find prev connect node
            int connectIndex = node.getIndex() - 1;
            ParseContext context = connectIndex < 0? ParseContext.builder().opera(ConnectEnum.AND).index(ParseContext.PROCESS_INDEX).build()
                    : contexts.get(connectIndex);
            if (subbed.indexOf(node) == 1){
                // prev is left
                context.setOpera(ConnectEnum.AND);
            }
            process.add(context);
            process.add(node);
        }
        process.add(ParseContext.builder().index(template.getEnd()).opera(ConnectEnum.RIGHT_PARENTHESIS).build());

        //last
        if (template.getEnd() != contexts.size() - 1){
            process.add(contexts.get(template.getEnd() + 1));
        }
        return process;
    }

    /**
     * process normal node
     * @param notProcessed
     * @return
     */
    private List<ParseContext> processNormal(List<ParseContext> notProcessed, List<ParseContext> contexts){
        if (CollectionUtils.isEmpty(notProcessed)){
            return Collections.EMPTY_LIST;
        }

        List<ParseContext> processed = new ArrayList<>(notProcessed.size() * 2);
        notProcessed.sort(Comparator.comparing(ParseContext::getColumn));
        notProcessed.stream().forEach(e -> {
            if (!e.isNode()){
                return;
            }
            //must find prev connect node
            int connectIndex = e.getIndex() - 1;
            //count is 1
            if (connectIndex < 0){
                if (notProcessed.size() == 1 && contexts.size() == 1){
                    processed.add(e);
                    return;
                }
                ParseContext parseContextConn = contexts.get(e.getIndex() + 1);
                if (Objects.isNull(parseContextConn) || !parseContextConn.isConnection()){
                    processed.add(ParseContext.builder().opera(ConnectEnum.AND).index(ParseContext.PROCESS_INDEX).build());
                } else {
                    processed.add(parseContextConn);
                }
            } else {
                processed.add(contexts.get(connectIndex));
            }
            processed.add(e);
        });
        return processed;
    }

    @Override
    protected void listener() {

    }

    @Override
    public int getOrder() {
        return 3;
    }
}
