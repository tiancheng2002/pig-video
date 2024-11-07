package com.zhu.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class SearchVo implements Serializable {

    private List<UserVo> userList;

    private List<VideoVo> videoList;

    private List<?> dataList;

    private static final long serialVersionUID = 1L;

}
