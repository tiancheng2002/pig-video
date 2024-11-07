package com.zhu.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.zhu.model.entity.Dict;
import com.zhu.model.entity.DictData;
import com.zhu.model.vo.DictTypeVo;
import com.zhu.model.vo.DictVo;
import com.zhu.service.IDictDataService;
import com.zhu.service.IDictService;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author xiaozhu
 * @since 2022-08-28
 */
@RestController
@RequestMapping("/admin/dict")
public class DictController implements InitializingBean {

    @Autowired
    @Qualifier("myRedisTemplate")
    private RedisTemplate redisTemplate;

    @Autowired
    private IDictService dictService;

    @Autowired
    private IDictDataService dictDataService;

    /*
        获取所有的字典数据
     */
    @RequestMapping("/all")
    public List<Dict> getDict(Dict dict){
        return dictService.getDict(dict);
    }

    /*
        获取该字典下的所有数据
     */
    @RequestMapping("/detail")
    public List<DictData> getDictDetail(@RequestParam("dictId") Integer dictId){
        List<DictData> dictData = dictDataService.list(new QueryWrapper<DictData>().eq("dict_id", dictId));
        return dictData;
    }

    /*
        根据id获取字典数据
     */
    @RequestMapping("/id")
    public Dict getDictById(@RequestParam("id") Integer id){
        Dict dict = dictService.getById(id);
        return dict;
    }

    /*
        根据id获取字典下的详情数据
     */
    @RequestMapping("/data/id")
    public DictData getDictDataById(@RequestParam("id") Integer id){
        DictData dictData = dictDataService.getById(id);
        return dictData;
    }

    /*
        根据code获取所有的字典信息
     */
    @RequestMapping("/selectCode")
    public List<DictTypeVo> getDictByCode(@RequestParam("dictCode") String dictCode){
        return dictService.getDictByCode(dictCode);
    }

    /*
        添加或更新字典下的指定数据
     */
    @PostMapping("/data/action")
    public String actionDictData(@RequestBody DictData dictData){
        if(dictData.getId()!=null){
            dictDataService.updateById(dictData);
        }else{
            dictDataService.save(dictData);
        }
        return "ok";
    }

    /*
        添加或更新字典数据
     */
    @PostMapping("/action")
    public void actionDict(@RequestBody Dict dict){
        if(dict.getId()!=null){
            dictService.updateById(dict);
        }else{
            dictService.save(dict);
        }
    }

    /*
        删除字典数据
     */
    @PostMapping("/del")
    public String delDict(@RequestBody List<Integer> selectionList){
        dictService.removeBatchByIds(selectionList);
        return "ok";
    }

    /*
        删除字典下的指定数据
     */
    @PostMapping("/data/del")
    public String delDictData(@RequestBody List<Integer> selectionList){
        dictDataService.removeBatchByIds(selectionList);
        return "ok";
    }

    /*
        刷新字典数据到缓存当中
     */
    @RequestMapping("/cache")
    public String cache(){
        dictCache();
        return "刷新缓存成功";
    }

    public void dictCache(){
        List<DictVo> dictVos = dictService.getDictDetail();
        for (DictVo dictVo : dictVos){
            for (DictData dictData : dictVo.getDictDataList()){
                redisTemplate.opsForValue().set(dictVo.getCode()+":"+dictData.getValue(),dictData.getLabel());
            }
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        dictCache();
//        List<DictVo> dictVos = dictService.getDictDetail();
//        for (DictVo dictVo : dictVos){
//            for (DictData dictData : dictVo.getDictDataList()){
//                redisTemplate.opsForValue().set(dictVo.getCode()+":"+dictData.getValue(),dictData.getLabel());
//            }
//        }
    }

}
