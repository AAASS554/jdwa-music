package com.example.jdwa.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.jdwa.common.R;
import com.example.jdwa.controller.MinioUploadController;
import com.example.jdwa.mapper.SongMapper;
import com.example.jdwa.model.domain.Song;
import com.example.jdwa.model.request.SongRequest;
import com.example.jdwa.service.SongService;

import io.minio.MinioClient;
import io.minio.RemoveObjectArgs;
import io.minio.errors.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class SongServiceImpl extends ServiceImpl<SongMapper, Song> implements SongService {

    @Autowired
    private SongMapper songMapper;

    @Autowired
    private MinioUploadController minioUploadController;

    @Value("${minio.bucket-name}")
    private String bucketName;

    @Autowired
    MinioClient minioClient;

    @Override
    public R allSong() {
        return R.success(null, songMapper.selectList(null));
    }

    @Override
    public R addSong(SongRequest addSongRequest, MultipartFile lrcfile, MultipartFile mpfile) {
        Song song = new Song();
        BeanUtils.copyProperties(addSongRequest, song);
        String pic = "/img/songPic/tubiao.jpg";
        String fileName = mpfile.getOriginalFilename();
        String filePath = System.currentTimeMillis() + fileName;
        
        // 上传音乐文件
        String result = minioUploadController.uploadSongFile(mpfile);
        if (!"File uploaded successfully!".equals(result)) {
            return R.error("音乐上传失败: " + result);
        }
        
        song.setCreateTime(new Date());
        song.setUpdateTime(new Date());
        song.setPic(pic);
        song.setUrl(filePath);

        if (lrcfile!=null&&(song.getLyric().equals("[00:00:00]暂无歌词"))){
            byte[] fileContent = new byte[0];
            try {
                fileContent = lrcfile.getBytes();
                String content = new String(fileContent, "GB2312");
                song.setLyric(content);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        if (songMapper.insert(song) > 0) {
            return R.success("上传成功", filePath);
        } else {
            return R.error("上传失败");
        }
    }

    @Override
    public R updateSongMsg(SongRequest updateSongRequest) {
        Song song = new Song();
        BeanUtils.copyProperties(updateSongRequest, song);
        if (songMapper.updateById(song) > 0) {
            return R.success("修改成功");
        } else {
            return R.error("修改失败");
        }
    }

    @Override
    public R updateSongUrl(MultipartFile urlFile, int id) {
        String fileName = urlFile.getOriginalFilename();
        String filePath = System.currentTimeMillis() + fileName;
        
        // 上传音乐文件
        String result = minioUploadController.uploadSongFile(urlFile);
        if (!"File uploaded successfully!".equals(result)) {
            return R.error("音乐上传失败: " + result);
        }

        Song song = songMapper.selectById(id);
        String path = song.getUrl();
        String[] parts = path.split("/");
        String oldFileName = parts[parts.length - 1];

        RemoveObjectArgs removeObjectArgs=RemoveObjectArgs.builder()
                .bucket(bucketName)
                .object(oldFileName)
                .build();
        if (songMapper.deleteById(id) > 0) {
            try {
                minioClient.removeObject(removeObjectArgs);
            } catch (ErrorResponseException e) {
                throw new RuntimeException(e);
            } catch (InsufficientDataException e) {
                throw new RuntimeException(e);
            } catch (InternalException e) {
                throw new RuntimeException(e);
            } catch (InvalidKeyException e) {
                throw new RuntimeException(e);
            } catch (InvalidResponseException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (NoSuchAlgorithmException e) {
                throw new RuntimeException(e);
            } catch (ServerException e) {
                throw new RuntimeException(e);
            } catch (XmlParserException e) {
                throw new RuntimeException(e);
            }
        }

        song.setId(id);
        song.setUrl(filePath);
        song.setName(fileName);
        if (songMapper.updateById(song) > 0) {
            return R.success("更新成功", filePath);
        } else {
            return R.error("更新失败");
        }
    }

    @Override
    public R updateSongPic(MultipartFile urlFile, int id) {
        String fileName = urlFile.getOriginalFilename();
        String filePath = "/img/songPic/" + fileName;
        
        // 上传图片文件
        String result = minioUploadController.uploadSongImgFile(urlFile);
        if (!"File uploaded successfully!".equals(result)) {
            return R.error("图片上传失败: " + result);
        }

        Song song = new Song();
        song.setId(id);
        song.setPic(filePath);
        
        if (songMapper.updateById(song) > 0) {
            return R.success("更新成功", filePath);
        } else {
            return R.error("更新失败");
        }
    }

    @Override
    public R deleteSong(Integer id) {
        Song song = songMapper.selectById(id);
        String path = song.getUrl();
        String[] parts = path.split("/");
        String fileName = parts[parts.length - 1];
        System.out.println(fileName);
        RemoveObjectArgs removeObjectArgs=RemoveObjectArgs.builder()
                .bucket(bucketName)
                .object(fileName)
                .build();
        if (songMapper.deleteById(id) > 0) {
            try {
                minioClient.removeObject(removeObjectArgs);
            } catch (ErrorResponseException e) {
                throw new RuntimeException(e);
            } catch (InsufficientDataException e) {
                throw new RuntimeException(e);
            } catch (InternalException e) {
                throw new RuntimeException(e);
            } catch (InvalidKeyException e) {
                throw new RuntimeException(e);
            } catch (InvalidResponseException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (NoSuchAlgorithmException e) {
                throw new RuntimeException(e);
            } catch (ServerException e) {
                throw new RuntimeException(e);
            } catch (XmlParserException e) {
                throw new RuntimeException(e);
            }
            return R.success("删除成功");
        } else {
            return R.error("删除失败");
        }
    }

    @Override
    public R songOfSingerId(Integer singerId) {
        QueryWrapper<Song> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("singer_id",singerId);
        return R.success(null, songMapper.selectList(queryWrapper));
    }

    @Override
    public R songOfId(Integer id) {
        QueryWrapper<Song> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id",id);
        return R.success(null, songMapper.selectList(queryWrapper));
    }

    @Override
    public R songOfSingerName(String name) {
        QueryWrapper<Song> queryWrapper = new QueryWrapper<>();
        queryWrapper.like("name",name);
        List<Song> songs = songMapper.selectList(queryWrapper);
        if (songs.isEmpty()){
            return R.error("添加失败，没有找到该歌,无法加入该歌单");
        }

        return R.success(null, songMapper.selectList(queryWrapper));
    }

    @Override
    public R updateSongLrc(MultipartFile lrcFile, int id) {
        Song song = songMapper.selectById(id);
        if (lrcFile!=null&&!(song.getLyric().equals("[00:00:00]暂无歌词"))){
            byte[] fileContent = new byte[0];
            try {
                fileContent = lrcFile.getBytes();
                String content = new String(fileContent, "GB2312");
                song.setLyric(content);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        if (songMapper.updateById(song) > 0) {
            return R.success("更新成功");
        } else {
            return R.error("更新失败");
        }
    }
}
