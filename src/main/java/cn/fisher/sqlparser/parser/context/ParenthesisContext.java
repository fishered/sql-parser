package cn.fisher.sqlparser.parser.context;

import cn.hutool.core.lang.Assert;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Data;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * @author fisher
 * @date 2023-07-27: 14:21
 *
 * parse context parenthesis index
 *
 */
@Data
@Builder
public class ParenthesisContext {

    private Integer start;

    private Integer end;

    @JsonIgnore
    public void isSupportThrowEx(){
        Assert.notNull(start, "括号起始位置不能是空的！");
        Assert.notNull(end, "括号结束位置不能是空的！");
        Assert.isTrue(start < end, "括号起始位置不能小于结束位置！");
    }

    @JsonIgnore
    public void lengthSupportThrowEx(List data){
        if (CollectionUtils.isEmpty(data)){
            throw new IllegalArgumentException("准备处理的数据集合是空的！");
        }
        Assert.isTrue(start <= data.size(), "准备处理的数据长度过短！");
        Assert.isTrue(end <= data.size(), "准备处理的数据长度过短！");
    }

}
