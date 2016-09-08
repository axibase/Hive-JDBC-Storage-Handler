/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.hadoop.hive.jdbc.storagehandler;

import com.sun.rowset.CachedRowSetImpl;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.mapreduce.lib.db.DBConfiguration;
import org.apache.hadoop.mapreduce.lib.db.DBInputFormat;
import org.apache.hadoop.mapreduce.lib.db.DBRecordReader;
import org.apache.hadoop.mapreduce.lib.db.DBWritable;

import javax.sql.rowset.RowSetMetaDataImpl;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import static org.apache.hadoop.hive.jdbc.storagehandler.Constants.DEFAULT_INPUT_FETCH_SIZE;
import static org.apache.hadoop.hive.jdbc.storagehandler.Constants.INPUT_FETCH_SIZE;

public class AtsdDBRecordReader<T extends DBWritable> extends DBRecordReader<T> {
    final Configuration conf;
    private static final Log LOG = LogFactory.getLog(AtsdDBRecordReader.class);

    public AtsdDBRecordReader(DBInputFormat.DBInputSplit split, Class<T> inputClass, Configuration conf, Connection conn, DBConfiguration dbConfig, String cond, String[] fields, String table)
            throws SQLException {
        super(split, inputClass, conf, conn, dbConfig, cond, fields, table);
        this.conf = conf;
    }

    @Override
    protected ResultSet executeQuery(String query) throws SQLException {
        LOG.debug("[executeQuery] query is " + query);
        query = query.replaceAll("\\$", ".");
        this.statement = getConnection().prepareStatement(query,
                ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
        statement.setFetchSize(conf.getInt(INPUT_FETCH_SIZE, DEFAULT_INPUT_FETCH_SIZE));
        return replaceDotsInColumnNames(statement.executeQuery());
    }

    private ResultSet replaceDotsInColumnNames(ResultSet resultSet) throws SQLException {
        ResultSetMetaData metaData = resultSet.getMetaData();
        int columnCount = metaData.getColumnCount();

        if (columnCount > 0) {
            CachedRowSetImpl crs = new CachedRowSetImpl();
            crs.populate(resultSet);

            RowSetMetaDataImpl rwsm = new RowSetMetaDataImpl();

            rwsm.setColumnCount(columnCount);

            for (int i = 1; i <= columnCount; i++) {
                String columnName = metaData.getColumnName(i);
                if (columnName.contains(".")) {
                    columnName = columnName.replaceAll("\\.", "\\$");
                }
                rwsm.setColumnName(i, columnName);
                rwsm.setColumnLabel(i, metaData.getColumnLabel(i));
                rwsm.setCatalogName(i, metaData.getCatalogName(i));
                rwsm.setColumnType(i, metaData.getColumnType(i));
                rwsm.setColumnTypeName(i, metaData.getColumnTypeName(i));
                rwsm.setSchemaName(i, metaData.getSchemaName(i));
                rwsm.setTableName(i, metaData.getTableName(i));
            }
            crs.setMetaData(rwsm);
            return crs;
        }
        return resultSet;
    }

    @Override
    public void close() throws IOException {
        try {
            statement.cancel();
        } catch (SQLException e) {
            // Ignore any errors in cancelling, this is not fatal
            LOG.error("Could not cancel query: " + this.getSelectQuery());
        }
        super.close();
    }
}
