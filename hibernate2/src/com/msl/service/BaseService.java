package com.msl.service;

import java.util.List;
import java.util.Map;

import com.msl.creteria.Creteria;
import com.msl.entity.BaseEntity;

public interface BaseService {
	/**
	 * 根据该对象绑定的表对象从数据库中查询集合,查询所有对象
	 * @param entity 继承于BaseEntity的实体类
	 * @return 返回该对象绑定的数据库中的所有记录，实体类中声明绑定的列会被自动赋值到绑定的实体类属性中
	 */
	List<? extends BaseEntity> queryAll(Class<? extends BaseEntity> entity);
	/**
	 * 带条件查询指定的实体类
	 * @param entity 继承于BaseEntity的实体类
	 * @param condition 条件
	 * @return 按照条件，返回该对象绑定的数据库中的所有记录，实体类中声明绑定的列会被自动赋值到绑定的实体类属性中
	 */
	List<? extends BaseEntity> queryList(Class<? extends BaseEntity> entity,Creteria creteria);
	
	
	
	List<? extends BaseEntity> queryList(Creteria creteria);
}
