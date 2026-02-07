package ru.goncharenko.market.core.types;

import lombok.Getter;

@Getter
public enum SortEnum {
	NO("id"),
	ALPHA( "title"),
	PRICE( "price");

	private final String fieldName;

	SortEnum(String fieldName){
		this.fieldName = fieldName;
	}
}
