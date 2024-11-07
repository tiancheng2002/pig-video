package com.zhu.config;

import com.zhu.constant.MQConstant;
import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    @Bean
    public TopicExchange videoTopic(){
        return new TopicExchange(MQConstant.VIDEO_TOPIC);
    }

    @Bean
    public Queue messageQueue(){
        return new Queue(MQConstant.MESSAGE_QUEUE);
    }

    @Bean
    public Queue elasticSearchQueue(){
        return new Queue(MQConstant.ES_QUEUE);
    }

    @Bean
    public Binding DLBinding(){
        return BindingBuilder.bind(messageQueue()).to(videoTopic()).with("video.message.#");
    }

    @Bean
    public Binding ESBinding(){
        return BindingBuilder.bind(elasticSearchQueue()).to(videoTopic()).with("video.elasticSearch.#");
    }

}
