package org.covscript.lang.module

import com.google.common.util.concurrent.SimpleTimeLimiter
import com.intellij.ide.util.projectWizard.*
import com.intellij.openapi.Disposable
import com.intellij.openapi.module.*
import com.intellij.openapi.project.Project
import com.intellij.openapi.projectRoots.Sdk
import com.intellij.openapi.projectRoots.SdkTypeId
import com.intellij.openapi.roots.ModifiableRootModel
import com.intellij.openapi.roots.ProjectRootManager
import org.covscript.lang.*
import java.nio.file.*
import java.util.concurrent.TimeUnit
import java.util.stream.Collectors

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

class CovModuleType : ModuleType<CovModuleBuilder>(COV_MODULE_ID) {
	override fun getName() = CovBundle.message("cov.name")
	override fun getNodeIcon(bool: Boolean) = COV_BIG_ICON
	override fun createModuleBuilder() = CovModuleBuilder()
	override fun getDescription() = CovBundle.message("cov.modules.type")

	companion object InstanceHolder {
		@JvmStatic val instance: CovModuleType get() = ModuleTypeManager.getInstance().findByID(COV_MODULE_ID) as CovModuleType
	}
}

const val GET_VERSION_TIME_PERIOD = 500L
//language=CovScript
fun versionOf(homePath: String, timeLimit: Long = GET_VERSION_TIME_PERIOD) = executeInRepl(homePath, """
runtime.info()
system.exit(0)0
""", timeLimit)
		.first
		.firstOrNull { it.startsWith("version", true) }
		?.run { substringAfter(':').trim() }
		?: CovBundle.message("cov.modules.sdk.unknown-version")

/**
 * @return (stdout, stderr)
 */
fun executeInRepl(homePath: String, code: String, timeLimit: Long): Pair<List<String>, List<String>> {
	var processRef: Process? = null
	var output: List<String> = emptyList()
	var outputErr: List<String> = emptyList()
	try {
		val path = Paths.get(homePath, "bin", "cs_repl").toAbsolutePath().toString()
		SimpleTimeLimiter().callWithTimeout({
			val process: Process = Runtime.getRuntime().exec("$path --silent")
			processRef = process
			process.outputStream.use {
				it.write(code.toByteArray())
				it.flush()
			}
			process.waitFor(timeLimit, TimeUnit.MILLISECONDS)
			process.inputStream.use {
				val reader = it.bufferedReader()
				output = reader.lines().map {
					it.trimStart('.', '>')
				}.collect(Collectors.toList()).dropLast(1)
				forceRun(reader::close)
			}
			process.errorStream.use {
				val reader = it.bufferedReader()
				outputErr = reader.lines().collect(Collectors.toList()).dropLast(1)
				forceRun(reader::close)
			}
			forceRun(process::destroy)
		}, timeLimit + 100, TimeUnit.MILLISECONDS, true)
	} catch (e: Throwable) {
		processRef?.destroy()
	}
	return output to outputErr
}

fun validateCovSDK(pathString: String): Boolean {
	val csPath = Paths.get(pathString, "bin", "cs")
	val csReplPath = Paths.get(pathString, "bin", "cs_repl")
	val csExePath = Paths.get(pathString, "bin", "cs.exe")
	val csExeReplPath = Paths.get(pathString, "bin", "cs_repl.exe")
	return (csPath.isExe() || csExePath.isExe()) && (csReplPath.isExe() || csExeReplPath.isExe())
}

fun Path.isExe() = Files.exists(this) and Files.isExecutable(this)

val Project.projectSdk get() = ProjectRootManager.getInstance(this).projectSdk