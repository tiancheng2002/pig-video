package com.zhu.utils;

import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.FFmpegFrameRecorder;
import org.bytedeco.javacv.Frame;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class WhisperUtils {

    public static final int secondTime = 30;

    public static void main(String[] args) throws Exception {
        String audioPath = "D:\\SoftWare\\PyTorch\\test.mp4";
        System.out.println(mySplitAndProcessVideo(audioPath));
//        System.out.println(parseTime(90));
    }

    public static String parseTime(int time){
        //计算出传入时间的分和秒并格式化后返回
        StringBuilder resultTime = new StringBuilder();
        int minute = time / 60;
        int second = time % 60;
        if(minute>10){
            resultTime.append(minute);
        }else{
            resultTime.append("0").append(minute);
        }
        resultTime.append(":");
        if(second>10){
            resultTime.append(second);
        }else{
            resultTime.append("0").append(second);
        }
        return resultTime.toString();
    }

    public static String mySplitAndProcessVideo(String inputVideoPath) throws Exception {
        FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(inputVideoPath);
        grabber.start();

        long totalTime = grabber.getLengthInTime()/(1000*1000);
        long splitNum = totalTime/secondTime;

        ExecutorService executor = Executors.newFixedThreadPool(4);
        List<Future<String>> futures = new ArrayList<>();

        for (int i = 0; i < splitNum; i++) {
            int startFrame = i * secondTime;
            Future<String> future = executor.submit(() -> myProcessVideoSegment(inputVideoPath, startFrame));
            futures.add(future);
        }

        executor.shutdown();
        executor.awaitTermination(Long.MAX_VALUE, TimeUnit.MILLISECONDS);

        StringBuilder result = new StringBuilder();
        for (Future<String> future : futures) {
            result.append(future.get());
        }

        grabber.stop();
        return result.toString();
    }

    private static String myProcessVideoSegment(String inputVideoPath, int startFrame) throws Exception {
        //将拆分的视频保存到临时目录下面，处理完后删除所有的拆分视频
        String outputVideoPath = "D:\\SoftWare\\PyTorch\\temp\\video_" + startFrame + "_" + (startFrame+secondTime) + ".mp4";
//
//        String command = "ffmpeg -ss "+parseTime(startFrame)+" -t "+parseTime(secondTime)+" -i "+inputVideoPath+" -c copy "+outputVideoPath;
//
//        Process process = Runtime.getRuntime().exec(command);

        String result = doWhisper(outputVideoPath);

        return result;
    }

    //分割长视频
    public static String splitAndProcessVideo(String inputVideoPath) throws Exception {
        FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(inputVideoPath);
        grabber.start();

        int frameRate = (int) grabber.getFrameRate();
        int totalFrames = grabber.getLengthInFrames();
        int framesPerSegment = frameRate * 30;
        int segments = totalFrames / framesPerSegment;

        ExecutorService executor = Executors.newFixedThreadPool(4);
        List<Future<String>> futures = new ArrayList<>();

        for (int i = 0; i < segments; i++) {
            int startFrame = i * framesPerSegment;
            int endFrame = (i + 1) * framesPerSegment - 1;
            if (i == segments - 1) {
                endFrame = totalFrames - 1;
            }

            int finalEndFrame = endFrame;
            Future<String> future = executor.submit(() -> processVideoSegment(inputVideoPath, startFrame, finalEndFrame));
            futures.add(future);
        }

        executor.shutdown();
        executor.awaitTermination(Long.MAX_VALUE, TimeUnit.MILLISECONDS);

        StringBuilder result = new StringBuilder();
        for (Future<String> future : futures) {
            result.append(future.get());
            System.out.println("处理结果： " + future.get());
        }

        grabber.stop();
        return result.toString();
    }

    //并行处理拆分视频，对拆分的视频进行内容分析
    private static String processVideoSegment(String inputVideoPath, int startFrame, int endFrame) throws Exception {
        //将拆分的视频保存到临时目录下面，处理完后删除所有的拆分视频
        String outputVideoPath = "D:\\SoftWare\\PyTorch\\temp\\video_" + startFrame + "_" + endFrame + ".mp4";

        FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(inputVideoPath);
        grabber.start();
        grabber.setFrameNumber(startFrame);

        FFmpegFrameRecorder recorder = new FFmpegFrameRecorder(outputVideoPath, grabber.getImageWidth(), grabber.getImageHeight());
        recorder.setFrameRate(grabber.getFrameRate());
        recorder.setVideoCodec(grabber.getVideoCodec());
        recorder.setAudioCodec(grabber.getAudioCodec());
        recorder.start();

        while (recorder.getFrameNumber() <= endFrame) {
            Frame frame = grabber.grabImage();
            recorder.record(frame);
        }

        recorder.stop();
        grabber.stop();

        // 在这里处理视频片段，例如分析、编辑等操作
//        String result = doWhisper(outputVideoPath);

        return "ok";
    }


    //用Whisper模型识别视频中的文字信息
    //只分析一分钟以上的视频，太短的视频直接不分析
    //分析出来的文字数量如何很少的话，也不会去星火大模型进行视频内容总结
    public static String doWhisper(String audioPath){
        StringBuilder result = new StringBuilder();
        String command = "whisper " + audioPath + " --model small --output_format srt --output_dir D:\\SoftWare\\PyTorch --language Chinese --device cpu";
        try {
            System.out.println("进入");
            // 执行whisper命令提取视频字幕
            Process process = Runtime.getRuntime().exec(command);
            BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getInputStream(),"GBK"));
            String line;
            while ((line = errorReader.readLine()) != null) {
                //将识别的结果进行整理返回
                result.append(line);
                System.out.println(line);
            }
            process.waitFor();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
//        finally {
            // 删除保存的视频文件
//            File outputVideoFile = new File(audioPath);
//            if (outputVideoFile.exists()) {
//                outputVideoFile.delete();
//            }
//        }
        return result.toString();
    }

}
