package cn.fisher.sqlparser.parser;

import com.idss.asm.demand.biz.parser.context.ParseContext;
import net.sf.jsqlparser.expression.Expression;
import org.springframework.beans.factory.InitializingBean;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author fisher
 * @date 2023-07-26: 15:57
 * abstract process expression and template
 */
public abstract class AbstractExpression<P extends ParseContext> implements InitializingBean {

    public static ConcurrentHashMap<Class, AbstractExpression> register = new ConcurrentHashMap<>();

    /**
     * is allow process
     * @return
     */
//    abstract protected boolean isSupport(Expression expr, CopyOnWriteArrayList<ParseContext> parseContexts);

    public boolean isSupport(Expression expr, CopyOnWriteArrayList<ParseContext> parseContexts){
//        return expr != null && CollectionUtils.isNotEmpty(parseContexts);
        return true;
    }

    /**
     * process core
     * @return
     */
    abstract protected void handler(Expression expr, CopyOnWriteArrayList<ParseContext> parseContexts);

    /**
     * mapping jsqlparse type
     * @return
     */
    abstract protected Class getClazz();

    /**
     * ioc
     * @throws Exception
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        if (getClazz() == null){
            return;
        }
        register.put(getClazz(), this);
    }
}
