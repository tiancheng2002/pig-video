package com.zhu.utils;


import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.FFmpegFrameRecorder;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.Java2DFrameConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import javax.imageio.ImageIO;
import javax.imageio.stream.ImageOutputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName: VideoCoverUtils
 * @Description:
 * @Author: zhanghongwei
 * @Date: 2022/5/31 20:05
 */
public class VideoCoverUtils {
    private static final Logger logger = LoggerFactory.getLogger(VideoCoverUtils.class);

    public static void main(String[] args) throws Exception {
        getImage("D:\\","video.mp4",20);
//        Map<String, Object> vedioImg = getVedioImg("https://image.xiaozhu02.top/", "video1.mp4", 20);
//        System.out.println(vedioImg);
//        String videoPath = "C:\\Users\\86183\\Desktop\\video1.mp4";
//        String videoPath = "D:\\SoftWare\\PyTorch\\out.mp4";
        // 输出音频文件路径
//        String audioPath = "C:\\Users\\86183\\Desktop\\output.mp3";
//        doWhisper(videoPath);
    }

    //提取视频当中的音频，输出到指定的地址
    public static void extractVoice(String sourceFileName, String audioUrl) throws FFmpegFrameGrabber.Exception, FFmpegFrameRecorder.Exception {
        //抓取资源
        FFmpegFrameGrabber frameGrabber = new FFmpegFrameGrabber(sourceFileName);
        Frame frame = null;
        FFmpegFrameRecorder recorder = null;
        frameGrabber.start();
        //转录为单轨, 16K采样率, wav格式
        recorder = new FFmpegFrameRecorder(audioUrl, frameGrabber.getAudioChannels());//frameGrabber.getAudioChannels()
//        recorder.setFormat("mp3");
        recorder.setFormat(frameGrabber.getFormat());
        recorder.setSampleRate(frameGrabber.getSampleRate());//frameGrabber.getSampleRate()
        //recorder.setAudioBitrate(128000);// 音频比特率
        recorder.setTimestamp(frameGrabber.getTimestamp());

        recorder.start();
        int index = 0;
        while (true) {
            frame = frameGrabber.grabSamples();
            if (frame == null) break;
            if (frame.samples != null) {
                recorder.recordSamples(frame.sampleRate, frame.audioChannels, frame.samples);
                recorder.setTimestamp(frameGrabber.getTimestamp());
            }
            index++;
        }
        recorder.stop();
        recorder.release();
        frameGrabber.stop();
        frameGrabber.release();
    }

    public static void getOut(){
        // 视频文件路径
        String videoPath = "C:\\Users\\86183\\Desktop\\video31.mp4";
        // 输出音频文件路径
        String audioPath = "C:\\Users\\86183\\Desktop\\output.mp3";

        // ffmpeg命令行
        String command = "ffmpeg -i " + videoPath + " -vn -ar 44100 -ac 2 -ab 192k -f mp3 " + audioPath;

        try {
            // 执行ffmpeg命令
            Process process = Runtime.getRuntime().exec(command);
            BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            String line;
            while ((line = errorReader.readLine()) != null) {
                System.out.println("ffmpeg error: " + line);
            }
            process.waitFor();
            System.out.println("Audio extraction completed.");
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void getVideoSubtitle(){
        String videoPath = "C:\\Users\\86183\\Desktop\\video1.mp4"; // 视频文件路径
        FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(videoPath);

        try {
            grabber.start();
            int lengthInFrames = grabber.getLengthInFrames();

            // 遍历所有帧
            for (int i = 0; i < lengthInFrames; i++) {
                Frame frame = grabber.grabFrame();
                if (frame != null) {
                    // 检查是否包含字幕
                    if (frame.samples != null) {
                        // 这里处理字幕帧
                        // 例如，可以打印出字幕内容
                        System.out.println(frame.samples.length);
                        for (int j = 0; j < frame.samples.length; j++) {
                            System.out.println(frame.samples[j].toString());
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (grabber != null) {
                    grabber.stop();
                }
            } catch (FFmpegFrameGrabber.Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 获取视频图片
     *
     * @throws
     * @Title: gainVedioImg
     * @param: filePath  文件夹路径
     * @return: void
     */
    public static Map<String, Object> getVedioImg(String filePath, String fileName, int second) {
        return  getImageInputStream(filePath, fileName, second);
    }

    /**
     * 获取视频图片
     *
     * @throws
     * @Title: gainVedioImg
     * @param:
     * @return: void
     */
    public static Map<String, Object> getVedioImg(String filePath, String fileName) {
        return getImageInputStream(filePath, fileName, 1);
    }

    private static Map<String, Object> getImageInputStream(String filePath, String fileName, int second) {
        Map<String, Object> result = new HashMap<String, Object>();
        File folder = new File(filePath);
        if (!folder.exists()) {
            folder.mkdirs();//如果文件夹不存在则创建
        }
        try {
            FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(filePath + fileName);
            grabber.start();
            int ftp = grabber.getLengthInFrames();
            int flag = 0;
            Frame frame = null;
            while (flag <= ftp) {
                //获取帧
                frame = grabber.grabImage();
                //过滤前 second 帧，避免出现全黑图片
                if ((flag > second) && (frame != null)) {
                    break;
                }
                flag++;
            }
            String rotate = grabber.getVideoMetadata("rotate");
            //创建BufferedImage对象
            Java2DFrameConverter converter = new Java2DFrameConverter();
            BufferedImage bufferedImage = converter.getBufferedImage(frame);
            if (rotate != null) {
                //旋转图片
                bufferedImage = rotate(bufferedImage, Integer.parseInt(rotate));
            }
            ByteArrayOutputStream bs = new ByteArrayOutputStream();
            ImageOutputStream imOut;
            imOut = ImageIO.createImageOutputStream(bs);
            ImageIO.write(bufferedImage, "jpg",imOut);
            InputStream inputStream = new ByteArrayInputStream(bs.toByteArray());
            String newFileName = fileName.substring(0, fileName.lastIndexOf(".")) + ".jpg";
            result.put("inputStream",inputStream);
            result.put("fileName",newFileName);
//            File file = new File(newFileName);
//            ImageIO.write(bufferedImage, "jpeg", file);
//            //拼接Map信息
//            result.put("videoWide", bufferedImage.getWidth());
//            result.put("videoHigh", bufferedImage.getHeight());
//            long duration = grabber.getLengthInTime() / (1000 * 1000);
//            result.put("rotate", StringUtils.isEmpty(rotate) ? "0" : rotate);
//            result.put("format", grabber.getFormat());
//            result.put("imgPath", file.getPath());
//            result.put("time（s/秒）", duration);
            grabber.close();
            grabber.stop();
            logger.debug("截取视频截图结束：" + System.currentTimeMillis());
        } catch (Exception e) {
            logger.error("获取视频图片失败！", e);
        }
        return result;
    }

    private static Map<String, Object> getImage(String filePath, String fileName, int second) {
        Map<String, Object> result = new HashMap<String, Object>();
        File folder = new File(filePath);
        if (!folder.exists()) {
            folder.mkdirs();//如果文件夹不存在则创建
        }
        try {
            FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(filePath + fileName);
            grabber.start();
            int ftp = grabber.getLengthInFrames();
            int flag = 0;
            Frame frame = null;
            while (flag <= ftp) {
                //获取帧
                frame = grabber.grabImage();
                //过滤前 second 帧，避免出现全黑图片
                if ((flag > second) && (frame != null)) {
                    break;
                }
                flag++;
            }
            String rotate = grabber.getVideoMetadata("rotate");
            //创建BufferedImage对象
            Java2DFrameConverter converter = new Java2DFrameConverter();
            BufferedImage bufferedImage = converter.getBufferedImage(frame);
            if (rotate != null) {
                //旋转图片
                bufferedImage = rotate(bufferedImage, Integer.parseInt(rotate));
            }
            String newFileName = "D:/"+fileName.substring(0, fileName.lastIndexOf(".")) + ".jpeg";
            File file = new File(newFileName);
            ImageIO.write(bufferedImage, "jpeg", file);
            //拼接Map信息
            result.put("videoWide", bufferedImage.getWidth());
            result.put("videoHigh", bufferedImage.getHeight());
            long duration = grabber.getLengthInTime() / (1000 * 1000);
            result.put("rotate", StringUtils.isEmpty(rotate) ? "0" : rotate);
            result.put("format", grabber.getFormat());
            result.put("imgPath", file.getPath());
            result.put("time（s/秒）", duration);
            grabber.close();
            grabber.stop();
            logger.debug("截取视频截图结束：" + System.currentTimeMillis());
        } catch (Exception e) {
            logger.error("获取视频图片失败！", e);
        }
        return result;
    }

    /**
     * 旋转 根据视频旋转度来调整图片
     * @param src
     * @param angel
     * @return
     */
    private static BufferedImage rotate(BufferedImage src, int angel) {
        int src_width = src.getWidth(null);
        int src_height = src.getHeight(null);
        int type = src.getColorModel().getTransparency();
        Rectangle rect_des = calcRotatedSize(new Rectangle(new Dimension(src_width, src_height)), angel);
        BufferedImage bufferedImage = new BufferedImage(rect_des.width, rect_des.height, type);
        Graphics2D graphics2D = bufferedImage.createGraphics();
        graphics2D.translate((rect_des.width - src_width) / 2, (rect_des.height - src_height) / 2);
        graphics2D.rotate(Math.toRadians(angel), src_width / 2, src_height / 2);
        graphics2D.drawImage(src, 0, 0, null);
        graphics2D.dispose();
        return bufferedImage;
    }

    /**
     * 计算图片旋转大小
     * @param src
     * @param angel
     * @return
     */
    private static Rectangle calcRotatedSize(Rectangle src, int angel) {
        if (angel >= 90) {
            if (angel / 90 % 2 == 1) {
                int temp = src.height;
                src.height = src.width;
                src.width = temp;
            }
            angel = angel % 90;
        }
        double totalSqrt = Math.sqrt(src.height * src.height + src.width * src.width) / 2;
        double totalLen = 2 * Math.sin(Math.toRadians(angel) / 2) * totalSqrt;
        double angel_alpha = (Math.PI - Math.toRadians(angel)) / 2;
        double angel_dalta_width = Math.atan((double) src.height / src.width);
        double angel_dalta_height = Math.atan((double) src.width / src.height);
        int len_width = (int) (totalLen * Math.cos(Math.PI - angel_alpha - angel_dalta_width));
        int len_height = (int) (totalLen * Math.cos(Math.PI - angel_alpha - angel_dalta_height));
        int des_width = src.width + len_width * 2;
        int des_height = src.height + len_height * 2;
        return new Rectangle(new Dimension(des_width, des_height));
    }
}