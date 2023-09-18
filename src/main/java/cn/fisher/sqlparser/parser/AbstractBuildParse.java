package cn.fisher.sqlparser.parser;

import com.idss.asm.demand.biz.parser.context.BuildParseContext;

import java.util.Optional;

/**
 * @author fisher
 * @date 2023-07-27: 10:07
 * process build parse abstract , we have some parseContext that is has not process.
 * @see ParameterFormatBiz#process, first, we want parse contexts and create a template,
 * after that Elements will be sorted.
 */
public abstract class AbstractBuildParse<E extends BuildParseContext> {

    /**
     * pre-condition to build
     * @return
     */
    protected abstract boolean isSupport(E e);

    /**
     * process core
     */
    protected abstract E process(E e);

    /**
     * after listener
     */
    protected abstract void listener();

    /**
     * refresh index
     */
    protected void refreshIndex(E e){
        e.initIndex();
    };

    /**
     * template method is used
     */
    public E templateMethod(E e){
        Optional.ofNullable(e).orElseThrow(() -> new IllegalArgumentException("构建biz执行失败：buildParseContext是空的！"));
        if (isSupport(e)){
            E process = process(e);
            refreshIndex(process);
            listener();
            return process;
        }
        return e;
    }

}
