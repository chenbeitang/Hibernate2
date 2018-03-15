package com.msl.creteria;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import com.msl.entity.BaseEntity;
import com.msl.util.AnnotationUtil;

/**
 * 条件查询帮助类
 * 
 * @author Zaki Chen
 *
 */
public class Creteria {
	private Class<? extends BaseEntity> entity;
	private List<String> conditionList = new ArrayList<>();

	public Creteria(Class<? extends BaseEntity> entity) {
		this.entity = entity;
	}

	/**
	 * 匹配相等
	 */
	public static final String EQUALS = " = ";
	/**
	 * 大于
	 */
	public static final String LARGER_THAN = " > ";
	/**
	 * 小于
	 */
	public static final String SMALLER_THAN = " < ";
	/**
	 * 不等于
	 */
	public static final String NOT_EQUALS = " != ";
	/**
	 * 相似
	 */
	public static final String LIKE = " like ";

	/**
	 * 不相似
	 */
	public static final String NOT_LIKE = " not like ";

	/**
	 * 升序
	 */
	public static final String ORDER_BY = " order by ";

	/**
	 * 降序
	 */
	public static final String ORDER_BY_DESC = " order by desc ";

	/**
	 * having
	 */
	public static final String HAVING = " having ";

	/**
	 * in
	 */
	public static final String IN = " in ";

	/**
	 * 分组
	 */
	public static final String GROUP_BY = " group by ";

	public Creteria createCreteria(Class<? extends BaseEntity> entity) {
		this.entity = entity;
		return null;
	}

	/**
	 * 
	 * @param fieldName
	 *            实体类声明的属性名称
	 * @param value
	 *            比较的值
	 * @param cond
	 *            比较条件，不指定则默认为匹配相等
	 */
	public void addCondition(String fieldName, Object value, String cond) {
		Field[] fields = entity.getDeclaredFields();
		for (Field field : fields) {
			if (field.getName().equals(fieldName)) {
				// 绑定的数据库表字段的名称
				String columnName = AnnotationUtil.getColumnName(field);
				// 绑定了字段
				if (columnName != null) {
					// 判断数据类型，String、int、long等
					String type = "string";
					if (field.getType().toString().equals("int") || field.getType().toString().equals("long")
							|| field.getType().toString().equals("double")
							|| field.getType().toString().equals("float")) {
						type = "numeric";
					}
					StringBuilder sBuilder = new StringBuilder();
					if (cond == null || "".equals(cond)) {
						sBuilder.append(" ");
						sBuilder.append(fieldName);
						sBuilder.append(" ");
						// 没有条件，匹配相等
						sBuilder.append(Creteria.EQUALS);
						if ("string".equals(type)) {// 字段类型为字符串
							sBuilder.append(" '");
							sBuilder.append(value);
							sBuilder.append("' ");
						} else {// 字段类型为非字符串
							sBuilder.append(value);
							sBuilder.append(" ");
						}
					} else { // 有条件，条件匹配
						// 排序功能
						if (cond.toLowerCase().trim().startsWith(Creteria.ORDER_BY_DESC.trim())) {
							sBuilder.append(" order by " + fieldName + " desc ");
							conditionList.add(sBuilder.toString());
							return;
						} else if (cond.toLowerCase().trim().equals(Creteria.ORDER_BY.trim())) {
							sBuilder.append(" order by " + fieldName + " ");
							conditionList.add(sBuilder.toString());
							return;
						}

						sBuilder.append(" ");
						sBuilder.append(fieldName);
						sBuilder.append(" ");
						if (cond.toLowerCase().equals(Creteria.IN)) { // 如果是IN条件
							if (value instanceof String[]) {
								String[] strs = (String[]) value;
								sBuilder.append(cond);
								sBuilder.append(" (");
								for (String s : strs) {
									sBuilder.append(" '");
									sBuilder.append(s);
									sBuilder.append("' ");
									sBuilder.append(",");
								}
								sBuilder = new StringBuilder(sBuilder.toString().substring(0, sBuilder.length() - 1));
								sBuilder.append(") ");
							} else if (value instanceof int[]) {
								int[] strs = (int[]) value;
								sBuilder.append(cond);
								sBuilder.append(" (");
								for (int s : strs) {
									sBuilder.append(s);
									sBuilder.append(",");
								}
								sBuilder = new StringBuilder(sBuilder.toString().substring(0, sBuilder.length() - 1));
								sBuilder.append(") ");
							} else if (value instanceof float[] || value instanceof double[]) {
								float[] strs = (float[]) value;
								sBuilder.append(cond);
								sBuilder.append(" (");
								for (float s : strs) {
									sBuilder.append(s);
									sBuilder.append(",");
								}
								sBuilder = new StringBuilder(sBuilder.toString().substring(0, sBuilder.length() - 1));
								sBuilder.append(") ");
							} else if (value instanceof long[]) {
								long[] strs = (long[]) value;
								sBuilder.append(cond);
								sBuilder.append(" (");
								for (long s : strs) {
									sBuilder.append(s);
									sBuilder.append(",");
								}
								sBuilder = new StringBuilder(sBuilder.toString().substring(0, sBuilder.length() - 1));
								sBuilder.append(") ");
							} else {
								System.err.println(
										"非法IN条件参数，不支持的参数类型，忽略该次条件匹配" + fieldName + " " + cond + " " + value.toString());
								return;
							}
						} else {
							if ("string".equals(type)) {// 字段类型为字符串
								sBuilder.append(cond);
								sBuilder.append(" '");
								sBuilder.append(value);
								sBuilder.append("' ");
							} else {// 字段类型为非字符串
								sBuilder.append(cond);
								sBuilder.append(" ");
								sBuilder.append(value);
								sBuilder.append(" ");
							}
						}
					}
					conditionList.add(sBuilder.toString());
				}
				break;
			}
		}
	}

	public String getCondtion() {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(" where 1=1 ");
		String orderBy = "";
		for (String string : conditionList) {
			if (string.trim().toLowerCase().startsWith(Creteria.ORDER_BY.trim())) {
				orderBy = string;
				continue;
			}
			stringBuilder.append(" and ");
			stringBuilder.append(string);
		}
		stringBuilder.append(orderBy);
		return stringBuilder.toString();
	}

	public Class<? extends BaseEntity> getEntity() {
		return entity;
	}

	public void setEntity(Class<? extends BaseEntity> entity) {
		conditionList.clear();
		this.entity = entity;
	}

	/**
	 * 自定义查询条件语句，不需要输入where关键字，调用该方法将会清空之前所绑定的其他条件信息
	 * 
	 * @param condSQL
	 */
	public void setCondition(String condSQL) {
		conditionList.clear();
		conditionList.add(condSQL);
	}
}
