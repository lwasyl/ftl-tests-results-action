package org.usefulness.ftl

import okio.FileSystem
import okio.Path

actual fun Path.toSource() = FileSystem.SYSTEM.source(this)
