package org.apache.freemarker.generator.tools.dataframe.converter;

import de.unknownreality.dataframe.DataFrame;
import org.apache.freemarker.generator.base.table.Table;

import java.util.List;

public class ListConverter {

    /**
     * Create a data frame from a list of rows. It is assumed
     * that the rows represent tabular data.
     *
     * @param rows rows to build the data frame
     * @return <code>DataFrame</code>
     */
    public static DataFrame toDataFrame(List<List<Object>> rows, boolean withFirstRowAsColumnNames) {
        final Table table = Table.fromRows(rows, withFirstRowAsColumnNames);
        return ConverterUtils.toDataFrame(table);
    }
}
