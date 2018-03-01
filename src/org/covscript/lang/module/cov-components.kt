package org.covscript.lang.module


import com.intellij.ide.plugins.PluginManager
import com.intellij.notification.*
import com.intellij.openapi.components.ProjectComponent
import com.intellij.openapi.extensions.PluginId
import com.intellij.openapi.project.Project
import org.covscript.lang.*
import org.jetbrains.annotations.Nls

class CovApplicationComponent(private val project: Project) : ProjectComponent {
	var isNightlyNotificationShown = false

	override fun getComponentName() = "CovApplicationComponent"
	override fun projectOpened() {
		super.projectOpened()
		val isNightly = PluginManager.getPlugin(PluginId.getId(COV_PLUGIN_ID))?.run { '-' in version }.orFalse()
		if (!validateCovExe(project.covSettings.settings)) notify(
				CovBundle.message("cov.messages.notify.invalid-julia.title"),
				CovBundle.message("cov.messages.notify.invalid-julia.content"),
				NotificationType.WARNING)
		if (isNightly and !isNightlyNotificationShown) {
			isNightlyNotificationShown = true
			notify(
					CovBundle.message("cov.messages.notify.nightly.title"),
					CovBundle.message("cov.messages.notify.nightly.content"))
		}
	}

	/** 好想把函数名写成 hugify 。。。 */
	private fun notify(@Nls title: String, @Nls content: String, type: NotificationType = NotificationType.INFORMATION) {
		val notification = NotificationGroup(
				CovBundle.message("cov.messages.notify.group"),
				NotificationDisplayType.STICKY_BALLOON,
				true)
				.createNotification(title, content, type, NotificationListener.URL_OPENING_LISTENER)
		Notifications.Bus.notify(notification, project)
	}
}

