package com.zhu.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zhu.model.entity.UserBrowse;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author xiaozhu
 * @since 2023-12-04
 */
public interface IUserBrowseService extends IService<UserBrowse> {

    int actionBrowse(Long videoId,Long uid);

}
