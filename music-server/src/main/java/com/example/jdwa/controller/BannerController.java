package com.example.jdwa.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.jdwa.common.R;
import com.example.jdwa.service.BannerService;

/**
 * @Author 祝英台炸油条
 * @Time : 2022/6/13 13:16
 **/
@RestController
@RequestMapping("/banner")
public class BannerController {

    @Autowired
    private BannerService bannerService;

    @GetMapping("/getAllBanner")
    public R getAllBanner(){
        return R.success("成功获取轮播图与",bannerService.getAllBanner());
    }
}
