package cn.fisher.sqlparser.parser.process;

import com.idss.asm.demand.biz.parser.AbstractExpression;
import com.idss.asm.demand.biz.parser.ParameterFormatBiz;
import com.idss.asm.demand.biz.parser.context.ParseContext;
import com.idss.asm.demand.biz.parser.enums.ConnectEnum;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.operators.conditional.OrExpression;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author fisher
 * @date 2023-07-26: 16:05
 */
@Component
public class OrExpressionProcess extends AbstractExpression<ParseContext> implements InitializingBean {

    @Resource
    private ParameterFormatBiz<ParseContext> parameterFormatBiz;

    @Override
    protected void handler(Expression expr, CopyOnWriteArrayList<ParseContext> parseContexts) {
        Expression leftExpression = ((OrExpression) expr).getLeftExpression();
        parameterFormatBiz.recursionProcess(parseContexts, leftExpression);
        /**
         * process or condition
         */
        parseContexts.add(
                ParseContext.builder().opera(ConnectEnum.OR).build()
        );
        /**
         * process right
         */
        Expression rightExpression = ((OrExpression) expr).getRightExpression();
        parameterFormatBiz.recursionProcess(parseContexts, rightExpression);
    }

    @Override
    protected Class getClazz() {
        return OrExpression.class;
    }

}
