package cn.fisher.sqlparser.parser.process;

import com.idss.asm.demand.biz.parser.AbstractExpression;
import com.idss.asm.demand.biz.parser.context.ParseContext;
import com.idss.asm.demand.biz.parser.factory.ParseContextFactory;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.operators.relational.NotEqualsTo;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author fisher
 * @date 2023-08-03: 10:06
 */
@Component
public class NotEqualsToProcess extends AbstractExpression<ParseContext> implements InitializingBean {

    @Override
    protected void handler(Expression expr, CopyOnWriteArrayList<ParseContext> parseContexts) {
        parseContexts.add(ParseContextFactory.getNeqNode((Expression) expr, parseContexts));
    }

    @Override
    protected Class getClazz() {
        return NotEqualsTo.class;
    }
}
