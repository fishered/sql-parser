package cn.fisher.sqlparser.parser;

import com.idss.asm.demand.biz.parser.context.ParseContext;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.select.SelectBody;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * format task parameter in rule and order
 * input output string format sql must be equals
 */
@Component
public class ParameterFormatBiz<P extends ParseContext> {

    /**
     * Fixed concatenated SQL prefix
     */
    private static final String PREFIX = "select * from a where ";

    public List<P> process(String condition) throws JSQLParserException {
        return parseData(parseExpression(condition));
    }

    /**
     * general sql and parse expression
     * @param condition
     * @return
     */
    private Expression parseExpression(String condition) throws JSQLParserException {
        Select select = (Select) CCJSqlParserUtil.parse(PREFIX + condition);
        SelectBody selectBody = select.getSelectBody();
        PlainSelect plainSelect = (PlainSelect) selectBody;
        return CCJSqlParserUtil.parseCondExpression(String.valueOf(plainSelect.getWhere()));
    }

    /**
     * recursion process expression
     * @param parseContexts
     * @param expr
     * @return
     */
    public List<P> recursionProcess(CopyOnWriteArrayList<P> parseContexts, Expression expr){
        AbstractExpression expression = AbstractExpression.register.get(expr.getClass());
        if (expression == null){
            throw new RuntimeException("解析异常，找不到对应的处理！");
        }
        if (expression.isSupport(expr, parseContexts)){
            expression.handler(expr, parseContexts);
        }
        return parseContexts;
    }

    private List<P> parseData(Expression expression) throws JSQLParserException {
        if (Objects.isNull(expression)){
            throw new JSQLParserException("解析expression是空的！");
        }

        CopyOnWriteArrayList<P> parseContexts = new CopyOnWriteArrayList<>();
        return recursionProcess(parseContexts, expression);
    }

}
