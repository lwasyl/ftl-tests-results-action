package io.github.lwasyl.ftl.cli

import okio.FileSystem
import okio.NodeJsFileSystem

actual fun platformFileSystem(): FileSystem = NodeJsFileSystem
