package com.zhu.controller;

import com.zhu.model.entity.Comment;
import com.zhu.result.RespBean;
import com.zhu.service.ICommentService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/admin/comment")
public class CommentController {

    @Resource
    private ICommentService commentService;

    @GetMapping("/all")
    public List<Comment> getComment(){
        //获取全部评论
        return null;
    }

}
