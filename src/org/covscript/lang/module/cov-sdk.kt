package org.covscript.lang.module

import com.intellij.openapi.projectRoots.*
import com.intellij.openapi.roots.RootProvider
import com.intellij.openapi.util.Key
import com.intellij.openapi.vfs.VirtualFile
import org.covscript.lang.COV_SDK_NAME
import org.covscript.lang.COV_SDK_TYPE
import org.jdom.Element
import java.nio.file.Paths

class CovSdk(private val home: String) : Sdk {
	override fun getName() = "CovScript SDK"
	private val map = mutableMapOf<Any, Any?>()
	override fun getVersionString() = "1.2.1"
	override fun getHomePath() = home
	override fun clone() = CovSdk(home)
	override fun getSdkModificator(): SdkModificator {
	}

	override fun getRootProvider(): RootProvider {
		TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
	}

	override fun <T : Any?> getUserData(p0: Key<T>): T? {
		TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
	}

	override fun <T : Any?> putUserData(p0: Key<T>, p1: T?) {
		TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
	}

	override fun getSdkAdditionalData(): SdkAdditionalData? {
		TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
	}

	override fun getSdkType()

	override fun getHomeDirectory(): VirtualFile? {
		TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
	}

}

object CovSdkType : SdkType(COV_SDK_NAME) {
	override fun getPresentableName() = COV_SDK_TYPE

	override fun isValidSdkHome(path: String?): Boolean {
		val pathString = path ?: return false
		val csPath = Paths.get(pathString, "bin", "cs")
		val csReplPath = Paths.get(pathString, "bin", "cs_repl")
		val csExePath = Paths.get(pathString, "bin", "cs.exe")
		val csExeReplPath = Paths.get(pathString, "bin", "cs_repl.exe")
		return (csPath.isExe() || csExePath.isExe()) && (csReplPath.isExe() || csExeReplPath.isExe())
	}

	override fun suggestSdkName(s: String?, s1: String?): String {
	}

	override fun suggestHomePath(): String? {
		TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
	}

	override fun createAdditionalDataConfigurable(p0: SdkModel, p1: SdkModificator): AdditionalDataConfigurable? {
		TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
	}

	override fun saveAdditionalData(p0: SdkAdditionalData, p1: Element) {
		TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
	}
}
