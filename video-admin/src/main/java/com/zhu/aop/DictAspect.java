package com.zhu.aop;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zhu.annotate.Dict;
import com.zhu.constant.DataConstant;
import com.zhu.utils.ObjConvertUtils;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Aspect
@Component
@Slf4j
public class DictAspect {

    @Autowired
    @Qualifier("myRedisTemplate")
    private RedisTemplate redisTemplate;

//    @Pointcut("@annotation(dict)")
//    public void dictMethod(Dict dict){
//
//    }
//
//    @Before("dictMethod(dict)")
//    public void doBefore(JoinPoint joinPoint,Dict dict) {
//        System.out.println(1);
//        System.out.println(joinPoint.getArgs());
//        System.out.println(dict.code());
//    }

//    @Before("@annotation(dict)")
//    public void doBefore(Dict dict){
//        System.out.println(dict.code());
//    }

    @Pointcut("execution( * com.zhu.controller.system.*.*(..))")
    public void dict() {

    }

    @Around("dict()")
    public Object doAround(ProceedingJoinPoint pjp) throws Throwable {
        //获取接口方法对应的返回值
        Object result = pjp.proceed();
//        System.out.println(result);
        Object value = this.parseDictText(result);
//        System.out.println("value:"+value);
        return value;
    }

    private Object parseDictText(Object result){
        if(result instanceof List){
            List<JSONObject> items = new ArrayList<>();
            List<?> list = (List<?>) result;
            for (Object record : list){
                ObjectMapper objectMapper = new ObjectMapper();
                String json = "";
                try {
                    json = objectMapper.writeValueAsString(record);
                } catch (JsonProcessingException e) {
                    log.error("json解析失败");
                }
                JSONObject item = JSONObject.parseObject(json);
                for (Field field : ObjConvertUtils.getAllFields(record)){
                    if(field.getAnnotation(Dict.class)!=null){
                        // 拿到注解的dictDataSource属性的值
                        String dictType = field.getAnnotation(Dict.class).code();
                        // 拿到注解的dictText属性的值
                        String text = field.getAnnotation(Dict.class).dicText();
                        //获取当前带翻译的值
                        String key = String.valueOf(item.get(field.getName()));
                        //翻译字典值对应的text值
                        String textValue = translateDictValue(dictType, key);
                        // DICT_TEXT_SUFFIX的值为，是默认值：
                        // public static final String DICT_TEXT_SUFFIX = "_dictText";
                        log.debug("字典Val: " + textValue);
                        log.debug("翻译字典字段：" + field.getName() + DataConstant.DICT_TEXT_SUFFIX + "： " + textValue);
                        //如果给了文本名
                        if (!StringUtils.isBlank(text)) {
                            item.put(text, textValue);
                        } else {
                            // 走默认策略
                            item.put(field.getName() + DataConstant.DICT_TEXT_SUFFIX, textValue);
                        }
                    }
                    if ("java.util.Date".equals(field.getType().getName())
                            && field.getAnnotation(JsonFormat.class) == null
                            && item.get(field.getName()) != null) {
                        SimpleDateFormat aDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        item.put(field.getName(), aDate.format(new Date((Long) item.get(field.getName()))));
                    }
                }
                items.add(item);
            }
            return items;
        }
        return result;
    }

    private String translateDictValue(String dictType, String key) {
//        System.out.println("code:"+dictType);
//        System.out.println("key:"+key);
        if (ObjConvertUtils.isEmpty(key)) {
            return null;
        }
        Object value = redisTemplate.opsForValue().get(dictType + ":" + key);

//        StringBuffer textValue = new StringBuffer();
//        String[] keys = key.split(",");
//        for (String k : keys) {
//            if (k.trim().length() == 0) {
//                continue;
//            }
//            /**
//             * 根据 dictCode 和 code 查询字典值，例如：dictCode:sex,code:1，返回:男
//             * 应该放在redis，提高响应速度
//             */
//            com.zhu.pojo.Dict dict = (com.zhu.pojo.Dict) redisTemplate.opsForValue().get(dictType + ":" + key);
//            if (dictData.getName() != null) {
//                if (!"".equals(textValue.toString())) {
//                    textValue.append(",");
//                }
//                textValue.append(dictData.getName());
//            }
//            log.info("数据字典翻译: 字典类型：{}，当前翻译值：{}，翻译结果：{}", dictType, k.trim(), dictData.getName());
//        }
        return value.toString();
    }

}
