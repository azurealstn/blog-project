package com.azurealstn.blogproject.service;

import com.azurealstn.blogproject.domain.board.Board;
import com.azurealstn.blogproject.domain.board.BoardRepository;
import com.azurealstn.blogproject.domain.reply.Reply;
import com.azurealstn.blogproject.domain.reply.ReplyRepository;
import com.azurealstn.blogproject.domain.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class ReplyService {

    private final ReplyRepository replyRepository;
    private final BoardRepository boardRepository;

    @Transactional
    public void replySave(Long boardId, Reply reply, User user) {
        Board board = boardRepository.findById(boardId).orElseThrow(() -> new IllegalArgumentException("해당 boardId가 없습니다. id=" + boardId));

        reply.save(board, user);

        replyRepository.save(reply);
    }

    @Transactional
    public void replyDelete(Long replyId) {
        replyRepository.deleteById(replyId);
    }
}
