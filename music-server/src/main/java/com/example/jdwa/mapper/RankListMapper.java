package com.example.jdwa.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.jdwa.model.domain.RankList;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface RankListMapper extends BaseMapper<RankList> {

    /**
     * 查总分
     * @param songListId
     * @return
     */
    int selectScoreSum(Long songListId);

    /**
     * 查制定用户评分
     * @param consumerId
     * @param songListId
     * @return
     */
    Integer selectUserRank(@Param("consumer_id") Long consumerId, @Param("song_list_id")  Long songListId);
}
