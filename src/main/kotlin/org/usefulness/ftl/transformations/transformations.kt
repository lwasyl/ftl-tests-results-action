package org.usefulness.ftl.transformations

import org.usefulness.ftl.model.Stacktrace

internal fun Stacktrace.shorten() = value.lines()
    .asSequence()
    .fold(mutableListOf(mutableListOf<String>())) { currentLists, line ->
        if (line.trim().startsWith("caused by", ignoreCase = true)) {
            currentLists.add(mutableListOf(line))
        } else {
            currentLists.last().add(line)
        }

        currentLists
    }
    .filter { it.isNotEmpty() }
    .map { it.take(5) }
    .flatten()
    .joinToString(separator = "\n")
    .let(Stacktrace.Companion::fromString)
