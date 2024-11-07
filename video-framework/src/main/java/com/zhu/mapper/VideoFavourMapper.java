package com.zhu.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zhu.model.entity.VideoFavour;
import com.zhu.model.entity.VideoInfo;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author xiaozhu
 * @since 2023-10-31
 */
public interface VideoFavourMapper extends BaseMapper<VideoFavour> {

    Page<VideoInfo> listFavourVideoByPage(IPage<VideoInfo> videoInfoIPage, @Param("uid") long uid);

}
