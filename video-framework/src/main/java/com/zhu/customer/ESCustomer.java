package com.zhu.customer;

import com.zhu.constant.MQConstant;
import com.zhu.model.dto.video.VideoEsDTO;
import com.zhu.model.entity.VideoInfo;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class ESCustomer {

    @Resource
    private ElasticsearchRestTemplate elasticsearchRestTemplate;

    @RabbitListener(queues = MQConstant.ES_QUEUE)
    public void updateESData(VideoInfo videoInfo){
        //异步更新ES当中的数据，并且需要保证消息要被消费成功，也就是ES中的消息必须得到同步
        VideoEsDTO videoEsDTO = VideoEsDTO.objToDto(videoInfo);
        elasticsearchRestTemplate.save(videoEsDTO);
    }

}
