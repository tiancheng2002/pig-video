package com.zhu.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zhu.model.entity.VideoInfo;
import com.zhu.vo.VideoVo;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author xiaozhu
 * @since 2023-10-24
 */
public interface VideoInfoMapper extends BaseMapper<VideoInfo> {

    Page<VideoVo> getVideoData(Page<VideoVo> videoVoPage, @Param("type") Long type);

    VideoVo getVideoById(@Param("videoId") Long videoId);

    Page<VideoInfo> randomVideo(Page<VideoInfo> videoInfoPage);

    Page<VideoInfo> getVideoByFollow(Page<VideoInfo> videoInfoPage, @Param("uid") Long uid);

}
