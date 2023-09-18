package cn.fisher.sqlparser.parser.process;

import com.idss.asm.demand.biz.parser.AbstractExpression;
import com.idss.asm.demand.biz.parser.ParameterFormatBiz;
import com.idss.asm.demand.biz.parser.context.ParseContext;
import com.idss.asm.demand.biz.parser.factory.ParseContextFactory;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.operators.relational.GreaterThan;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author fisher
 * @date 2023-07-26: 16:05
 */
@Component
public class GreaterThanProcess extends AbstractExpression<ParseContext> implements InitializingBean {

    @Resource
    private ParameterFormatBiz<ParseContext> parameterFormatBiz;

    @Override
    protected void handler(Expression expr, CopyOnWriteArrayList<ParseContext> parseContexts) {
        parseContexts.add(
                ParseContextFactory.getGreaterThanNode(expr, parseContexts)
        );
    }

    @Override
    protected Class getClazz() {
        return GreaterThan.class;
    }
}
