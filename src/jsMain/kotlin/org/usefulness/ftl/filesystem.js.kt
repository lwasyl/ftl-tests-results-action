package org.usefulness.ftl

import okio.NodeJsFileSystem
import okio.Path

actual fun Path.toSource() = NodeJsFileSystem.openReadOnly(this).source()
