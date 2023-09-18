package cn.fisher.sqlparser.parser.process;

import com.idss.asm.demand.biz.parser.AbstractExpression;
import com.idss.asm.demand.biz.parser.ParameterFormatBiz;
import com.idss.asm.demand.biz.parser.context.ParseContext;
import com.idss.asm.demand.biz.parser.enums.ConnectEnum;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.Parenthesis;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author fisher
 * @date 2023-07-26: 16:05
 */
@Component
public class ParenthesisProcess extends AbstractExpression<ParseContext> implements InitializingBean {

    @Resource
    private ParameterFormatBiz<ParseContext> parameterFormatBiz;

    @Override
    protected void handler(Expression expr, CopyOnWriteArrayList<ParseContext> parseContexts) {
        Expression leftExpression = ((Parenthesis) expr).getExpression();

        parseContexts.add(
                ParseContext.builder().opera(ConnectEnum.LEFT_PARENTHESIS).index(parseContexts.size()).build()
        );
        parameterFormatBiz.recursionProcess(parseContexts, leftExpression);
        parseContexts.add(
                ParseContext.builder().opera(ConnectEnum.RIGHT_PARENTHESIS).index(parseContexts.size()).build()
        );
    }

    @Override
    protected Class getClazz() {
        return Parenthesis.class;
    }

}
