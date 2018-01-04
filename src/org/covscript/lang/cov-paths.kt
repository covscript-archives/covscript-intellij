package org.covscript.lang

import com.intellij.openapi.util.IconLoader
import javax.swing.Icon

const val COV_NAME = "CovScript"
const val COV_PKG_NAME = "CovScript Package"
const val COV_EXT_NAME = "CovScript Extension"
const val COV_EXTENSION = "csc"
const val COV_EXT_EXTENSION = "cse"
const val COV_PKG_EXTENSION = "csp"

const val COV_DESCRIPTION = "$COV_NAME Source File"
const val COV_PKG_DESCRIPTION = "$COV_NAME Package"
const val COV_EXT_DESCRIPTION = "$COV_NAME Extension File"

@JvmField val COV_ICON: Icon = IconLoader.getIcon("/icons/csc.png")
@JvmField val COV_PKG_ICON: Icon = IconLoader.getIcon("/icons/csp.png")
@JvmField val COV_EXT_ICON: Icon = IconLoader.getIcon("/icons/cse.png")
