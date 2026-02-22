package ru.goncharenko.market.item.controller;

import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.reactive.result.view.Rendering;
import reactor.core.publisher.Mono;
import ru.goncharenko.market.core.types.SortEnum;
import ru.goncharenko.market.item.dto.ItemRequest;
import ru.goncharenko.market.item.service.CartService;
import ru.goncharenko.market.item.service.FileService;
import ru.goncharenko.market.item.service.ItemService;

@Controller
@Validated
@RequiredArgsConstructor
public class ItemController {
	private final ItemService itemService;
	private final CartService cartService;
	private final FileService fileService;

	@GetMapping(path = {"/items", ""})
	public Mono<Rendering> show(
			@RequestParam(required = false) String search,
			@RequestParam(defaultValue = "NO") SortEnum sort,
			@RequestParam(defaultValue = "1")
			@Min(value = 1, message = "Page number should be more then 1.") int pageNumber,
			@RequestParam(defaultValue = "5")
			@Min(value = 1, message = "Page size should be more then 1.") int pageSize) {
		return itemService.getItems(search, sort, pageNumber, pageSize)
				.flatMap(item ->
						Mono.just(Rendering.view("items")
										.modelAttribute("items", item.getItems()).
										modelAttribute("search", search).
										modelAttribute("sort", sort).
										modelAttribute("paging", item.getPaging())
								.build())
				);
	}

	@PostMapping(path = "/items")
	public Mono<Rendering> changeItemCountInCartFromItemsPage(
			@ModelAttribute ItemRequest request,
			@RequestParam(required = false) String search,
			@RequestParam(defaultValue = "NO") SortEnum sort,
			@RequestParam(defaultValue = "1")
				@Min(value = 1, message = "Page number should be more then 1.") int pageNumber,
			@RequestParam(defaultValue = "5")
				@Min(value = 1, message = "Page size should be more then 1.") int pageSize) {
		cartService.changeItemsCountFromCart(request.getId(), request.getAction());

		return Mono.just(Rendering.redirectTo(String.format("redirect:/items?search=%s&sort=%s&pageNumber=%d&pageSize=%d",
						search,
						sort,
						pageNumber,
						pageSize)).build());
	}

	@GetMapping(path = "/items/{id}")
	public Mono<Rendering> show(@PathVariable Long id) {
		return itemService.findById(id).flatMap(item ->
				Mono.just(Rendering.view("item")
						.modelAttribute("item", item)
						.build())
		);
	}

	@PostMapping(path = "/items/{id}")
	public Mono<Rendering> changeItemCountInCartFromItem(@ModelAttribute ItemRequest request) {
		cartService.changeItemsCountFromCart(request.getId(), request.getAction());
		return itemService.findById(request.getId()).flatMap(item ->
				Mono.just(Rendering.view("item")
						.modelAttribute("item", item)
						.build())
		);
	}

	@GetMapping(path = "/images/{filename:.+}", produces = MediaType.IMAGE_JPEG_VALUE)
	public @ResponseBody byte[] getImage(@PathVariable String filename) {
		return fileService.download(filename);
	}
}
