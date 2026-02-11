package ru.goncharenko.market.core.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Paging {
	private int pageNumber;
	private int pageSize;
	private boolean hasPrev;
	private boolean hasNext;

	public int pageNumber() {
		return pageNumber;
	}

	public int pageSize() {
		return pageSize;
	}

	public boolean hasPrevious() {
		return hasPrev;
	}

	public boolean hasNext() {
		return hasNext;
	}

}
