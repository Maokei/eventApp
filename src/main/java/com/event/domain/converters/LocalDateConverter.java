package com.event.domain.converters;

import java.sql.Date;
import java.time.LocalDate;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter(autoApply = true)
public class LocalDateConverter implements AttributeConverter<LocalDate, Date> {

	@Override
	public java.sql.Date convertToDatabaseColumn(LocalDate localDate) {
		return java.sql.Date.valueOf(localDate);
	}

	@Override
	public LocalDate convertToEntityAttribute(java.sql.Date date) {
		return date.toLocalDate();
	}
}
