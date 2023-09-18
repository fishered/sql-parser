package cn.fisher.sqlparser.parser.util;

import com.idss.asm.demand.biz.parser.context.ParenthesisContext;
import com.idss.asm.demand.biz.parser.context.ParseContext;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author fisher
 * @date 2023-07-31: 11:15
 * split regular
 */
public class FormatParenthesisUtil {

    public static final String SEPARATOR = " ";

    public static final String NULL = "NULL";

    public static final String IS_TRUE = "TRUE";

    public static final String IS_FALSE = "FALSE";

    public static List<ParenthesisContext> splitRegular(List<ParseContext> contexts){
        List<ParenthesisContext> template = new ArrayList<>(contexts.size() + 1);
        //First obtain the conditional ranges in parentheses, they can only be transformed within the conditions
        List<Integer> left = contexts.stream().filter(e -> e.isLeftParenthesis())
                .sorted(Comparator.comparing(ParseContext::getIndex))
                .map(ParseContext::getIndex)
                .collect(Collectors.toList());
        List<Integer> right = contexts.stream().filter(e -> e.isRightParenthesis())
                .sorted(Comparator.comparing(ParseContext::getIndex).reversed())
                .map(ParseContext::getIndex)
                .collect(Collectors.toList());

        if (left.size() != right.size()){
            throw new IllegalArgumentException("出现无法匹配的括号符号！");
        }
        if (left.size() == 0){
            return Collections.EMPTY_LIST;
        }
        for (int i = 0; i < left.size(); i++) {
            ParenthesisContext parenthesisContext = ParenthesisContext.builder()
                    .start(left.get(i))
                    .end(right.get(i))
                    .build();
            parenthesisContext.isSupportThrowEx();
            template.add(parenthesisContext);
        }
        return template;
    }

}
