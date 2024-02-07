package com.sinensia.pollosalegres.backend.business.services.impl;

import java.util.List;

import org.dozer.DozerBeanMapper;

public abstract class AbstractServices {
	
	protected DozerBeanMapper mapper;
	
	protected AbstractServices(DozerBeanMapper mapper) {
		this.mapper = mapper;
	}
	
	protected <T, R> List<R> convertList(List<T> list, Class<R> classToConvert) {
		
		return list.stream()
				.map(x -> mapper.map(x, classToConvert))
				.toList();
	}
}
