package org.apache.freemarker.generator.tools.dataframe.converter;

import de.unknownreality.dataframe.DataFrame;
import org.apache.freemarker.generator.base.table.Table;

import java.util.List;

public class ListConverter {

    /**
     * Create a data frame from a list of lists. It is assumed
     * that the lists represent tabular data.
     *
     * @param lists lists to build the data frame
     * @return <code>DataFrame</code>
     */
    public static DataFrame toDataFrame(List<List<Object>> lists, boolean withFirstRowAsColumnNames) {
        final Table table = Table.fromLists(lists, withFirstRowAsColumnNames);
        return ConverterUtils.toDataFrame(table);
    }
}
