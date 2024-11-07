package com.zhu.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.zhu.common.CommonEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * <p>
 * 
 * </p>
 *
 * @author xiaozhu
 * @since 2023-10-24
 */
@TableName("t_video_info")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class VideoInfo extends CommonEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "video_id", type = IdType.AUTO)
    private Long videoId;

    private String description;

    private String videoUrl;

    private String videoPic;

    private Long typeId;

//    private Boolean isDraft;

    private Long playNum;

    private Long favourNum;

    private Long starNum;

    private Long commentNum;

    private Long shareNum;

    private Integer status;

    private Long uid;

}
