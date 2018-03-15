package com.msl.service;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.msl.creteria.Creteria;
import com.msl.entity.BaseEntity;
import com.msl.util.AnnotationUtil;
import com.msl.util.DBConnUtil;

/**
 * Hibernate2 功能服务类
 * 
 * @author Zaki Chen
 *
 */
public class Hibernate2Service implements BaseService {

	/**
	 * 数据库连接对象
	 */
	private Connection connection = DBConnUtil.getServerConn();

	@Override
	public List<? extends BaseEntity> queryAll(Class<? extends BaseEntity> entity) {
		// 实体类绑定的表名
		String tableName = AnnotationUtil.getTableName(entity);
		if (tableName == null) {
			System.err.println("错误：实体类" + entity + "未绑定任何数据表,无法查询数据库！");
			throw new NullPointerException();
		}
		String sql = "select * from " + tableName;
		return qurey(sql, entity);
	}

	@Override
	public List<? extends BaseEntity> queryList(Class<? extends BaseEntity> entity, Creteria creteria) {

		return null;
	}

	/**
	 * 将ResultSet转换为List集合，元素为Map
	 * 
	 * @param rs
	 * @return
	 * @throws SQLException
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private List<Map<String, Object>> Result2Map(ResultSet resultSet) throws SQLException {
		List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();
		ResultSetMetaData md = resultSet.getMetaData(); // 获得结果集结构信息,元数据
		int columnCount = md.getColumnCount(); // 获得列数
		// 开始封装到集合
		while (resultSet.next()) {
			Map<String, Object> rowData = new HashMap<String, Object>();
			for (int i = 1; i <= columnCount; i++) {
				rowData.put(md.getColumnName(i), resultSet.getObject(i));
			}
			resultList.add(rowData);

		}
		return resultList;
	}

	/**
	 * 
	 * @param sql
	 *            查询语句基础方法
	 * @param entity
	 *            封装的实体类
	 * @return 根据查询结果封装后的实体集合
	 */
	private List<? extends BaseEntity> qurey(String sql, Class<? extends BaseEntity> entity) {
		List<BaseEntity> entityList = new ArrayList<>();
		// 实体类绑定的数据表字段信息
		Map<String, String> columnMap = AnnotationUtil.getFieldsColumnMap(entity);
		Set<Entry<String, String>> columnSet = columnMap.entrySet();
		try {
			Statement statement = connection.createStatement();
			ResultSet resultSet = statement.executeQuery(sql);

			// 数据库查询到的集合信息
			List<Map<String, Object>> result2MapList = Result2Map(resultSet);
			// 开始绑定信息到实体类
			for (Map<String, Object> resultMap : result2MapList) {
				// 便利数据库查询到的一条记录的每个字段信息，从实体类属性中去逐个匹配
				BaseEntity clazz = entity.newInstance();
				Method[] methods = clazz.getClass().getMethods();
				// 先遍历注解字段
				for (Entry<String, String> entry : columnSet) {
					// 实体类的属性变量
					String field = entry.getKey();
					// 实体类绑定的数据库字段名
					String columnName = entry.getValue();
					// 数据库查询到的字段数据
					Object columnValue = resultMap.get(columnName) == null ? null : resultMap.get(columnName);
					// 调用set方法设置字段值到属性中
					// 拼接Set方法
					String methodName;
					if (field.length() > 1) {
						methodName = "set" + field.substring(0, 1).toUpperCase() + field.substring(1);
					} else {
						methodName = "set" + field.toUpperCase();
					}
					for (Method method : methods) {
						if (method.getName().equals(methodName)) {
							method.invoke(clazz, columnValue);
						}
					}
				}
				entityList.add(clazz);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		return entityList;

	}

	@Override
	public List<? extends BaseEntity> queryList(Creteria creteria) {
		Class<? extends BaseEntity> entity = creteria.getEntity();
		// 实体类绑定的表名
		String tableName = AnnotationUtil.getTableName(entity);
		if (tableName == null) {
			System.err.println("错误：实体类" + entity + "未绑定任何数据表,无法查询数据库！");
			throw new NullPointerException();
		}
		String sql = "select * from " + tableName + creteria.getCondtion();
		return qurey(sql, entity);
	}
}
