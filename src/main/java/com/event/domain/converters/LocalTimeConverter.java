package com.event.domain.converters;

import java.time.LocalTime;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter(autoApply = true)
public class LocalTimeConverter implements AttributeConverter<LocalTime, java.sql.Time> {

	@Override
	public java.sql.Time convertToDatabaseColumn(LocalTime localTime) {
		return java.sql.Time.valueOf(localTime);
	}

	@Override
	public LocalTime convertToEntityAttribute(java.sql.Time time) {
		return time.toLocalTime();
	}
}
