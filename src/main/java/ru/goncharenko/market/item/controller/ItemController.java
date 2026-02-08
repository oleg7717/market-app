package ru.goncharenko.market.item.controller;

import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.goncharenko.market.core.types.ActionEnum;
import ru.goncharenko.market.core.types.SortEnum;
import ru.goncharenko.market.item.dto.ItemDTO;
import ru.goncharenko.market.item.dto.ListItemsDTO;
import ru.goncharenko.market.item.service.ItemService;

@RestController
@Validated
@RequiredArgsConstructor
public class ItemController {
	private final ItemService service;

	@GetMapping(path = {"/items", ""})
	public ListItemsDTO index(
			@RequestParam(required = false) String search,
			@RequestParam(defaultValue = "NO") SortEnum sort,
			@RequestParam(defaultValue = "1")
				@Min(value = 1, message = "Page number should be more then 1.") int pageNumber,
			@RequestParam(defaultValue = "5")
				@Min(value = 1, message = "Page size should be more then 1.") int pageSize) {
		return service.getItems(search, sort, pageNumber, pageSize);
	}

	@PostMapping(path = "/items")
	public String changeItemCountInCartFromItemsPage(
			@RequestParam long id,
			@RequestParam(required = false) String search,
			@RequestParam(defaultValue = "NO") SortEnum sort,
			@RequestParam(defaultValue = "1")
				@Min(value = 1, message = "Page number should be more then 1.") int pageNumber,
			@RequestParam(defaultValue = "5")
				@Min(value = 1, message = "Page size should be more then 1.") int pageSize,
			@RequestParam ActionEnum action) {
		service.changeItemCountInCart(id, action);
		return String.format("redirect:/items?search=%s&sort=%s&pageNumber=%d&pageSize=%d", search, sort, pageNumber, pageSize);
	}

	@GetMapping(path = "/items/{id}")
	public ItemDTO show(@PathVariable long id) {
		return service.findById(id);
	}

	@PostMapping(path = "/items/{id}")
	public ItemDTO changeItemCountInCartFromItem(@PathVariable long id, @RequestParam ActionEnum action) {
		return service.changeCountAndReturnItemInCart(id, action);
	}
}
