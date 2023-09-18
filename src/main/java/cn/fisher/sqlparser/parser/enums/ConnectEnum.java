package cn.fisher.sqlparser.parser.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

@Getter
@AllArgsConstructor
public enum ConnectEnum {

    AND("AND"),

    OR("OR"),

    LEFT_PARENTHESIS("("),

    RIGHT_PARENTHESIS(")"),

    BETWEEN("BETWEEN"),

    LIKE("LIKE"),

    IN("IN"),

    NOT_IN("NOT IN"),

    EQ("="),

    NEQ("<>"),

    LT("<"),

    GT(">"),

    LT_EQ("<="),

    GT_EQ(">="),

    IS("IS"),

    IS_NOT("IS NOT");

    private String connect;

    public static ConnectEnum general(String connect) {
        if (StringUtils.isNotEmpty(connect)) {
            for (ConnectEnum b : ConnectEnum.values()) {
                if (connect.toUpperCase().equalsIgnoreCase(b.connect)) {
                    return b;
                }
            }
        }
        throw new IllegalArgumentException("未知的连接符类型！");
    }
}
