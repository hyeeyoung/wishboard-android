package com.hyeeyoung.wishboard.util

import timber.log.Timber

class WishBoardDebugTree : Timber.DebugTree() {
    override fun createStackElementTag(element: StackTraceElement) =
        "${element.fileName}:${element.lineNumber}#${element.methodName}"
}