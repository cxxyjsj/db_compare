package com.domain.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;

import com.domain.ColumnInfo;

/**
 * @author cxxyjsj
 * @date 2016年5月1日 下午6:26:03
 */
public class DefaultColumnInfoResultSetExtractor implements ResultSetExtractor<List<ColumnInfo>> {

	@Override
	public List<ColumnInfo> extractData(ResultSet rs) throws SQLException, DataAccessException {
		List<ColumnInfo> list = new ArrayList<>();
		while(rs.next()){
			ColumnInfo ci = new ColumnInfo();
			ci.setTableName(rs.getString("TABLE_NAME"));
			ci.setName(rs.getString("COLUMN_NAME"));
			ci.setType(rs.getString("COLUMN_TYPE"));
			ci.setSize(rs.getInt("COLUMN_SIZE"));
			list.add(ci);
		}
		return list;
	}
}
