package com.example.jdwa.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.jdwa.common.R;
import com.example.jdwa.controller.MinioUploadController;
import com.example.jdwa.mapper.SongListMapper;
import com.example.jdwa.model.domain.SongList;
import com.example.jdwa.model.request.SongListRequest;
import com.example.jdwa.service.SongListService;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;

@Service
public class SongListServiceImpl extends ServiceImpl<SongListMapper, SongList> implements SongListService {

    @Autowired
    private SongListMapper songListMapper;
    @Value("${minio.bucket-name}")
    String bucketName;

    @Autowired
    private MinioUploadController minioUploadController;

    @Override
    public R updateSongListMsg(SongListRequest updateSongListRequest) {
        SongList songList = new SongList();
        BeanUtils.copyProperties(updateSongListRequest, songList);
        if (songListMapper.updateById(songList) > 0) {
            return R.success("修改成功");
        } else {
            return R.error("修改失败");
        }
    }

    @Override
    public R deleteSongList(Integer id) {
        if (songListMapper.deleteById(id) > 0) {
            return R.success("删除成功");
        } else {
            return R.error("删除失败");
        }
    }

    @Override
    public R allSongList() {
        return R.success(null, songListMapper.selectList(null));
    }

    @Override
    public List<SongList> findAllSong() {
        List<SongList> songLists = songListMapper.selectList(null);
        return songLists;
    }


    @Override
    public R likeTitle(String title) {
        QueryWrapper<SongList> queryWrapper = new QueryWrapper<>();
        queryWrapper.like("title",title);
        return R.success(null, songListMapper.selectList(queryWrapper));
    }

    @Override
    public R likeStyle(String style) {
        QueryWrapper<SongList> queryWrapper = new QueryWrapper<>();
        queryWrapper.like("style",style);
        return R.success(null, songListMapper.selectList(queryWrapper));
    }

    @Override
    public R addSongList(SongListRequest addSongListRequest) {
        SongList songList = new SongList();
        BeanUtils.copyProperties(addSongListRequest, songList);
        String pic = "/img/songListPic/123.jpg";
        songList.setPic(pic);
        if (songListMapper.insert(songList) > 0) {
            return R.success("添加成功");
        } else {
            return R.error("添加失败");
        }
    }

    @Override
    public R updateSongListImg(MultipartFile avatarFile, int id) {
        String fileName = avatarFile.getOriginalFilename();
        String imgPath = "/img/songListPic/" + fileName;
        
        // 上传图片文件
        String result = minioUploadController.uploadSonglistImgFile(avatarFile);
        if (!"File uploaded successfully!".equals(result)) {
            return R.error("图片上传失败: " + result);
        }

        SongList songList = new SongList();
        songList.setId(id);
        songList.setPic(imgPath);
        
        if (songListMapper.updateById(songList) > 0) {
            return R.success("更新成功", imgPath);
        } else {
            return R.error("更新失败");
        }
    }
}
