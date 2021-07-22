package com.azurealstn.blogproject.controller.api;

import com.azurealstn.blogproject.domain.board.Board;
import com.azurealstn.blogproject.domain.board.BoardRepository;
import com.azurealstn.blogproject.dto.board.BoardSaveRequestDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class BoardApiControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private WebApplicationContext context;

    private MockMvc mvc;

    @Autowired
    private BoardRepository boardRepository;

    @BeforeEach
    public void setup() {
        mvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @AfterEach
    public void cleanup() {
        boardRepository.deleteAll();
    }

    @Test
    @WithMockUser(roles = "USER")
    public void board_글작성_테스트() throws Exception {
//        //given
//        String title = "test";
//        String content = "content";
//        int count = 0;
//
//        BoardSaveRequestDto dto = BoardSaveRequestDto.builder()
//                .title(title)
//                .content(content)
//                .count(count)
//                .build();
//
//        String url = "http://localhost:" + port + "/api/v1/board";
//
//        //when
//        mvc.perform(post(url)
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(new ObjectMapper().writeValueAsString(dto)))
//                .andExpect(status().isOk());
//
//        //then
//        List<Board> all = boardRepository.findAll();
//        assertThat(all.get(0).getTitle()).isEqualTo(title);
//        assertThat(all.get(0).getContent()).isEqualTo(content);
    }
}
