package com.zhu.vo;

import com.zhu.model.entity.User;
import com.zhu.model.entity.VideoInfo;
import lombok.Data;

@Data
public class VideoVo extends VideoInfo {

    private boolean isStar;

    private boolean isFavour;

    private User user;

    @Override
    public String toString() {
        return "VideoVo{" +
                "isStar=" + isStar +
                ", isFavour=" + isFavour +
                ", user=" + user +
                "} " + super.toString();
    }
}
