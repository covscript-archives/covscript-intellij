package org.covscript.lang.module

import com.intellij.ide.util.projectWizard.*
import com.intellij.openapi.Disposable
import com.intellij.openapi.module.*
import com.intellij.openapi.projectRoots.Sdk
import com.intellij.openapi.projectRoots.SdkTypeId
import com.intellij.openapi.roots.ModifiableRootModel
import com.intellij.openapi.roots.ProjectRootManager
import org.covscript.lang.*
import java.nio.file.*
import java.util.concurrent.TimeUnit

class CovModuleBuilder : ModuleBuilder(), ModuleBuilderListener {
	init {
		addListener(this)
	}

	lateinit var sdk: Sdk
	override fun isSuitableSdkType(sdkType: SdkTypeId?) = sdkType is CovSdkType
	override fun getWeight() = 99
	override fun getNodeIcon() = COV_BIG_ICON
	override fun getModuleType() = CovModuleType.instance
	override fun getCustomOptionsStep(context: WizardContext, parentDisposable: Disposable): CovSetupSdkWizardStep {
		parentDisposable.dispose()
		context.projectName = COV_DEFAULT_MODULE_NAME
		return CovSetupSdkWizardStep(this)
	}

	override fun setupRootModel(model: ModifiableRootModel) {
		model.sdk = sdk
		Files.createDirectories(Paths.get(contentEntryPath, "src"))
		doAddContentEntry(model)
	}

	override fun moduleCreated(module: Module) {
		ProjectRootManager.getInstance(module.project).projectSdk = sdk
	}
}

class CovModuleType : ModuleType<CovModuleBuilder>(ID) {
	override fun getName() = COV_NAME
	override fun getNodeIcon(bool: Boolean) = COV_BIG_ICON
	override fun createModuleBuilder() = CovModuleBuilder()
	override fun getDescription() = COV_MODULE_TYPE_DESCRIPTION

	companion object InstanceHolder {
		private const val ID = "COV_MODULE_TYPE"
		@JvmStatic val instance: CovModuleType get() = ModuleTypeManager.getInstance().findByID(ID) as CovModuleType
	}
}

fun versionOf(homePath: String) = try {
	val path = Paths.get(homePath, "bin", "cs_repl").toAbsolutePath().toString()
	val process = Runtime.getRuntime().exec("$path --silent")
	//language=CovScript
	process.outputStream.use {
		it.write("runtime.info()\n".toByteArray())
		it.flush()
	}
	process.waitFor(300L, TimeUnit.MILLISECONDS)
	process.inputStream.use {
		val reader = it.bufferedReader()
		reader.readLine()
		val ret = reader.readLine().substringAfter(':').trim()
		reader.close()
		process.destroy()
		ret
	}
} catch (e: Throwable) {
	"Unknown"
}

fun validateCovSDK(pathString: String): Boolean {
	val csPath = Paths.get(pathString, "bin", "cs")
	val csReplPath = Paths.get(pathString, "bin", "cs_repl")
	val csExePath = Paths.get(pathString, "bin", "cs.exe")
	val csExeReplPath = Paths.get(pathString, "bin", "cs_repl.exe")
	return (csPath.isExe() || csExePath.isExe()) && (csReplPath.isExe() || csExeReplPath.isExe())
}

fun Path.isExe() = Files.exists(this) and Files.isExecutable(this)
