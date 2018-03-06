import groovy.lang.Closure
import org.gradle.api.internal.HasConvention
import org.gradle.language.base.internal.plugins.CleanRule
import org.jetbrains.grammarkit.tasks.BaseTask
import org.jetbrains.grammarkit.tasks.GenerateLexer
import org.jetbrains.grammarkit.tasks.GenerateParser
import org.jetbrains.intellij.tasks.PatchPluginXmlTask
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.io.*
import java.nio.file.*
import java.util.concurrent.TimeUnit
import java.util.stream.Collectors

val commitHash by lazy {
	val output: String
	val process: Process = Runtime.getRuntime().exec("git rev-parse --short HEAD")
	process.waitFor(2000L, TimeUnit.MILLISECONDS)
	output = process.inputStream.`fuck kotlin! it doesn't support "use" here` {
		bufferedReader().`fuck kotlin! it doesn't support "use" here` {
			readText()
		}
	}
	process.destroy()
	output.trim()
}

val isCI = !System.getenv("CI").isNullOrBlank()

val pluginComingVersion = "1.8.1"
val pluginVersion = if (isCI) "$pluginComingVersion-$commitHash" else pluginComingVersion
val packageName = "org.covscript"
val kotlinVersion: String by extra

group = packageName
version = pluginVersion

buildscript {
	var kotlinVersion: String by extra
	var grammarKitVersion: String by extra

	grammarKitVersion = "2017.1.1"
	kotlinVersion = "1.2.30"

	repositories {
		mavenCentral()
		maven("https://jitpack.io")
	}

	dependencies {
		classpath(kotlin("gradle-plugin", kotlinVersion))
		classpath("com.github.hurricup:gradle-grammar-kit-plugin:$grammarKitVersion")
	}
}

plugins {
	id("org.jetbrains.intellij") version "0.2.18"
}

allprojects {
	apply {
		listOf(
			"kotlin",
			"java",
			"org.jetbrains.grammarkit",
			"org.jetbrains.intellij"
		).forEach {
			plugin(it)
		}
	}

	intellij {
		updateSinceUntilBuild = false
		instrumentCode = true
		if (System.getProperty("user.name") == "ice1000")
			localPath = "/home/ice1000/.local/share/JetBrains/Toolbox/apps/IDEA-U/ch-0/173.4548.28"
		else version = "2017.3"
	}
}

java {
	sourceCompatibility = JavaVersion.VERSION_1_8
	targetCompatibility = JavaVersion.VERSION_1_8
}

tasks.withType<KotlinCompile> {
	dependsOn("genParser")
	dependsOn("genLexer")
	kotlinOptions {
		jvmTarget = "1.8"
	}
}

tasks.withType<Delete> {
	dependsOn("cleanGenerated")
}

tasks.withType<PatchPluginXmlTask> {
	changeNotes(file("res/META-INF/change-notes.html").readText())
	pluginDescription(file("res/META-INF/description.html").readText())
	version(pluginVersion)
	pluginId(packageName)
	println(pluginId)
}

val SourceSet.kotlin
	get() = (this as HasConvention)
		.convention
		.getPlugin(KotlinSourceSet::class.java)
		.kotlin

java.sourceSets {
	getByName("main").run {
		java.srcDirs("src", "gen")
		kotlin.srcDirs("src", "gen")
		resources.srcDirs("res")
	}

	getByName("test").run {
		java.srcDirs("test")
		kotlin.srcDirs("test")
		resources.srcDirs("testData")
	}
}

@Suppress("FunctionName", "ConvertTryFinallyToUseCall")
inline fun <reified Closable : Closeable, reified Unit : Any>
	Closable.`fuck kotlin! it doesn't support "use" here`(block: Closable.() -> Unit): Unit = try {
	block()
} finally {
	close()
}

// TODO workaround for KT-23077
inline fun <reified TheTask : BaseTask>
	Project.genTask(name: String, noinline configuration: TheTask.() -> Unit) =
	task(name, TheTask::class, configuration)

repositories {
	mavenCentral()
}

dependencies {
	compileOnly(kotlin("stdlib", kotlinVersion))
	compile(files(Paths.get("lib", "org.eclipse.egit.github.core-2.1.5.jar")))
	testCompile("junit", "junit", "4.12")
}

task("displayCommitHash") {
	group = "help"
	description = "Display the newest commit hash"
	doFirst {
		println("Commit hash: $commitHash")
	}
}

task("isCI") {
	group = "help"
	description = "Check if it's running in a continuous-integration"
	doFirst {
		println(if (isCI) "Yes, I'm on a CI." else "No, I'm not on CI.")
	}
}

genTask<GenerateParser>("genParser") {
	group = "build setup"
	description = "Generate the Parser and PsiElement classes"
	source = "grammar/cov-grammar.bnf"
	targetRoot = "gen/"
	pathToParser = "org/covscript/lang/CovParser.java"
	pathToPsiRoot = "org/covscript/lang/psi"
	purgeOldFiles = true
}

genTask<GenerateLexer>("genLexer") {
	group = "build setup"
	description = "Generate the Lexer"
	source = "grammar/cov-lexer.flex"
	targetDir = "gen/org/covscript/lang"
	targetClass = "CovLexer"
	purgeOldFiles = true
}

task("cleanGenerated") {
	group = "build"
	description = "Remove all generated codes"
	doFirst {
		delete("gen")
	}
}
