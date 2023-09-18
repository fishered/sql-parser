package cn.fisher.sqlparser.parser.process;

import com.idss.asm.demand.biz.parser.AbstractExpression;
import com.idss.asm.demand.biz.parser.ParameterFormatBiz;
import com.idss.asm.demand.biz.parser.context.ParseContext;
import com.idss.asm.demand.biz.parser.enums.ConnectEnum;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author fisher
 * @date 2023-07-26: 16:05
 */
@Component()
public class AndExpressionProcess extends AbstractExpression<ParseContext> implements InitializingBean {

    @Resource
    private ParameterFormatBiz<ParseContext> parameterFormatBiz;

    @Override
    protected void handler(Expression expr, CopyOnWriteArrayList<ParseContext> parseContexts) {
        Expression leftExpression = ((AndExpression) expr).getLeftExpression();
        parameterFormatBiz.recursionProcess(parseContexts, leftExpression);

        parseContexts.add(
                ParseContext.builder().opera(ConnectEnum.AND).index(parseContexts.size()).build()
        );

        Expression rightExpression = ((AndExpression) expr).getRightExpression();
        parameterFormatBiz.recursionProcess(parseContexts, rightExpression);
    }

    @Override
    protected Class getClazz() {
        return AndExpression.class;
    }
}
