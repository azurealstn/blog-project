package com.azurealstn.blogproject.controller.api;

import com.azurealstn.blogproject.config.auth.PrincipalDetail;
import com.azurealstn.blogproject.domain.reply.Reply;
import com.azurealstn.blogproject.service.ReplyService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
public class ReplyApiController {

    private final ReplyService replyService;

    /**
     * 댓글 작성 API
     */
    @PostMapping("/api/v1/board/{boardId}/reply")
    public void save(@PathVariable Long boardId,
                     @RequestBody Reply reply,
                     @AuthenticationPrincipal PrincipalDetail principalDetail) {
        replyService.replySave(boardId, reply, principalDetail.getUser());
    }

    /**
     * 댓글 삭제 API
     */
    @DeleteMapping("/api/v1/board/{boardId}/reply/{replyId}")
    public void delete(@PathVariable Long replyId) {
        replyService.replyDelete(replyId);
    }
}
