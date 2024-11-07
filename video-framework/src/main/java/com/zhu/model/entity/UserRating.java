package com.zhu.model.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * <p>
 * 
 * </p>
 *
 * @author xiaozhu
 * @since 2023-12-05
 */
@TableName("t_user_rating")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserRating implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long uid;

    private Long videoId;

    private Integer rate;

    public UserRating(Long uid, Long videoId, Integer rate) {
        this.uid = uid;
        this.videoId = videoId;
        this.rate = rate;
    }

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date rateTime;

}
