package org.covscript.lang

import com.intellij.openapi.util.IconLoader
import org.jetbrains.annotations.Nls
import org.jetbrains.annotations.NonNls
import javax.swing.Icon

@Nls const val COV_NAME = "CovScript"
@Nls const val COV_PKG_NAME = "CovScript Package"
@Nls const val COV_EXT_NAME = "CovScript Extension"
@NonNls const val COV_EXTENSION = "csc"
@NonNls const val COV_EXT_EXTENSION = "cse"
@NonNls const val COV_PKG_EXTENSION = "csp"

@Nls const val COV_DESCRIPTION = "$COV_NAME Source File"
@Nls const val COV_PKG_DESCRIPTION = "$COV_NAME Package"
@Nls const val COV_EXT_DESCRIPTION = "$COV_NAME Extension File"
@Nls const val COV_MODULE_TYPE_DESCRIPTION = "$COV_NAME Module Type"
@Nls const val COV_SDK_TYPE = "$COV_NAME SDK Type"

@NonNls const val COV_DEFAULT_MODULE_NAME = "my_bizarre_covscript_module"
@NonNls const val COV_SDK_NAME = "COV_SDK_NAME"
@NonNls const val COV_SDK_HOME_KEY = "COVSCRIPT_HOME"

@JvmField val COV_ICON: Icon = IconLoader.getIcon("/icons/csc.png")
@JvmField val COV_PKG_ICON: Icon = IconLoader.getIcon("/icons/csp.png")
@JvmField val COV_EXT_ICON: Icon = IconLoader.getIcon("/icons/cse.png")
@JvmField val COV_BIG_ICON: Icon = IconLoader.getIcon("/icons/cov.png")

@JvmField val JOJO_ICON: Icon = IconLoader.getIcon("/icons/jojo.png")
