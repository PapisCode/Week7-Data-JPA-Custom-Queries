package com.example.Thymeleaf.Demo.controllers;

import com.example.Thymeleaf.Demo.Model.Fighter;
import com.example.Thymeleaf.Demo.repository.FighterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class FighterController {

    @Autowired
    private FighterRepository fighterRepository;

    @GetMapping("/fighters")
    public String getFighters(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sort,
            @RequestParam(defaultValue = "ASC") String direction,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "all") String filterType,
            Model model) {

        if (!sort.equals("id") &&
            !sort.equals("name") &&
            !sort.equals("health") &&
            !sort.equals("damage") &&
            !sort.equals("resistance")) {
            sort = "id";
        }

        Sort.Direction sortDirection = direction.equalsIgnoreCase("DESC")
                ? Sort.Direction.DESC
                : Sort.Direction.ASC;

        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sort));

        Page<Fighter> fighterPage;

        switch (filterType.toLowerCase()) {
            case "name":
                if (search != null && !search.trim().isEmpty()) {
                    fighterPage = fighterRepository.findByNameContainingIgnoreCase(search.trim(), pageable);
                } else {
                    fighterPage = fighterRepository.findAll(pageable);
                }
                break;

            case "health":
                try {
                    Integer healthValue = Integer.parseInt(search);
                    fighterPage = fighterRepository.findByHealthGreaterThan(healthValue, pageable);
                } catch (Exception e) {
                    fighterPage = fighterRepository.findAll(pageable);
                }
                break;

            case "strongest":
                fighterPage = fighterRepository.findStrongestFighters(pageable);
                break;

            case "balanced":
                fighterPage = fighterRepository.findBalancedFighters(1001, 99.99, pageable);
                break;

            default:
                fighterPage = fighterRepository.findAll(pageable);
                break;
        }

        model.addAttribute("fighters", fighterPage.getContent());
        model.addAttribute("totalPages", fighterPage.getTotalPages());
        model.addAttribute("totalElements", fighterPage.getTotalElements());
        model.addAttribute("currentPage", page);
        model.addAttribute("pageSize", size);
        model.addAttribute("hasPrevious", fighterPage.hasPrevious());
        model.addAttribute("hasNext", fighterPage.hasNext());

        model.addAttribute("search", search);
        model.addAttribute("sort", sort);
        model.addAttribute("direction", direction);
        model.addAttribute("filterType", filterType);

        return "fighters";
    }
}