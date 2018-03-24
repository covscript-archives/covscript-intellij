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
	@NotNull Icon COV_BIG_ICON = IconLoader.getIcon("/icons/cov_big.png");

	@NotNull Icon FUNCTION_ICON = IconLoader.getIcon("/icons/function_cov.png");
	@NotNull Icon NAMESPACE_ICON = IconLoader.getIcon("/icons/namespace_cov.png");
	@NotNull Icon CONTROL_FLOW_ICON = IconLoader.getIcon("/icons/control_flow.png");
	@NotNull Icon JOJO_ICON = IconLoader.getIcon("/icons/jojo.png");

	@NotNull Icon STRUCT_ICON = IconLoader.getIcon("/icons/struct_cov.png");
	@NotNull Icon COLLAPSED_ICON = IconLoader.getIcon("/icons/collapsed_cov.png");
	@NotNull Icon VARIABLE_ICON = IconLoader.getIcon("/icons/variable_cov.png");
	@NotNull Icon TRY_CATCH_ICON = IconLoader.getIcon("/icons/exception_cov.png");
	@NotNull Icon SWITCH_ICON = IconLoader.getIcon("/nodes/deploy.png");
	@NotNull Icon BLOCK_ICON = IconLoader.getIcon("/icons/block_cov.png");
}
