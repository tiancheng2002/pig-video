package com.zhu.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.util.Map;

@Slf4j
public class AddressUtils {

    public static Map getAddress(String ip) {
        OkHttpClient httpClient = new OkHttpClient();
//        String ip = "172.24.99.10";
        Map resultMap = null;
        String url = "http://whois.pconline.com.cn/ipJson.jsp?ip=" + ip + "&json=true";
        Request request = new Request.Builder()
                .url(url)
                .build();
        try {
            Response response = httpClient.newCall(request).execute();
            String result = response.body().string();
            ObjectMapper objectMapper = new ObjectMapper();
            resultMap = objectMapper.readValue(result, Map.class);
//            System.out.println("ip信息：" + resultMap);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resultMap;
    }

    public static void main(String[] args) {
        System.out.println(getAddress("125.117.20.94"));
    }

}
