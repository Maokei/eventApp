package com.event.domain.converters;

import java.sql.Timestamp;
import java.time.LocalDateTime;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter(autoApply = true)
public class LocalDateTimeConverter implements AttributeConverter<LocalDateTime, Timestamp> {

	@Override
	public java.sql.Timestamp convertToDatabaseColumn(LocalDateTime localDateTime) {
		 return Timestamp.valueOf(localDateTime);
	}

	@Override
	public LocalDateTime convertToEntityAttribute(java.sql.Timestamp timestamp) {
		return timestamp.toLocalDateTime();
	}
}
