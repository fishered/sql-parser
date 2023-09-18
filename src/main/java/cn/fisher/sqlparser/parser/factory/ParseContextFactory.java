package cn.fisher.sqlparser.parser.factory;

import com.idss.asm.demand.biz.parser.context.ParseContext;
import com.idss.asm.demand.biz.parser.enums.ConnectEnum;
import com.idss.asm.demand.biz.parser.util.FormatParenthesisUtil;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.operators.relational.*;
import net.sf.jsqlparser.parser.SimpleNode;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author fisher
 * @date 2023-07-26: 15:19
 */
public class ParseContextFactory {

    /**
     * process expression node
     * @param expression
     * @return
     */
    public static ParseContext getNode(Expression expression, List<ParseContext> contexts) {
        SimpleNode astNode = expression.getASTNode();
        Expression node = (Expression) astNode.jjtGetValue();

        return ParseContext.builder()
                .opera(ConnectEnum.general(((EqualsTo) node).getStringExpression()))
                .column(((EqualsTo) node).getLeftExpression().toString())
                .condition(((EqualsTo) node).getRightExpression().toString())
                .index(contexts.size())
                .build();
    }

    public static ParseContext getLikeNode(Expression expression, List<ParseContext> contexts) {
        SimpleNode astNode = expression.getASTNode();
        return ParseContext.builder()
                .opera(ConnectEnum.general(String.valueOf(astNode.jjtGetFirstToken())))
                .column(String.valueOf(((LikeExpression) expression).getLeftExpression()))
                .condition(String.valueOf(astNode.jjtGetLastToken()))
                .index(contexts.size())
                .build();
    }

    public static ParseContext getBetweenNode(Expression expression, List<ParseContext> contexts) {
        String start = String.valueOf(((Between) expression).getBetweenExpressionStart());
        String end = String.valueOf(((Between) expression).getBetweenExpressionEnd());

        return ParseContext.builder()
                .opera(ConnectEnum.BETWEEN)
                .column(String.valueOf(((Between) expression).getLeftExpression()))
                .condition(start + FormatParenthesisUtil.SEPARATOR + ConnectEnum.AND + FormatParenthesisUtil.SEPARATOR + end)
                .index(contexts.size())
                .build();
    }

    public static ParseContext getInNode(Expression expression, List<ParseContext> contexts) {
        boolean isNot = ((InExpression) expression).isNot();

        List<Expression> expressions = ((ExpressionList) ((InExpression) expression).getRightItemsList()).getExpressions();
        if (CollectionUtils.isEmpty(expressions)){
            throw new IllegalArgumentException("解析[IN]条件错误！条件内容为空！");
        }
        String condition = expressions.stream().map(e -> String.valueOf(e)).sorted().collect(Collectors.joining(","));

        return ParseContext.builder()
                .opera(isNot?ConnectEnum.NOT_IN:ConnectEnum.IN)
                .column(String.valueOf(((InExpression) expression).getLeftExpression()))
                .condition(ConnectEnum.LEFT_PARENTHESIS.getConnect() + condition + ConnectEnum.RIGHT_PARENTHESIS.getConnect())
                .index(contexts.size())
                .build();
    }

    public static ParseContext getLessThanNode(Expression expression, List<ParseContext> contexts) {
        SimpleNode astNode = expression.getASTNode();
        Expression node = (Expression) astNode.jjtGetValue();

        return ParseContext.builder()
                .opera(ConnectEnum.general(((MinorThan) node).getStringExpression()))
                .column(((MinorThan) node).getLeftExpression().toString())
                .condition(((MinorThan) node).getRightExpression().toString())
                .index(contexts.size())
                .build();
    }

    public static ParseContext getLessEqualsNode(Expression expression, List<ParseContext> contexts) {
        SimpleNode astNode = expression.getASTNode();
        Expression node = (Expression) astNode.jjtGetValue();

        return ParseContext.builder()
                .opera(ConnectEnum.general(((MinorThanEquals) node).getStringExpression()))
                .column(((MinorThanEquals) node).getLeftExpression().toString())
                .condition(((MinorThanEquals) node).getRightExpression().toString())
                .index(contexts.size())
                .build();
    }

    public static ParseContext getGreaterThanNode(Expression expression, List<ParseContext> contexts) {
        SimpleNode astNode = expression.getASTNode();
        Expression node = (Expression) astNode.jjtGetValue();

        return ParseContext.builder()
                .opera(ConnectEnum.general(((GreaterThan) node).getStringExpression()))
                .column(((GreaterThan) node).getLeftExpression().toString())
                .condition(((GreaterThan) node).getRightExpression().toString())
                .index(contexts.size())
                .build();
    }

    public static ParseContext getGreaterEqualsNode(Expression expression, List<ParseContext> contexts) {
        SimpleNode astNode = expression.getASTNode();
        Expression node = (Expression) astNode.jjtGetValue();

        return ParseContext.builder()
                .opera(ConnectEnum.general(((GreaterThanEquals) node).getStringExpression()))
                .column(((GreaterThanEquals) node).getLeftExpression().toString())
                .condition(((GreaterThanEquals) node).getRightExpression().toString())
                .index(contexts.size())
                .build();
    }

    public static ParseContext getNeqNode(Expression expression, List<ParseContext> contexts) {
        SimpleNode astNode = expression.getASTNode();
        Expression node = (Expression) astNode.jjtGetValue();

        return ParseContext.builder()
                .opera(ConnectEnum.NEQ)
                .column(((NotEqualsTo) node).getLeftExpression().toString())
                .condition(((NotEqualsTo) node).getRightExpression().toString())
                .index(contexts.size())
                .build();
    }

    public static ParseContext getIsNullNode(Expression expression, List<ParseContext> contexts) {
        boolean isNot = ((IsNullExpression) expression).isNot();

        return ParseContext.builder()
                .opera(isNot? ConnectEnum.IS_NOT: ConnectEnum.IS)
                .column(((IsNullExpression) expression).getLeftExpression().toString())
                .condition(FormatParenthesisUtil.NULL)
                .index(contexts.size())
                .build();
    }

    public static ParseContext getIsBooleanNode(Expression expression, List<ParseContext> contexts) {
        boolean isNot = ((IsBooleanExpression) expression).isNot();
        boolean isTrue = ((IsBooleanExpression) expression).isTrue();

        return ParseContext.builder()
                .opera(isNot? ConnectEnum.IS_NOT: ConnectEnum.IS)
                .column(((IsBooleanExpression) expression).getLeftExpression().toString())
                .condition(isTrue? FormatParenthesisUtil.IS_TRUE: FormatParenthesisUtil.IS_FALSE)
                .index(contexts.size())
                .build();
    }

}
