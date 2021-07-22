package com.azurealstn.blogproject.controller;

import com.azurealstn.blogproject.service.BoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RequiredArgsConstructor
@Controller
public class BoardController {

    private final BoardService boardService;

    @GetMapping("/board/save")
    public String save() {
        return "layout/board/board-save";
    }

    @GetMapping("/board/{id}")
    public String detail(@PathVariable Long id, Model model) {
        model.addAttribute("board", boardService.detail(id));
        return "layout/board/board-detail";
    }

    @GetMapping("/board/{id}/update")
    public String update(@PathVariable Long id, Model model) {
        model.addAttribute("board", boardService.detail(id));
        return "layout/board/board-update";
    }
}
