package cn.fisher.sqlparser.parser;

import com.idss.asm.demand.biz.parser.context.BuildParseContext;
import com.idss.asm.demand.biz.parser.context.ParseContext;
import com.idss.asm.demand.biz.parser.util.FormatParenthesisUtil;
import net.sf.jsqlparser.JSQLParserException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Optional;

/**
 * @author fisher
 * @date 2023-07-27: 16:38
 */
@Component
public class ProcessParseBiz {

    @Autowired
    private List<AbstractBuildParse> process;

    @Autowired
    private ParameterFormatBiz parameterFormatBiz;

    private final String REGEX = "\\s{1,}";

    /**
     * <h2>ProcessParseBiz -- Single table Single Condition</h2>
     * process parse sql condition will be : The input and output are consistent, because Comparison will be conducted in db,
     * if one day we only comparison less-than data ,Converting it into a tree node is a good choice for comparison in memory.
     *
     * <ul>what it can do?
     * <li>simple sql condition parse and process.</li>
     * <li>replace some special char like ", reserve '. becuase sql has " is error.</li>
     * <li>process empty char like " ", we all know that :a = 1 is equals a = " " + 1.</li>
     * <li>process order , we all know that : a = 1 and b = 2 is equals b = 2 and a = 1.</li>
     * <li>process useless char ,like : (a = 1 and b = 2) is equals a = 1 and b = 2.</li>
     * <li>base validate conform to sql.</li>
     * </ul>
     * <ul>
     * <li>Not Supported complex sql like : left join ,group by, having ,select Subquery</li>
     * <li>Not Supported merge : ax = '1' OR ax = '2' OR ax = '3' is equals ax IN ('1','2','3'), but it's not supported</li>
     * <li>Only Support connect special {@link com.idss.asm.demand.biz.parser.enums.ConnectEnum}<li/>
     * <li>Not Supported function like: to_char("start_time",'YYYYMMDDH24MMSS') readme support real value</li>
     * <li>Not Support expression calculate like: task_type <> 2 and task_type <> 1+1 ,it's not calculate</li>
     * <li>Not Support used alias.</li>
     * <li>Not Support Multi table union {@link ParameterFormatBiz#PREFIX}</li>
     * <li>Not Support ORDER BY ,this will be ignore.</li>
     * </ul>
     * @see ParameterFormatBiz is assemble data, So that it can be parsed by JSql.
     * parse result like {@link ParseContext} parseContext it's a global context, all build impl around it process.
     * @see AbstractExpression is expression parse template method. if we need add or remove implement ,operation this.
     * @see AbstractBuildParse is after parse and process other things, like sort, special process...
     * if condiftion unable to be parse, throw JSQLParserException
     * <p>
     * @param condition sql condition
     * @return
     * @throws JSQLParserException
     */
    public String process(String condition) throws JSQLParserException {
        List<ParseContext> contexts = parameterFormatBiz.process(condition);
        if (CollectionUtils.isEmpty(contexts)){
            return StringUtils.EMPTY;
        }
        BuildParseContext context = null;
        for (AbstractBuildParse parse:process
             ) {
            context = parse.templateMethod(Optional.ofNullable(context).orElse(BuildParseContext.builder().contexts(contexts).build()));
        }
        return context.toString().replaceAll(REGEX, FormatParenthesisUtil.SEPARATOR);
    }

    /**
     * contrast sql format is equals
     * @param source
     * @param target
     * @return
     * @throws JSQLParserException
     */
    public boolean contrast(String source, String target) throws JSQLParserException {
        Assert.isTrue(StringUtils.isNoneBlank(source, target), "[processParse] 对比的数据中至少有一条是空的！");
        return StringUtils.equals(process(source), process(target));
    }

}
