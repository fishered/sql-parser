package cn.fisher.sqlparser.parser.context;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.idss.asm.demand.biz.parser.enums.ConnectEnum;
import com.idss.asm.demand.biz.parser.util.FormatParenthesisUtil;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

/**
 * auth: fisher
 * readme： this context is parse result, we want to parse sql that it allowed to build.
 * Ultimately, they will exist as a set and be sorted according to the relevant order.
 * for example: select * from a where a = 1, parse opera is '=',parse column is 'a', parse
 * condition is '1'.
 * If it has multiple conditions, parse some conditions,
 * If only opera has a value, it indicates that it is an identifier connecting multiple conditions
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ParseContext {

    /**
     * process order will be update index, so if it equals '-1' ,this node it's processed and it's Temporary supplement
     */
    public static final int PROCESS_INDEX = -1;

    /**
     * Used for intra conditional or multiple conditional connections
     */
    private ConnectEnum opera;

    /**
     * column value
     */
    private String column;

    /**
     * condition value
     */
    private String condition;

    /**
     * result list index
     */
    private int index;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ParseContext that = (ParseContext) o;
        if (isConnection()){
            return Objects.equals(opera, opera);
        }
        return Objects.equals(opera, that.opera) && Objects.equals(column, that.column) && Objects.equals(condition, that.condition);
    }

    @Override
    public int hashCode() {
        return Objects.hash(opera, column, condition);
    }

    /**
     * is connection node
     * @return
     */
    @JsonIgnore
    public boolean isConnection(){
        return StringUtils.isAllBlank(column, condition);
    }

    /**
     * is data node
     * @return
     */
    @JsonIgnore
    public boolean isNode(){
        return StringUtils.isNotBlank(column) && StringUtils.isNotBlank(condition) && Objects.nonNull(opera);
    }

    /**
     * is left parenthesis connection
     * @return
     */
    @JsonIgnore
    public boolean isLeftParenthesis(){
        return StringUtils.equals(opera.getConnect(), ConnectEnum.LEFT_PARENTHESIS.getConnect());
    }

    @JsonIgnore
    public boolean isRightParenthesis(){
        return StringUtils.equals(opera.getConnect(), ConnectEnum.RIGHT_PARENTHESIS.getConnect());
    }

    @JsonIgnore
    public boolean isParenthesis(){
        return isLeftParenthesis() || isRightParenthesis();
    }

    @JsonIgnore
    public void processedIndex(){
        this.setIndex(PROCESS_INDEX);
    }

    @JsonIgnore
    public boolean isProcessed(){
        return index == PROCESS_INDEX;
    }

    @Override
    public String toString() {
        if (isConnection()){
            if (StringUtils.equals(ConnectEnum.RIGHT_PARENTHESIS.getConnect(), opera.getConnect())){
                return opera.getConnect();
            }
            if (StringUtils.equals(ConnectEnum.LEFT_PARENTHESIS.getConnect(), opera.getConnect())){
                return FormatParenthesisUtil.SEPARATOR + opera.getConnect();
            }
            return FormatParenthesisUtil.SEPARATOR + opera.getConnect() + FormatParenthesisUtil.SEPARATOR;
//            return opera.getConnect().trim();
        }
        if (isNode()){
            return String.format("%s%s%s%s%s",
                    column, FormatParenthesisUtil.SEPARATOR, opera.getConnect(), FormatParenthesisUtil.SEPARATOR, condition).trim();
        }
        throw new IllegalArgumentException("解析转化字符串失败！未知的类型处理！");
    }
}
