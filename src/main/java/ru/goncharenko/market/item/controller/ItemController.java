package ru.goncharenko.market.item.controller;

import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import ru.goncharenko.market.core.types.ActionEnum;
import ru.goncharenko.market.core.types.SortEnum;
import ru.goncharenko.market.item.dto.ItemInCartDTO;
import ru.goncharenko.market.item.dto.ListItemsDTO;
import ru.goncharenko.market.item.service.FileService;
import ru.goncharenko.market.item.service.ItemService;

@Controller
@Validated
@RequiredArgsConstructor
public class ItemController {
	private final ItemService itemService;
	private final FileService fileService;

	@GetMapping(path = {"/items", ""})
	public ModelAndView show(
			@RequestParam(required = false) String search,
			@RequestParam(defaultValue = "NO") SortEnum sort,
			@RequestParam(defaultValue = "1")
				@Min(value = 1, message = "Page number should be more then 1.") int pageNumber,
			@RequestParam(defaultValue = "5")
				@Min(value = 1, message = "Page size should be more then 1.") int pageSize) {
		ListItemsDTO listItemsDTO = itemService.getItems(search, sort, pageNumber, pageSize);

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
		itemService.changeItemCountInCart(id, action);
		return new ModelAndView(String.format("redirect:/items?search=%s&sort=%s&pageNumber=%d&pageSize=%d",
				search,
				sort,
				pageNumber,
				pageSize)
		);
	}

	@GetMapping(path = "/items/{id}")
	public ModelAndView show(@PathVariable long id) {
		ItemInCartDTO itemDTO = itemService.findById(id);

		ModelAndView modelAndView = new ModelAndView("item");
		modelAndView.addObject("item", itemDTO);

		return modelAndView;
	}

	@PostMapping(path = "/items/{id}")
	public ModelAndView changeItemCountInCartFromItem(@PathVariable long id, @RequestParam ActionEnum action) {
		itemService.changeItemCountInCart(id, action);
		ItemInCartDTO itemDTO = itemService.findById(id);

		ModelAndView modelAndView = new ModelAndView("item");
		modelAndView.addObject("item", itemDTO);

		return modelAndView;
	}

	@GetMapping(path = "/images/{filename:.+}", produces = MediaType.IMAGE_JPEG_VALUE)
	public @ResponseBody byte[] getImage(@PathVariable String filename) {
		return fileService.download(filename);
	}
}
