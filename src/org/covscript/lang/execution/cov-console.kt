package org.covscript.lang.execution

import com.intellij.execution.filters.*
import com.intellij.openapi.project.Project
import com.intellij.psi.search.GlobalSearchScope
import java.util.regex.Pattern

class CovConsoleFilter(private val project: Project) : Filter {
	private companion object PatternHolder {
		private val ERROR_FILE_LOCATION = Pattern.compile("File \"([^\"]|\"[^,])+\",")
		private const val startOffset = "File \"".length
	}

	private fun default(startPoint: Int, entireLength: Int): Filter.Result? = null
			// Filter.Result(startPoint, entireLength, null)
	override fun applyFilter(line: String, entireLength: Int): Filter.Result? {
		val startPoint = entireLength - line.length
		val matcher = ERROR_FILE_LOCATION.matcher(line)
		if (matcher.find()) {
			val resultFile = project
					.baseDir
					.fileSystem
					.findFileByPath(matcher.group().drop(startOffset).dropLast(2))
					?: return default(startPoint, entireLength)
			val lineNumber = line.split(' ').lastOrNull()?.trim()?.toIntOrNull()
					?: return default(startPoint, entireLength)
			return Filter.Result(
					startPoint + matcher.start() + startOffset,
					startPoint + matcher.end() - 2,
					OpenFileHyperlinkInfo(project, resultFile, lineNumber.let { if (it > 0) it - 1 else it }))
		}
		return default(startPoint, entireLength)
	}
}

class CovConsoleFilterProvider : ConsoleFilterProviderEx {
	override fun getDefaultFilters(project: Project, scope: GlobalSearchScope) = getDefaultFilters(project)
	override fun getDefaultFilters(project: Project) = arrayOf(CovConsoleFilter(project), UrlFilter())
}

