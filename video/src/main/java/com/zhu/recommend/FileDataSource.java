package com.zhu.recommend;

import cn.hutool.core.util.RandomUtil;
import com.zhu.model.entity.User;
import com.zhu.model.entity.VideoInfo;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.util.Lists;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;

/**
 * @author tarzan
 */
@Data
@Slf4j
public class FileDataSource {


      public  static String folderPath;
      static {
          folderPath= Objects.requireNonNull(FileDataSource.class.getResource("/data")).getPath();
      }


    /**
     * 方法描述: 读取基础数据
     *
     * @author tarzan
     * @date 2020年07月31日 16:53:40
     */
    public static List<VideoInfo> getData(List<User> userList) {
        List<VideoInfo> relateList = Lists.newArrayList();
        try {
            FileInputStream out = new FileInputStream("D:\\tool\\recommend_system-master\\recommend_system-master\\target\\classes\\ml-100k\\DVideo.data");
            InputStreamReader reader = new InputStreamReader(out, StandardCharsets.UTF_8);
            BufferedReader in = new BufferedReader(reader);
            String line;
            while ((line = in.readLine()) != null) {
                String newline = line.replaceAll("[\t]", " ");
                String[] ht = newline.split(" ");
                String content = ht[0];
                String videoUrl = ht[1];
                String videoPic = ht[2];
                VideoInfo videoInfo = new VideoInfo();
                videoInfo.setDescription(content);
                videoInfo.setVideoUrl(videoUrl);
                videoInfo.setVideoPic(videoPic);
                videoInfo.setTypeId(RandomUtil.randomLong(2,9));
                videoInfo.setUid(userList.get(RandomUtil.randomInt(0,userList.size()/8)).getUid());
                videoInfo.setStatus(1);
                System.out.println(videoInfo);
                relateList.add(videoInfo);
            }
        } catch (IOException e) {
            log.error(e.getMessage());
        }
        return relateList;
    }

}

