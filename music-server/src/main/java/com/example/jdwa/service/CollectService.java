package com.example.jdwa.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.jdwa.common.R;
import com.example.jdwa.model.domain.Collect;
import com.example.jdwa.model.request.CollectRequest;

public interface CollectService extends IService<Collect> {

    R addCollection(CollectRequest addCollectRequest);

    R existSongId(CollectRequest isCollectRequest);

    R deleteCollect(Integer userId,Integer songId);

    R collectionOfUser(Integer userId);
}
