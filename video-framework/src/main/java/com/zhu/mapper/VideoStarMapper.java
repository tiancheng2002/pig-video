package com.zhu.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zhu.model.entity.VideoInfo;
import com.zhu.model.entity.VideoStar;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author xiaozhu
 * @since 2023-10-27
 */
public interface VideoStarMapper extends BaseMapper<VideoStar> {

    Page<VideoInfo> listStarVideoByPage(IPage<VideoInfo> videoInfoIPage, @Param("uid") long uid);

}
