package com.example.jdwa.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.jdwa.common.R;
import com.example.jdwa.model.domain.RankList;
import com.example.jdwa.model.request.RankListRequest;

public interface RankListService extends IService<RankList> {

    R addRank(RankListRequest rankListAddRequest);

    R rankOfSongListId(Long songListId);

    R getUserRank(Long consumerId, Long songListId);

}
