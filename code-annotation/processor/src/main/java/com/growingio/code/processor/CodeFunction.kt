package com.growingio.code.processor

/**
 * <p>
 *
 * @author cpacm 2023/4/12
 */
data class CodeFunction(
    val dir: String,
    val fileName: String,
    val methodName: String,
    val startLine: Int,
    val endLine: Int,
    val body: String,
)
