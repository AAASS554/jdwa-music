package com.example.jdwa.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.example.jdwa.common.R;
import com.example.jdwa.model.request.RankListRequest;
import com.example.jdwa.service.RankListService;

@RestController
public class RankListController {

    @Autowired
    private RankListService rankListService;


    // 提交评分
    @PostMapping("/rankList/add")
    public R addRank(@RequestBody RankListRequest rankListAddRequest) {
        return rankListService.addRank(rankListAddRequest);
    }

    // 获取指定歌单的评分
    @GetMapping("/rankList")
    public R rankOfSongListId(@RequestParam Long songListId) {
        return rankListService.rankOfSongListId(songListId);
    }

    // 获取指定用户的歌单评分
    @GetMapping("/rankList/user")
    public R getUserRank(@RequestParam(required = false) Long consumerId, @RequestParam Long songListId) {
        R userRank = rankListService.getUserRank(consumerId, songListId);
        return R.success("成功",userRank);
    }


}
