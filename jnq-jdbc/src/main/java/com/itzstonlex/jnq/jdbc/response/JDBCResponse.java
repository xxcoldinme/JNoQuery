package com.itzstonlex.jnq.jdbc.response;

import com.itzstonlex.jnq.content.exception.JnqContentException;
import com.itzstonlex.jnq.content.Response;
import com.itzstonlex.jnq.content.ResponseLine;
import lombok.NonNull;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.*;
import java.util.function.Supplier;

public class JDBCResponse extends LinkedList<ResponseLine> implements Response {

    public JDBCResponse(@NonNull Response response) {
        super(response);
    }

    public JDBCResponse(@NonNull ResultSet resultSet)
    throws JnqContentException {

        try {
            ResultSetMetaData metadata = resultSet.getMetaData();

            int columns = metadata.getColumnCount();
            int index = 0;

            while (resultSet.next()) {

                int finalIndex = index; // java8 fuck u
                Supplier<ResponseLine> nextSupplier = () -> super.get(finalIndex);

                JDBCResponseLine responseLine = new JDBCResponseLine(index == 0, index == (columns - 1), nextSupplier);
                this._applyMetadata(responseLine, resultSet, metadata, columns);

                super.add(responseLine);
                index++;
            }
        } catch (SQLException exception) {
            throw new JnqContentException("response", exception);
        }
    }

    private void _applyMetadata(JDBCResponseLine responseLine, ResultSet resultSet, ResultSetMetaData metadata, int columns)
    throws SQLException {

        // create caches data.
        Set<Integer> nullableIndexes = new HashSet<>();

        Map<String, Integer> indexByLabelsMap = new HashMap<>();
        Map<Integer, String> labelByIndexesMap = new HashMap<>();

        // init columns data.
        for (int columnIndex = 1; columnIndex <= columns; columnIndex++) {

            String name = metadata.getColumnName(columnIndex);

            indexByLabelsMap.put(name.toLowerCase(), columnIndex);
            labelByIndexesMap.put(columnIndex, name);

            if (metadata.isNullable(columnIndex) == ResultSetMetaData.columnNullable) {
                nullableIndexes.add(columnIndex);
            }

            responseLine.set(columnIndex, resultSet.getObject(columnIndex));
        }

        // set metadata values for response-line.
        responseLine.setIndexByLabelsMap(indexByLabelsMap);
        responseLine.setLabelByIndexesMap(labelByIndexesMap);

        responseLine.setNullableIndexes(nullableIndexes);
    }

}
