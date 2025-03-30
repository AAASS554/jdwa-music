package com.example.jdwa.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.jdwa.common.R;
import com.example.jdwa.model.domain.ListSong;
import com.example.jdwa.model.request.ListSongRequest;

import java.util.List;

public interface ListSongService extends IService<ListSong> {

    R addListSong(ListSongRequest addListSongRequest);

    R updateListSongMsg(ListSongRequest updateListSongRequest);

    R deleteListSong(Integer songId);

    //看看这啥
    List<ListSong> allListSong();

    R listSongOfSongId(Integer songListId);
}
