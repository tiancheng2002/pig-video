package com.zhu.model.dto.video;

import com.zhu.model.entity.VideoInfo;
import lombok.Data;
import org.springframework.beans.BeanUtils;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.Date;

@Data
@Document(indexName = "video")
public class VideoEsDTO {

    private static final String DATE_TIME_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";

    @Id
    private Long videoId;


    private String description;

    @Field(type = FieldType.Long)
    private Long favourNum;

    @Field(type = FieldType.Long)
    private Long starNum;

    @Field(type = FieldType.Long)
    private Long typeId;

    @Field(type = FieldType.Long)
    private Long uid;

    @Field(type = FieldType.Integer)
    private Integer status;

    @Field(index = false, store = true, type = FieldType.Date, format = {}, pattern = DATE_TIME_PATTERN)
    private Date createTime;

    @Field(index = false, store = true, type = FieldType.Date, format = {}, pattern = DATE_TIME_PATTERN)
    private Date updateTime;

    private static final long serialVersionUID = 1L;

    /**
     * 对象转包装类
     *
     * @param videoInfo
     * @return
     */
    public static VideoEsDTO objToDto(VideoInfo videoInfo) {
        if (videoInfo == null) {
            return null;
        }
        VideoEsDTO postEsDTO = new VideoEsDTO();
        BeanUtils.copyProperties(videoInfo, postEsDTO);
        return postEsDTO;
    }

    /**
     * 包装类转对象
     *
     * @param videoEsDTO
     * @return
     */
    public static VideoInfo dtoToObj(VideoEsDTO videoEsDTO) {
        if (videoEsDTO == null) {
            return null;
        }
        VideoInfo post = new VideoInfo();
        BeanUtils.copyProperties(videoEsDTO, post);
        return post;
    }

}
