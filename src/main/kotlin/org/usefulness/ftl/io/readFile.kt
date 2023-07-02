package org.usefulness.ftl.io

import java.io.File
import java.io.InputStream

fun readFile(path: String) = File(path).readText()

fun readFile(inputStream: InputStream) = inputStream.bufferedReader().use { it.readText() }
