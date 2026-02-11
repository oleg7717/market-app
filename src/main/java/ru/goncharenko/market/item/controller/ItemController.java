package ru.goncharenko.market.item.controller;

import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
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
	public ModelAndView show(
			@RequestParam(required = false) String search,
			@RequestParam(defaultValue = "NO") SortEnum sort,
			@RequestParam(defaultValue = "1")
				@Min(value = 1, message = "Page number should be more then 1.") int pageNumber,
			@RequestParam(defaultValue = "5")
				@Min(value = 1, message = "Page size should be more then 1.") int pageSize) {
		ListItemsDTO listItemsDTO = service.getItems(search, sort, pageNumber, pageSize);

		ModelAndView modelAndView = new ModelAndView("items");
		modelAndView.addObject("items", listItemsDTO.getItems());
		modelAndView.addObject("search", search);
		modelAndView.addObject("sort", sort);
		modelAndView.addObject("paging", listItemsDTO.getPaging());

		return modelAndView;
	}

	@PostMapping(path = "/items")
	public ModelAndView changeItemCountInCartFromItemsPage(
			@RequestParam long id,
			@RequestParam(required = false) String search,
			@RequestParam(defaultValue = "NO") SortEnum sort,
			@RequestParam(defaultValue = "1")
				@Min(value = 1, message = "Page number should be more then 1.") int pageNumber,
			@RequestParam(defaultValue = "5")
				@Min(value = 1, message = "Page size should be more then 1.") int pageSize,
			@RequestParam ActionEnum action) {
//		service.changeItemCountInCart(id, action);
		return new ModelAndView(String.format("redirect:/items?search=%s&sort=%s&pageNumber=%d&pageSize=%d", search, sort, pageNumber, pageSize));
	}

	@GetMapping(path = "/items/{id}")
	public ModelAndView show(@PathVariable long id) {
		ItemDTO itemDTO = service.findById(id);

		ModelAndView modelAndView = new ModelAndView("item");
		modelAndView.addObject("item", itemDTO.getItem());

		return modelAndView;
	}

	@PostMapping(path = "/items/{id}")
	public ModelAndView changeItemCountInCartFromItem(@PathVariable long id, @RequestParam ActionEnum action) {
		ItemDTO itemDTO =  service.changeCountAndReturnItemInCart(id, action);

		ModelAndView modelAndView = new ModelAndView("item");
		modelAndView.addObject("item", itemDTO.getItem());

		return modelAndView;
	}
}
