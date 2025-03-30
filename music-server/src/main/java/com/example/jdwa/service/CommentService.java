package com.example.jdwa.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.jdwa.common.R;
import com.example.jdwa.model.domain.Comment;
import com.example.jdwa.model.request.CommentRequest;

public interface CommentService extends IService<Comment> {

    R addComment(CommentRequest addCommentRequest);

    R updateCommentMsg(CommentRequest upCommentRequest);

    R deleteComment(Integer id);

    R commentOfSongId(Integer songId);

    R commentOfSongListId(Integer songListId);

}
