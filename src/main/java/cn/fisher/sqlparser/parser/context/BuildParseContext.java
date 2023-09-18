package cn.fisher.sqlparser.parser.context;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author fisher
 * @date 2023-07-27: 10:35
 * build parse context
 */
@Data
@Builder
public class BuildParseContext<P extends ParseContext> {

    /**
     * build result list
     */
    private List<P> contexts;

    @Override
    public String toString() {
        if (CollectionUtils.isEmpty(contexts)){
            return StringUtils.EMPTY;
        }
        return contexts.stream().map(e -> e.toString()).collect(Collectors.joining(StringUtils.EMPTY));
    }

    @JsonIgnore
    public void initIndex(){
        if (!CollectionUtils.isEmpty(contexts)){
            for (int i = 0; i < contexts.size(); i++) {
                contexts.get(i).setIndex(i);
            }
        }
    }

    @JsonIgnore
    public void validateThrowEx(BuildParseContext buildParseContext){
        if (Objects.isNull(buildParseContext)){
            // isSupport ?
            throw new IllegalArgumentException("获取的解析上下文是空的！");
        }
        if (CollectionUtils.isEmpty(contexts)){
            // isSupport ?
            throw new IllegalArgumentException("解析完成的上下文是空的！");
        }
        List<ParseContext> contextsParam = buildParseContext.getContexts();
        long paramNodeCount = contextsParam.stream().filter(e -> e.isNode()).count();
        long paramConnectCount = contextsParam.stream().filter(e -> e.isConnection()).count();

        long nodeCount = ((List<ParseContext>) contexts).stream().filter(e -> e.isNode()).count();
        long connectCount = ((List<ParseContext>) contexts).stream().filter(e -> e.isConnection()).count();

        // connect nodes possible not equals because connect node may be remove or format
//        Assert.isTrue(paramConnectCount == connectCount, "解析结果中连接符号数量不匹配！");
        Assert.isTrue(paramNodeCount == nodeCount, "解析结果中节点数量不匹配！");
    }
}
