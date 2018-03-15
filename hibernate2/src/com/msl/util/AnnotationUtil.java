package com.msl.util;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import com.msl.annotation.Column;
import com.msl.annotation.Table;

public class AnnotationUtil {
	/**
	 * @author Zaki Chen 获取实体类中注解绑定的属性与数据库中对应的字段名
	 * @param clazz
	 * @return 返回实体类属性与其绑定的数据库字段的Map对象
	 */
	public static Map<String, String> getFieldsColumnMap(Class<?> clazz) {
		Field[] fields = clazz.getDeclaredFields();
		Map<String, String> map = new HashMap<>();
		for (Field field : fields) {
			if (field.isAnnotationPresent(Column.class)) {
				Column column = (Column) field.getAnnotation(Column.class);
				map.put(field.getName(), column.name());
			}
		}
		return map;
	}

	/**
	 * @author Zaki Chen 返回实体类对象绑定的表名
	 * @param Class.class
	 * @return 发现绑定返回绑定值，未绑定则返回null
	 */
	public static String getTableName(Class<?> clazz) {
		if (clazz.isAnnotationPresent(Table.class)) {
			Table table = (Table) clazz.getAnnotation(Table.class);
			return table.name();
		}
		return null;
	}

	/**
	 * 根据绑定的域属性返回对应的数据库字段名
	 * 
	 * @param field
	 * @return
	 */
	public static String getColumnName(Field field) {
		if (field.isAnnotationPresent(Column.class)) {
			Column column = (Column) field.getAnnotation(Column.class);
			return column.name();
		}
		return null;
	}
}
