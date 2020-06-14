package org.apache.freemarker.generator.tools.dataframe.converter;

import de.unknownreality.dataframe.DataFrame;
import de.unknownreality.dataframe.DataFrameBuilder;
import org.apache.freemarker.generator.base.table.Table;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class MapConverter {

    /**
     * Create a data frame from a list of maps. It is assumed
     * that the map represent tabular data.
     *
     * @param map map to build the data frame
     * @return <code>DataFrame</code>
     */
    public static DataFrame toDataFrame(Map<String, Object> map) {
        return toDataFrame(Collections.singletonList(map));
    }

    /**
     * Create a data frame from a list of maps. It is assumed
     * that the map represent tabular data.
     *
     * @param maps list of map to build the data frame
     * @return <code>DataFrame</code>
     */
    public static DataFrame toDataFrame(List<Map<String, Object>> maps) {
        final Table table = Table.fromMaps(maps);
        return ConverterUtils.toDataFrame(table);
    }
}
