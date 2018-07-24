import org.gradle.language.base.internal.plugins.CleanRule
import org.jetbrains.grammarkit.GrammarKitPluginExtension
import org.jetbrains.grammarkit.tasks.BaseTask
import org.jetbrains.grammarkit.tasks.GenerateLexer
import org.jetbrains.grammarkit.tasks.GenerateParser
import org.jetbrains.intellij.tasks.PatchPluginXmlTask
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.io.*

val commitHash by lazy {
	val output: String
	val process: Process = Runtime.getRuntime().exec("git rev-parse --short HEAD")
	process.waitFor()
	output = process.inputStream.use { it.bufferedReader().use { it.readText() } }
	process.destroy()
	output.trim()
}

val isCI = !System.getenv("CI").isNullOrBlank()

val pluginComingVersion = "1.9.2"
val pluginVersion = if (isCI) "$pluginComingVersion-$commitHash" else pluginComingVersion
val packageName = "org.covscript"
val kotlinVersion = "1.2.41"

group = packageName
version = pluginVersion

plugins {
	idea
	java
	id("org.jetbrains.intellij") version "0.3.1"
	id("org.jetbrains.grammarkit") version "2018.1.7"
	kotlin("jvm") version "1.2.41"
}

apply { plugin("org.jetbrains.grammarkit") }
configure<GrammarKitPluginExtension> {
	grammarKitRelease = "2017.1.5"
}

idea {
	module {
		// https://github.com/gradle/kotlin-dsl/issues/537/
		excludeDirs = excludeDirs + file("pinpoint_piggy")
	}
}

intellij {
	updateSinceUntilBuild = false
	instrumentCode = true
	when {
		System.getProperty("user.name") == "ice1000" -> {
			val root = "/home/ice1000/.local/share/JetBrains/Toolbox/apps"
			localPath = "$root/IDEA-U/ch-0/181.5540.7"
			alternativeIdePath = "$root/PyCharm-C/ch-0/181.5087.37"
		}
		else -> version = "2018.1"
	}
}

java {
	sourceCompatibility = JavaVersion.VERSION_1_8
	targetCompatibility = JavaVersion.VERSION_1_8
}

tasks.withType<PatchPluginXmlTask> {
	changeNotes(file("res/META-INF/change-notes.html").readText())
	pluginDescription(file("res/META-INF/description.html").readText())
	version(pluginVersion)
	pluginId(packageName)
}

java.sourceSets {
	"main" {
		java.srcDirs("src", "gen")
		withConvention(KotlinSourceSet::class) {
			kotlin.srcDirs("src")
		}
		resources.srcDirs("res")
	}

	"test" {
		java.srcDirs("test")
		withConvention(KotlinSourceSet::class) {
			kotlin.srcDirs("test")
		}
		resources.srcDirs("testData")
	}
}

repositories { jcenter() }

dependencies {
	compileOnly(kotlin("stdlib-jdk8", kotlinVersion))
	compile(kotlin("stdlib-jdk8", kotlinVersion).toString()) {
		exclude(module = "kotlin-runtime")
		exclude(module = "kotlin-reflect")
		exclude(module = "kotlin-stdlib")
	}
	compile(files("lib/org.eclipse.egit.github.core-2.1.5.jar"))
	testCompile(kotlin("test-junit", kotlinVersion))
	testCompile("junit", "junit", "4.12")
}

task("displayCommitHash") {
	group = "help"
	description = "Display the newest commit hash"
	doFirst { println("Commit hash: $commitHash") }
}

task("isCI") {
	group = "help"
	description = "Check if it's running in a continuous-integration"
	doFirst { println(if (isCI) "Yes, I'm on a CI." else "No, I'm not on CI.") }
}

val genParser = task<GenerateParser>("genParser") {
	group = "build setup"
	description = "Generate the Parser and PsiElement classes"
	source = "grammar/cov-grammar.bnf"
	targetRoot = "gen/"
	pathToParser = "org/covscript/lang/CovParser.java"
	pathToPsiRoot = "org/covscript/lang/psi"
	purgeOldFiles = true
}

val genLexer = task<GenerateLexer>("genLexer") {
	group = "build setup"
	description = "Generate the Lexer"
	source = "grammar/cov-lexer.flex"
	targetDir = "gen/org/covscript/lang"
	targetClass = "CovLexer"
	purgeOldFiles = true
}

val cleanGenerated = task("cleanGenerated") {
	group = tasks["clean"].group
	description = "Remove all generated codes"
	doFirst { delete("gen") }
}

tasks.withType<KotlinCompile> {
	dependsOn(genParser)
	dependsOn(genLexer)
	kotlinOptions {
		jvmTarget = "1.8"
		languageVersion = "1.2"
		apiVersion = "1.2"
	}
}

tasks.withType<Delete> { dependsOn(cleanGenerated) }
