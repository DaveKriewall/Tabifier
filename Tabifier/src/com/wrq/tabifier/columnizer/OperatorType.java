package com.wrq.tabifier.columnizer;

import com.intellij.psi.JavaTokenType;
import com.intellij.psi.tree.IElementType;
import com.wrq.tabifier.parse.ColumnSequenceNodeType;
import com.wrq.tabifier.settings.ColumnSetting;
import com.wrq.tabifier.settings.TabifierSettings;

/**
 * Inspects an operator token and determines an appropriate column setting and column sequence node type, based on whether the operator is
 * logical (&& or ||), relational (==, >, >=, <, <=) or arithmetic.
 */
final class OperatorType
{
    public final ColumnSetting setting;
    public final ColumnSequenceNodeType expressionType;

    public OperatorType(final IElementType tokenType,
                        final TabifierSettings settings  )
    {
        if (tokenType == JavaTokenType.ANDAND ||
                tokenType == JavaTokenType.OROR)
        {
            setting        = settings.align_logical_operators;
            expressionType = ColumnSequenceNodeType.LOGICAL_EXPRESSION;
        }
        else if (tokenType == JavaTokenType.EQEQ ||
                tokenType == JavaTokenType.GT ||
                tokenType == JavaTokenType.GE ||
                tokenType == JavaTokenType.LT ||
                tokenType == JavaTokenType.NE)
        {
            setting        = settings.align_relational_operators;
            expressionType = ColumnSequenceNodeType.RELATIONAL_EXPRESSION;
        }
        else {
            setting        = settings.align_arithmetic_operators;
            expressionType = ColumnSequenceNodeType.ARITHMETIC_EXPRESSION;
        }
    }
}
