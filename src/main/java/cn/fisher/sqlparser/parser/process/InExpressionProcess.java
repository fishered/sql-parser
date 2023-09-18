package cn.fisher.sqlparser.parser.process;

import com.idss.asm.demand.biz.parser.AbstractExpression;
import com.idss.asm.demand.biz.parser.context.ParseContext;
import com.idss.asm.demand.biz.parser.factory.ParseContextFactory;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.operators.relational.InExpression;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author fisher
 * @date 2023-08-02: 11:06
 */
@Component
public class InExpressionProcess extends AbstractExpression<ParseContext> implements InitializingBean {
    @Override
    protected void handler(Expression expr, CopyOnWriteArrayList<ParseContext> parseContexts) {
        parseContexts.add(
                ParseContextFactory.getInNode(expr, parseContexts)
        );
    }

    @Override
    protected Class getClazz() {
        return InExpression.class;
    }
}
