package geogebra.common.kernel.geos;

import geogebra.common.kernel.arithmetic.ExpressionNodeConstants;
import geogebra.common.util.CopyPaste;
import geogebra.common.util.Unicode;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

public class LabelManager {
	private static Set<String> invalidFunctionNames = new HashSet<String>(ExpressionNodeConstants.RESERVED_FUNCTION_NAMES);
	static
	{
		invalidFunctionNames.addAll(Arrays.asList("x", "y", Unicode.IMAGINARY));
	}

		/**
		 * Checks whether name can be used as label
		 * @param geo geo to be checked
		 * @param name potential label
		 * @return true for valid labels
		 */
		public static boolean checkName(GeoElement geo, String name) {
			if (name == null) return true;

			if (name.startsWith(CopyPaste.labelPrefix))
				return false;

			name = geo.getKernel().getApplication().toLowerCase(name);
			if (geo.isGeoFunction()) {
				if (invalidFunctionNames.contains(name))
						return false;
			}

			return true;
		}
}
