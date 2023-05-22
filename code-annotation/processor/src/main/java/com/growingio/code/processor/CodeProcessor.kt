/*
 *   Copyright (c) 2023 Beijing Yishu Technology Co., Ltd.
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

package com.growingio.code.processor

import com.google.devtools.ksp.processing.*
import com.google.devtools.ksp.symbol.*
import com.google.devtools.ksp.validate
import com.growingio.code.annotation.SourceCode
import java.io.File
import java.util.Stack

class CodeProcessor(
    private val options: Map<String, String>,
    private val logger: KSPLogger,
    private val codeGenerator: CodeGenerator,
) : SymbolProcessor {

    fun log(message: String) {
        logger.logging(message)
    }

    private val sourceCodeList = arrayListOf<CodeFunction>()

    override fun process(resolver: Resolver): List<KSAnnotated> {

        val symbols = resolver.getSymbolsWithAnnotation(
            SourceCode::class.qualifiedName ?: "com.growingio.code.annotation.SourceCode"
        ).filterIsInstance<KSFunctionDeclaration>()


        logger.info("start code processor!")

        symbols.forEach { function ->
            var startLine = 0
            if (function.location is FileLocation) {
                startLine = (function.location as FileLocation).lineNumber
            }
            var endLine = Int.MAX_VALUE

            if (function.parent != null && function.parent is KSClassDeclaration) {
                function.parent!!.accept(LineNumberVisitor(startLine) {
                    endLine = it
                }, Unit)
            }

            val file = function.containingFile
            val path = file?.filePath
            val packageName = function.packageName.asString().normalize()
            var fileName = file?.fileName?.simpleClassName() ?: "unknown"

            function.annotations.filter {
                it.shortName.asString() == SourceCode::class.simpleName
            }.iterator().forEach { annotation ->
                annotation.arguments.forEach {
                    when (it.name?.asString()) {
                        "fileName" -> {
                            if ((it.value as String).isNotEmpty()) {
                                fileName = it.value as String
                            }
                        }
                    }
                }
            }

            if (path != null && fileName != "unknown") {
                log("$fileName,${function.simpleName.getShortName()},$path => $startLine == $endLine")
                val body = readSourceCode(path, startLine, endLine)
                sourceCodeList.add(
                    CodeFunction(
                        packageName, fileName, function.simpleName.getShortName(), startLine, endLine, body
                    )
                )
            }

        }

        generateCodeFile()

        return symbols.filterNot { it.validate() }.toList()
    }

    /**
     * generate file form CodeFunction
     *
     */
    private fun generateCodeFile() {
        val dir = options.get("source.dir") ?: return

        val fileDir = File(dir)
        if (!fileDir.exists()) {
            fileDir.mkdirs()
        }
        //delete all
        //fileDir.listFiles { _, name -> name.endsWith(".code") }?.forEach { it.delete() }

        sourceCodeList.groupBy { code ->
            code.dir + "." + code.fileName + ".code"
        }.forEach { (fileName, codes) ->
            val newFile = File(fileDir, fileName)
            newFile.writeText("")
            log("generate file:${newFile.path}")
            codes.forEachIndexed { index, code ->
                newFile.appendText(code.body + if (index < codes.size) "\n\n" else "")
            }
        }
    }

    /**
     * read source code from file
     * @param path file path
     */
    private fun readSourceCode(path: String, startLine: Int, endLine: Int): String {
        val file = File(path)
        var line = 1
        val functionBody = StringBuilder()
        if (file.exists()) {
            file.forEachLine {
                if (line in startLine until endLine) {
                    functionBody.append(it).appendLine()
                }
                line += 1
            }
        }
        return getFunctionString(functionBody.toString())
    }

    private fun getFunctionString(str: String): String {
        val stack = Stack<Int>()
        var isInDocType = 0

        for (i in str.indices) {
            when (str[i]) {
                '/' -> {
                    if (i + 1 in str.indices && isInDocType == 0) {
                        if (str[i + 1] == '/') {
                            isInDocType = 1
                        } else if (str[i + 1] == '*') {
                            isInDocType = 2
                        }
                    }
                }

                '*' -> {
                    if (i + 1 in str.indices && isInDocType == 2) {
                        if (str[i + 1] == '/') {
                            isInDocType = 0
                        }
                    }
                }

                '\n' -> if (isInDocType == 1) isInDocType = 0
                '{' -> if (isInDocType == 0) stack.push(i)
                '}' -> {
                    if (!stack.empty() && isInDocType == 0) {
                        stack.pop()
                        if (stack.empty()) {
                            return str.substring(0, i + 1)
                        }
                    }

                }
            }
        }
        return str
    }


    inner class LineNumberVisitor(
        private val startLine: Int, private val endLineCallback: (endLine: Int) -> Unit
    ) : KSVisitorVoid() {
        override fun visitClassDeclaration(classDeclaration: KSClassDeclaration, data: Unit) {
            var endLine = Int.MAX_VALUE
            classDeclaration.declarations.forEach {
                if (it.location is FileLocation) {
                    val declarationLine = (it.location as FileLocation).lineNumber
                    if (declarationLine in (startLine + 1) until endLine) {
                        endLine = declarationLine
                    }
                }
            }
            endLineCallback.invoke(endLine)
        }
    }

}


