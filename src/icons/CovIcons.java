package icons;

import com.intellij.openapi.util.IconLoader;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

/**
 * Icon holder class
 *
 * @author ice1000
 */
public interface CovIcons {
	@NotNull Icon COV_ICON = IconLoader.getIcon("/icons/csc.png");
	@NotNull Icon COV_PKG_ICON = IconLoader.getIcon("/icons/csp.png");
	@NotNull Icon COV_EXT_ICON = IconLoader.getIcon("/icons/cse.png");
	@NotNull Icon COV_BIG_ICON = IconLoader.getIcon("/icons/icon.png");

	@NotNull Icon FUNCTION_ICON = IconLoader.getIcon("/icons/function.png");
	@NotNull Icon NAMESPACE_ICON = IconLoader.getIcon("/icons/namespace.png");
	@NotNull Icon CONTROL_FLOW_ICON = IconLoader.getIcon("/icons/control_flow.png");
	@NotNull Icon JOJO_ICON = IconLoader.getIcon("/icons/jojo.png");

	@NotNull
	Icon STRUCT_ICON = IconLoader.getIcon("/icons/static.png");
	@NotNull
	Icon COLLAPSED_ICON = IconLoader.getIcon("/icons/annotationtype.png");
	@NotNull
	Icon VARIABLE_ICON = IconLoader.getIcon("/icons/variable.png");
	@NotNull
	Icon TRY_CATCH_ICON = IconLoader.getIcon("/icons/exceptionClass.png");
	@NotNull Icon SWITCH_ICON = IconLoader.getIcon("/nodes/deploy.png");
	@NotNull
	Icon BLOCK_ICON = IconLoader.getIcon("/icons/anonymousClass.png");
}
