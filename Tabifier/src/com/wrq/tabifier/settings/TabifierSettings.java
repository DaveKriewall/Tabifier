/*
 * Copyright (c) 2003, 2010, Dave Kriewall
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the
 * following conditions are met:
 *
 * 1) Redistributions of source code must retain the above copyright notice, this list of conditions and the following
 * disclaimer.
 *
 * 2) Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following
 * disclaimer in the documentation and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES,
 * INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE
 * USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.wrq.tabifier.settings;

/**
 * Contains all settings for an instance of the tabifier (one per IDEA project).
 */
public final class TabifierSettings
        extends Settings
{
    public ColumnSetting              align_annotations; // if multiple annotations per line, align them
    public ColumnSetting              align_annotation_open_parend;
    public ColumnSetting              align_annotation_value_pairs;
    public ColumnSetting              align_annotation_close_parend;
    public ColumnSetting              align_arithmetic_operators;
    public ColumnSetting              align_assignment_operators;
    public ColumnSetting              align_braces;
    public ColumnSetting              align_code_block;
    public ColumnSetting              align_colon;
    public ColumnSetting              align_typecast_type;
    public ColumnSetting              align_class;
    public ColumnSetting              align_commas;
    public ColumnSetting              align_expression_statements;
    public ColumnSetting              align_if_keywords;
    public ColumnSetting              align_if_stmt_conditionals;
    public ColumnSetting              align_if_stmt_close_parend;
    public ColumnSetting              align_if_stmt_open_parend;
    public ColumnSetting              align_logical_operators;
    public ColumnSetting              align_method_call_names;
    public ColumnSetting              align_method_declaration_initial_param_commas;
    public ColumnSetting              align_method_declaration_initial_params;
    public ColumnSetting              align_method_declaration_subsequent_param_commas;
    public ColumnSetting              align_method_declaration_subsequent_params;
    public ColumnSetting              align_method_call_close_parend;
    public ColumnSetting              align_method_call_open_parend;
    public ColumnSetting              align_method_decl_close_parend;
    public ColumnSetting              align_method_decl_open_parend;
    public RearrangeableColumnSetting align_modifiers;
    public ColumnSetting              align_multiple_statements;                       // multiple statements per line
    public ColumnSetting              align_new;
    public ColumnSetting              align_new_object;
    public ColumnSetting              align_other_close_parend;
    public ColumnSetting              align_other_open_parend;
    public ColumnSetting              align_initial_param_commas;
    public ColumnSetting              align_initial_params;
    public ColumnSetting              align_subsequent_param_commas;
    public ColumnSetting              align_subsequent_params;
    public ColumnSetting              align_question_mark;
    public ColumnSetting              align_relational_operators;
    public ColumnSetting              align_semicolons;
    public ColumnSetting              align_statements;
    public ColumnSetting              align_terms;
    public ColumnSetting              align_trailing_comments;
    public ColumnSetting              align_typecast_close_parend;
    public ColumnSetting              align_typecast_open_parend;
    public ColumnSetting              align_variable_names;
    public ColumnSetting              align_variable_types;
    public BooleanSetting             chain_from_reformat_plugin;
    public BooleanSetting             debug;
    public StringSetting              debug_output;                                    // no longer used; kept since it is the only StringSetting and used for tests
    public BooleanSetting             delimit_by_blank_lines;
    public BooleanSetting             delimit_by_non_blank_lines;
    public BooleanSetting             delimit_by_statement_type;
    public BooleanSetting             delimit_method_declarations;
    public BooleanSetting             disable_array_initializer_processing;
    public IntegerSetting             expression_parse_nesting_level;
    public BooleanSetting             force_space_before_array_initializer;
    public BooleanSetting             force_space_within_array_initializer;
    public BooleanSetting             group_assignments_with_declarations;
    public IntegerSetting             method_call_similarity_threshold;
    public BooleanSetting             no_selection_behavior;
    public BooleanSetting             no_space_before_assignment_operators;
    public BooleanSetting             right_justify_numeric_literals;
    public BooleanSetting             run_code_layout_on_preview_pane;
    public BooleanSetting             spaceBetweenEmptyParentheses;
   /**
     * the start_of_column_sequence column setting is used for a dummy TokenColumn that sits at the beginning of
     * each ColumnSequence.  Purpose of the dummy column is to provide a "left margin" tabstop for unaligned tokens
     * in the column sequence.  We want these to simply be "left aligned" in the sequence head and therefore in the
     * sequence head's parent ColumnChoice.
     */
    public               ColumnSetting start_of_column_sequence;

    private static final String ALIGN_ANNOTATIONS                                = "align_annotations";
    private static final String ALIGN_ANNOTATION_OPEN_PARENDS                    = "align_annotation_open_parends";
    private static final String ALIGN_ANNOTATION_VALUE_PAIRS                     = "align_annotation_value_pairs";
    private static final String ALIGN_ANNOTATION_CLOSE_PARENDS                   = "align_annotation_close_parends";
    private static final String ALIGN_ARITHMETIC_OPERATORS                       = "align_arithmetic_operators";
    private static final String ALIGN_ASSIGNMENT_OPERATORS                       = "align_assignment_operators";
    private static final String ALIGN_BRACES                                     = "align_braces";
    private static final String ALIGN_TYPECAST_TYPE                              = "align_typecast_type";
    private static final String ALIGN_CLASS                                      = "align_class";
    private static final String ALIGN_CODE_BLOCK                                 = "align_code_block";
    private static final String ALIGN_COLON                                      = "align_colon";
    private static final String ALIGN_COMMAS                                     = "align_commas";
    private static final String ALIGN_EXPRESSION_STATEMENTS                      = "align_expression_statements";
    private static final String ALIGN_IF_KEYWORDS                                = "align_if_keywords";
    private static final String ALIGN_IF_STMT_CONDITIONALS                       = "align_if_stmt_conditionals";
    private static final String ALIGN_IF_STMT_CLOSE_PAREND                       = "align_if_stmt_close_parend";
    private static final String ALIGN_IF_STMT_OPEN_PAREND                        = "align_if_stmt_open_parend";
    private static final String ALIGN_LOGICAL_OPERATORS                          = "align_logical_operators";
    private static final String ALIGN_METHOD_NAMES                               = "align_method_names";
    private static final String ALIGN_METHOD_CALL_CLOSE_PAREND                   = "align_method_call_close_parend";
    private static final String ALIGN_METHOD_CALL_OPEN_PAREND                    = "align_method_call_open_parend";
    private static final String ALIGN_METHOD_DECL_CLOSE_PAREND                   = "align_method_decl_close_parend";
    private static final String ALIGN_METHOD_DECL_OPEN_PAREND                    = "align_method_decl_open_parend";
    public  static final String ALIGN_METHOD_DECLARATION_INITIAL_PARAM_COMMAS    = "align_method_declaration_initial_param_commas";
    public  static final String ALIGN_METHOD_DECLARATION_INITIAL_PARAMS          = "align_method_declaration_initial_params";
    public  static final String ALIGN_METHOD_DECLARATION_SUBSEQUENT_PARAM_COMMAS = "align_method_declaration_subsequent_param_commas";
    public  static final String ALIGN_METHOD_DECLARATION_SUBSEQUENT_PARAMS       = "align_method_declaration_subsequent_params";
    private static final String ALIGN_MODIFIERS                                  = "align_modifiers";
    private static final String ALIGN_MULTIPLE_STATEMENTS                        = "align_multiple_statements";
    private static final String ALIGN_NEW                                        = "align_new";
    private static final String ALIGN_NEW_OBJECT                                 = "align_new_object";
    private static final String ALIGN_OTHER_CLOSE_PAREND                         = "align_other_close_parend";
    private static final String ALIGN_OTHER_OPEN_PAREND                          = "align_other_open_parend";
    private static final String ALIGN_PARAM_COMMAS                               = "align_parameter_commas";
    private static final String ALIGN_INITIAL_PARAM_COMMAS                       = "align_initial_param_commas";
    private static final String ALIGN_INITIAL_PARAMS                             = "align_initial_params";
    private static final String ALIGN_SUBSEQUENT_PARAM_COMMAS                    = "align_subsequent_param_commas";
    private static final String ALIGN_SUBSEQUENT_PARAMS                          = "align_subsequent_params";
    private static final String ALIGN_QUESTION_MARK                              = "align_question_mark";
    private static final String ALIGN_RELATIONAL_OPERATORS                       = "align_relational_operators";
    private static final String ALIGN_SEMICOLONS                                 = "align_semicolons";
    private static final String ALIGN_STATEMENTS                                 = "align_statements";
    private static final String ALIGN_TERMS                                      = "align_terms";
    private static final String ALIGN_TRAILING_COMMENTS                          = "align_trailing_comments";
    private static final String ALIGN_TYPECAST_CLOSE_PAREND                      = "align_typecast_close_parend";
    private static final String ALIGN_TYPECAST_OPEN_PAREND                       = "align_typecast_open_parend";
    private static final String ALIGN_VARIABLE_NAMES                             = "align_variable_names";
    private static final String ALIGN_VARIABLE_TYPES                             = "align_variable_types";
    private static final String CHAIN_FROM_REFORMAT_PLUGIN                       = "chain_from_reformat_plugin";
    private static final String DEBUG                                            = "debug";
    private static final String DEBUG_OUTPUT                                     = "debug_output";
    private static final String DELIMIT_BY_BLANK_LINES                           = "delimit_by_blank_lines";
    private static final String DELIMIT_BY_NON_BLANK_LINES                       = "delimit_by_non_blank_lines";
    private static final String DELIMIT_BY_STATEMENT_TYPE                        = "delimit_by_statement_type";
    private static final String DELIMIT_METHOD_DECLARATIONS                      = "delimit_method_declarations";
    private static final String DISABLE_ARRAY_INITIALIZER_PROCESSING             = "disable_array_initializer_processing";
    private static final String EXPRESSION_PARSE_NESTING_LEVEL                   = "expression_parse_nesting_level";
    public  static final String FORCE_SPACE_BEFORE_ARRAY_INITIALIZER             = "force_space_before_array_initializer";
    public  static final String FORCE_SPACE_WITHIN_ARRAY_INITIALIZER             = "force_space_within_array_initializer";
    private static final String GROUP_ASSIGNMENTS_WITH_DECLARATIONS              = "group_assignments_with_declarations";
    private static final String METHOD_CALL_SIMILARITY_THRESHOLD                 = "method_call_similarity_threshold";
    private static final String NO_SELECTION_BEHAVIOR                            = "no_selection_means_tabify_entire_file";
    public  static final String NO_SPACE_BEFORE_ASSIGNMENT_OPERATORS             = "no_space_before_assignment_operators";
    private static final String RIGHT_JUSTIFY_NUMERIC_LITERALS                   = "right_justify_numeric_literals";
    private static final String START_OF_COLUMN_SEQUENCE                         = "start_of_column_sequence";
    private static final String RUN_CODE_LAYOUT_ON_PREVIEW_PANE                  = "run_code_layout_on_preview_pane";
    public  static final String SPACE_BETWEEN_EMPTY_PARENTHESES                  = "spaceBetweenEmptyParentheses";

    public TabifierSettings()
    {
        addSetting(new ColumnSetting             (false,                1,                                              ALIGN_ANNOTATIONS             ));
        addSetting(new ColumnSetting             (false,                ALIGN_ANNOTATION_OPEN_PARENDS));
        addSetting(new ColumnSetting             (false,                ALIGN_ANNOTATION_VALUE_PAIRS));
        addSetting(new ColumnSetting             (false,                1, ALIGN_ANNOTATION_CLOSE_PARENDS));
        addSetting(new ColumnSetting             (true,                 1,                                              ALIGN_ARITHMETIC_OPERATORS    ));
        addSetting(new ColumnSetting             (true,                 1,                                              ALIGN_ASSIGNMENT_OPERATORS    ));
        addSetting(new ColumnSetting             (false,                ALIGN_BRACES                                                                  ));
        addSetting(new ColumnSetting             (true,                 ALIGN_TYPECAST_TYPE                                                           ));
        addSetting(new ColumnSetting             (true,                 ALIGN_CODE_BLOCK                                                              ));
        addSetting(new ColumnSetting             (true,                 ALIGN_COLON                                                                   ));
        addSetting(new ColumnSetting             (true,                 ALIGN_CLASS                                                                   ));
        addSetting(new ColumnSetting             (false,                ALIGN_COMMAS                                                                  ));
        addSetting(new ColumnSetting             (true,                 ALIGN_EXPRESSION_STATEMENTS                                                   ));
        addSetting(new ColumnSetting             (true,                 ALIGN_IF_KEYWORDS                                                             ));
        addSetting(new ColumnSetting             (true,                 1,                                              ALIGN_IF_STMT_CONDITIONALS    ));
        addSetting(new ColumnSetting             (true,                 ALIGN_IF_STMT_CLOSE_PAREND                                                    ));
        addSetting(new ColumnSetting             (true,                 1,                                              ALIGN_IF_STMT_OPEN_PAREND     ));
        addSetting(new ColumnSetting             (true,                 1,                                              ALIGN_LOGICAL_OPERATORS       ));
        addSetting(new ColumnSetting             (true,                 ALIGN_METHOD_CALL_CLOSE_PAREND                                                ));
        addSetting(new ColumnSetting             (true,                 ALIGN_METHOD_CALL_OPEN_PAREND                                                 ));
        addSetting(new ColumnSetting             (true,                 ALIGN_METHOD_DECL_CLOSE_PAREND                                                ));
        addSetting(new ColumnSetting             (true,                 ALIGN_METHOD_DECL_OPEN_PAREND                                                 ));
        addSetting(new ColumnSetting             (false,                ALIGN_METHOD_DECLARATION_INITIAL_PARAM_COMMAS                                 ));
        addSetting(new ColumnSetting             (true,                 ALIGN_METHOD_DECLARATION_INITIAL_PARAMS                                       ));
        addSetting(new ColumnSetting             (false,                ALIGN_METHOD_DECLARATION_SUBSEQUENT_PARAM_COMMAS                              ));
        addSetting(new ColumnSetting             (false,                ALIGN_METHOD_DECLARATION_SUBSEQUENT_PARAMS                                    ));
        addSetting(new ColumnSetting             (true,                 ALIGN_METHOD_NAMES                                                            ));
        addSetting(new RearrangeableColumnSetting(true,                 0,                                              ALIGN_MODIFIERS,          true));
        addSetting(new ColumnSetting             (true,                 1,                                              ALIGN_MULTIPLE_STATEMENTS     ));
        addSetting(new ColumnSetting             (true,                 ALIGN_NEW                                                                     ));
        addSetting(new ColumnSetting             (true,                 1,                                              ALIGN_NEW_OBJECT              ));
        addSetting(new ColumnSetting             (true,                 ALIGN_OTHER_CLOSE_PAREND                                                      ));
        addSetting(new ColumnSetting             (true,                 ALIGN_OTHER_OPEN_PAREND                                                       ));
        addSetting(new ColumnSetting             (true,                 ALIGN_PARAM_COMMAS                                                            ));
        addSetting(new ColumnSetting             (false,                ALIGN_INITIAL_PARAM_COMMAS                                                    ));
        addSetting(new ColumnSetting             (true,                 ALIGN_INITIAL_PARAMS                                                          ));
        addSetting(new ColumnSetting             (false,                ALIGN_SUBSEQUENT_PARAM_COMMAS                                                 ));
        addSetting(new ColumnSetting             (false,                ALIGN_SUBSEQUENT_PARAMS                                                       ));
        addSetting(new ColumnSetting             (true,                 1,                                              ALIGN_RELATIONAL_OPERATORS    ));
        addSetting(new ColumnSetting             (true,                 ALIGN_QUESTION_MARK                                                           ));
        addSetting(new ColumnSetting             (false,                ALIGN_SEMICOLONS                                                              ));
        addSetting(new ColumnSetting             (true,                 ALIGN_STATEMENTS                                                              ));
        addSetting(new ColumnSetting             (true,                 ALIGN_TERMS                                                                   ));
        addSetting(new ColumnSetting             (true,                 1,                                              ALIGN_TRAILING_COMMENTS       ));
        addSetting(new ColumnSetting             (true,                 ALIGN_TYPECAST_CLOSE_PAREND                                                   ));
        addSetting(new ColumnSetting             (true,                 ALIGN_TYPECAST_OPEN_PAREND                                                    ));
        addSetting(new ColumnSetting             (true,                 1,                                              ALIGN_VARIABLE_NAMES          ));
        addSetting(new ColumnSetting             (true,                 1,                                              ALIGN_VARIABLE_TYPES          ));
        addSetting(new BooleanSetting            (true,                 CHAIN_FROM_REFORMAT_PLUGIN                                                    ));
        addSetting(new BooleanSetting            (false,                DEBUG                                                                         ));
        addSetting(new StringSetting             ("/temp/tabifier.txt", DEBUG_OUTPUT                                                                  ));
        addSetting(new BooleanSetting            (false,                DELIMIT_BY_BLANK_LINES                                                        ));
        addSetting(new BooleanSetting            (true,                 DELIMIT_BY_NON_BLANK_LINES                                                    ));
        addSetting(new BooleanSetting            (true,                 DELIMIT_BY_STATEMENT_TYPE                                                     ));
        addSetting(new BooleanSetting            (false,                DELIMIT_METHOD_DECLARATIONS                                                   ));
        addSetting(new BooleanSetting            (false,                DISABLE_ARRAY_INITIALIZER_PROCESSING                                          ));
        addSetting(new IntegerSetting            (2,                    EXPRESSION_PARSE_NESTING_LEVEL                                                ));
        addSetting(new BooleanSetting            (false,                FORCE_SPACE_BEFORE_ARRAY_INITIALIZER                                          ));
        addSetting(new BooleanSetting            (false,                FORCE_SPACE_WITHIN_ARRAY_INITIALIZER                                          ));
        addSetting(new BooleanSetting            (false,                GROUP_ASSIGNMENTS_WITH_DECLARATIONS                                           ));
        addSetting(new IntegerSetting            (4,                    METHOD_CALL_SIMILARITY_THRESHOLD                                              ));
        addSetting(new BooleanSetting            (false,                NO_SELECTION_BEHAVIOR                                                         ));
        addSetting(new BooleanSetting            (false,                NO_SPACE_BEFORE_ASSIGNMENT_OPERATORS                                          ));
        addSetting(new BooleanSetting            (true,                 RIGHT_JUSTIFY_NUMERIC_LITERALS                                                ));
        addSetting(new BooleanSetting            (false,                RUN_CODE_LAYOUT_ON_PREVIEW_PANE                                               ));
        addSetting(new BooleanSetting            (false,                SPACE_BETWEEN_EMPTY_PARENTHESES                                               ));
        addSetting(new ColumnSetting             (true,                 START_OF_COLUMN_SEQUENCE                                                      ));
        initialize(                                                                                                                                    );
    }

    protected void initialize()
    {
        align_annotations                                = (ColumnSetting             ) find(ALIGN_ANNOTATIONS                               );
        align_annotation_open_parend                     = (ColumnSetting             ) find(ALIGN_ANNOTATION_OPEN_PARENDS                   );
        align_annotation_value_pairs                     = (ColumnSetting             ) find(ALIGN_ANNOTATION_VALUE_PAIRS                    );
        align_annotation_close_parend                    = (ColumnSetting             ) find(ALIGN_ANNOTATION_CLOSE_PARENDS                  );
        align_arithmetic_operators                       = (ColumnSetting             ) find(ALIGN_ARITHMETIC_OPERATORS                      );
        align_assignment_operators                       = (ColumnSetting             ) find(ALIGN_ASSIGNMENT_OPERATORS                      );
        align_braces                                     = (ColumnSetting             ) find(ALIGN_BRACES                                    );
        align_typecast_type                              = (ColumnSetting             ) find(ALIGN_TYPECAST_TYPE                             );
        align_class                                      = (ColumnSetting             ) find(ALIGN_CLASS                                     );
        align_colon                                      = (ColumnSetting             ) find(ALIGN_COLON                                     );
        align_commas                                     = (ColumnSetting             ) find(ALIGN_COMMAS                                    );
        align_code_block                                 = (ColumnSetting             ) find(ALIGN_CODE_BLOCK                                );
        align_expression_statements                      = (ColumnSetting             ) find(ALIGN_EXPRESSION_STATEMENTS                     );
        align_if_keywords                                = (ColumnSetting             ) find(ALIGN_IF_KEYWORDS                               );
        align_if_stmt_conditionals                       = (ColumnSetting             ) find(ALIGN_IF_STMT_CONDITIONALS                      );
        align_if_stmt_close_parend                       = (ColumnSetting             ) find(ALIGN_IF_STMT_CLOSE_PAREND                      );
        align_if_stmt_open_parend                        = (ColumnSetting             ) find(ALIGN_IF_STMT_OPEN_PAREND                       );
        align_logical_operators                          = (ColumnSetting             ) find(ALIGN_LOGICAL_OPERATORS                         );
        align_method_call_close_parend                   = (ColumnSetting             ) find(ALIGN_METHOD_CALL_CLOSE_PAREND                  );
        align_method_call_open_parend                    = (ColumnSetting             ) find(ALIGN_METHOD_CALL_OPEN_PAREND                   );
        align_method_decl_close_parend                   = (ColumnSetting             ) find(ALIGN_METHOD_DECL_CLOSE_PAREND                  );
        align_method_decl_open_parend                    = (ColumnSetting             ) find(ALIGN_METHOD_DECL_OPEN_PAREND                   );
        align_method_declaration_initial_param_commas    = (ColumnSetting             ) find(ALIGN_METHOD_DECLARATION_INITIAL_PARAM_COMMAS   );
        align_method_declaration_initial_params          = (ColumnSetting             ) find(ALIGN_METHOD_DECLARATION_INITIAL_PARAMS         );
        align_method_declaration_subsequent_param_commas = (ColumnSetting             ) find(ALIGN_METHOD_DECLARATION_SUBSEQUENT_PARAM_COMMAS);
        align_method_declaration_subsequent_params       = (ColumnSetting             ) find(ALIGN_METHOD_DECLARATION_SUBSEQUENT_PARAMS      );
        align_method_call_names                          = (ColumnSetting             ) find(ALIGN_METHOD_NAMES                              );
        align_modifiers                                  = (RearrangeableColumnSetting) find(ALIGN_MODIFIERS                                 );
        align_multiple_statements                        = (ColumnSetting             ) find(ALIGN_MULTIPLE_STATEMENTS                       );
        align_new                                        = (ColumnSetting             ) find(ALIGN_NEW                                       );
        align_new_object                                 = (ColumnSetting             ) find(ALIGN_NEW_OBJECT                                );
        align_other_close_parend                         = (ColumnSetting             ) find(ALIGN_OTHER_CLOSE_PAREND                        );
        align_other_open_parend                          = (ColumnSetting             ) find(ALIGN_OTHER_OPEN_PAREND                         );
        align_initial_param_commas                       = (ColumnSetting             ) find(ALIGN_INITIAL_PARAM_COMMAS                      );
        align_initial_params                             = (ColumnSetting             ) find(ALIGN_INITIAL_PARAMS                            );
        align_subsequent_param_commas                    = (ColumnSetting             ) find(ALIGN_SUBSEQUENT_PARAM_COMMAS                   );
        align_subsequent_params                          = (ColumnSetting             ) find(ALIGN_SUBSEQUENT_PARAMS                         );
        align_question_mark                              = (ColumnSetting             ) find(ALIGN_QUESTION_MARK                             );
        align_relational_operators                       = (ColumnSetting             ) find(ALIGN_RELATIONAL_OPERATORS                      );
        align_semicolons                                 = (ColumnSetting             ) find(ALIGN_SEMICOLONS                                );
        align_statements                                 = (ColumnSetting             ) find(ALIGN_STATEMENTS                                );
        align_terms                                      = (ColumnSetting             ) find(ALIGN_TERMS                                     );
        align_trailing_comments                          = (ColumnSetting             ) find(ALIGN_TRAILING_COMMENTS                         );
        align_typecast_close_parend                      = (ColumnSetting             ) find(ALIGN_TYPECAST_CLOSE_PAREND                     );
        align_typecast_open_parend                       = (ColumnSetting             ) find(ALIGN_TYPECAST_OPEN_PAREND                      );
        align_variable_names                             = (ColumnSetting             ) find(ALIGN_VARIABLE_NAMES                            );
        align_variable_types                             = (ColumnSetting             ) find(ALIGN_VARIABLE_TYPES                            );
        chain_from_reformat_plugin                       = (BooleanSetting            ) find(CHAIN_FROM_REFORMAT_PLUGIN                      );
        debug                                            = (BooleanSetting            ) find(DEBUG                                           );
        debug_output                                     = (StringSetting             ) find(DEBUG_OUTPUT                                    );
        delimit_by_blank_lines                           = (BooleanSetting            ) find(DELIMIT_BY_BLANK_LINES                          );
        delimit_by_non_blank_lines                       = (BooleanSetting            ) find(DELIMIT_BY_NON_BLANK_LINES                      );
        delimit_by_statement_type                        = (BooleanSetting            ) find(DELIMIT_BY_STATEMENT_TYPE                       );
        delimit_method_declarations                      = (BooleanSetting            ) find(DELIMIT_METHOD_DECLARATIONS                     );
        disable_array_initializer_processing             = (BooleanSetting            ) find(DISABLE_ARRAY_INITIALIZER_PROCESSING            );
        expression_parse_nesting_level                   = (IntegerSetting            ) find(EXPRESSION_PARSE_NESTING_LEVEL                  );
        force_space_before_array_initializer             = (BooleanSetting            ) find(FORCE_SPACE_BEFORE_ARRAY_INITIALIZER            );
        force_space_within_array_initializer             = (BooleanSetting            ) find(FORCE_SPACE_WITHIN_ARRAY_INITIALIZER            );
        group_assignments_with_declarations              = (BooleanSetting            ) find(GROUP_ASSIGNMENTS_WITH_DECLARATIONS             );
        method_call_similarity_threshold                 = (IntegerSetting            ) find(METHOD_CALL_SIMILARITY_THRESHOLD                );
        no_selection_behavior                            = (BooleanSetting            ) find(NO_SELECTION_BEHAVIOR                           );
        no_space_before_assignment_operators             = (BooleanSetting            ) find(NO_SPACE_BEFORE_ASSIGNMENT_OPERATORS            );
        right_justify_numeric_literals                   = (BooleanSetting            ) find(RIGHT_JUSTIFY_NUMERIC_LITERALS                  );
        run_code_layout_on_preview_pane                  = (BooleanSetting            ) find(RUN_CODE_LAYOUT_ON_PREVIEW_PANE                 );
        spaceBetweenEmptyParentheses                     = (BooleanSetting            ) find(SPACE_BETWEEN_EMPTY_PARENTHESES                 );
        start_of_column_sequence                         = (ColumnSetting             ) find(START_OF_COLUMN_SEQUENCE                        );
    }
}
