package com.zhu.handler;

import com.zhu.model.enums.VideoTypeEnums;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

@Component
public class VideoHandlerRegister {

    @Resource
    private VideoWorksHandler videoWorksHandler;

    @Resource
    private VideoFavourHandler videoFavourHandler;

    @Resource
    private VideoStarHandler videoStarHandler;

    @Resource
    private VideoBrowseHandler videoBrowseHandler;

    private Map<String, VideoHandler> typeHandlerMap;

    @PostConstruct
    public void doInit() {
        typeHandlerMap = new HashMap() {{
            put(VideoTypeEnums.VIDEO_USER.getValue(), videoWorksHandler);
            put(VideoTypeEnums.VIDEO_FAVOUR.getValue(), videoFavourHandler);
            put(VideoTypeEnums.VIDEO_STAR.getValue(), videoStarHandler);
            put(VideoTypeEnums.VIDEO_HISTORY.getValue(),videoBrowseHandler);
        }};
    }

    public VideoHandler getDataSourceByType(String type) {
        if (typeHandlerMap == null) {
            return null;
        }
        return typeHandlerMap.get(type);
    }

}
