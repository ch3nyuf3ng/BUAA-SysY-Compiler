package symbol;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SymbolUtils {
    public static List<Integer> generateDimensionOffsets(List<Integer> dimensionSizes) {
        if (dimensionSizes.isEmpty()) {
            return Collections.emptyList();
        } else {
            final var dimensionOffsets = new ArrayList<>(dimensionSizes);
            for (var i = dimensionOffsets.size() - 2; i >= 0; i -= 1) {
                dimensionOffsets.set(i, dimensionOffsets.get(i) * dimensionOffsets.get(i + 1));
            }
            dimensionOffsets.remove(0);
            dimensionOffsets.add(1);
            return Collections.unmodifiableList(dimensionOffsets);
        }
    }
}
