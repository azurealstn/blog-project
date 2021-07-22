package com.azurealstn.blogproject.service;

import com.azurealstn.blogproject.domain.board.Board;
import com.azurealstn.blogproject.domain.board.BoardRepository;
import com.azurealstn.blogproject.domain.user.User;
import com.azurealstn.blogproject.dto.board.BoardSaveRequestDto;
import com.azurealstn.blogproject.dto.board.BoardUpdateRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
public class BoardService {

    private final BoardRepository boardRepository;

    /**
     * 글작성 로직
     */
    @Transactional
    public Long save(BoardSaveRequestDto boardSaveRequestDto, User user) {
        boardSaveRequestDto.setUser(user);
        return boardRepository.save(boardSaveRequestDto.toEntity()).getId();
    }

    /**
     * 글목록 로직
     */
    @Transactional(readOnly = true)
    public List<Board> findAll() {
        return boardRepository.findAll();
    }

    /**
     * 글상세 로직
     */
    @Transactional(readOnly = true)
    public Board detail(Long id) {
        return boardRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("해당 id가 없습니다. id=" + id));
    }

    /**
     * 글삭제 로직
     */
    @Transactional
    public void deleteById(Long id) {
        boardRepository.deleteById(id);
    }

    /**
     * 글수정 로직
     */
    @Transactional
    public Long update(Long id, BoardUpdateRequestDto boardUpdateRequestDto) {
        Board board = boardRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("해당 id가 없습니다. id=" + id));
        board.update(boardUpdateRequestDto.getTitle(), boardUpdateRequestDto.getContent());
        return id;
    }

}
