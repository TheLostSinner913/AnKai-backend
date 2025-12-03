package com.ankai;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

/**
 * IP定位 + 天气API 测试
 * 
 * 测试流程：
 * 1. 根据IP获取地理位置（城市）
 * 2. 根据城市获取天气数据
 */
public class WeatherApiTest {

    private static final HttpClient client = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    public static void main(String[] args) {
        System.out.println("========== IP定位 + 天气API 测试 ==========\n");

        // 直接使用指定的IP地址进行测试
        String testIp = "218.68.145.137";
        System.out.println("测试IP地址: " + testIp + "\n");

        // 测试: 根据IP获取地理位置（寻找能返回区/县级别的API）
        testIpLocation(testIp);
    }

    /**
     * 获取公网IP
     */
    private static String getPublicIp() {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://api.ipify.org"))
                    .timeout(Duration.ofSeconds(5))
                    .GET()
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            return response.body();
        } catch (Exception e) {
            System.out.println("获取公网IP失败: " + e.getMessage());
            return "unknown";
        }
    }

    /**
     * 测试IP定位API - 寻找能返回区/县级别的API
     */
    private static void testIpLocation(String ip) {
        System.out.println("--- 测试IP定位API (寻找区/县级别定位) ---");
        System.out.println("测试IP: " + ip + "\n");

        // 1. 太平洋IP定位（国内可用，但只到市级）
        testApi("太平洋IP库", "https://whois.pconline.com.cn/ipJson.jsp?ip=" + ip + "&json=true");

        // 2. 百度开放数据（免费，可能有区级信息）
        testApi("百度开放数据", "https://opendata.baidu.com/api.php?query=" + ip + "&co=&resource_id=6006&oe=utf8");

        // 3. ip.zxinc.org（国内IP库）
        testApi("zxinc", "https://ip.zxinc.org/api.php?type=json&ip=" + ip);

        // 4. 测试 ip-api.com（免费，有district字段，但国内可能不稳定）
        testApi("ip-api.com", "http://ip-api.com/json/" + ip
                + "?lang=zh-CN&fields=status,message,country,regionName,city,district,lat,lon");

        // 5. 高德地图IP定位（需要申请免费key，每日5000次）
        // 申请地址: https://lbs.amap.com/api/webservice/guide/api/ipconfig
        String amapKey = "YOUR_AMAP_KEY"; // 替换为你的key
        testApi("高德地图IP定位", "https://restapi.amap.com/v3/ip?ip=" + ip + "&key=" + amapKey);

        // 6. 腾讯位置服务（需要申请免费key，每日10000次）
        // 申请地址: https://lbs.qq.com/service/webService/webServiceGuide/webServiceIp
        String tencentKey = "YOUR_TENCENT_KEY"; // 替换为你的key
        testApi("腾讯位置服务", "https://apis.map.qq.com/ws/location/v1/ip?ip=" + ip + "&key=" + tencentKey);

        System.out.println("\n===== 总结 =====");
        System.out.println("免费API大多只能定位到【市级】，要获取【区/县级】需要：");
        System.out.println("1. 高德地图API（免费5000次/日）: https://lbs.amap.com/");
        System.out.println("2. 腾讯位置服务（免费10000次/日）: https://lbs.qq.com/");
        System.out.println("3. 百度地图API（免费5000次/日）: https://lbsyun.baidu.com/");
        System.out.println("\n注意：即使使用付费API，IP定位精度也受限于运营商IP分配，");
        System.out.println("有些IP可能只能定位到市级，无法精确到区/县。");
    }

    /**
     * 测试天气API
     */
    private static void testWeatherApi() {
        System.out.println("--- 测试天气API ---");

        // 1. 和风天气（需要key，免费版每天1000次）
        // 需要注册获取key: https://dev.qweather.com/
        // testApi("和风天气",
        // "https://devapi.qweather.com/v7/weather/now?location=101010100&key=YOUR_KEY");

        // 2. 心知天气（需要key，免费版每天400次）
        // 需要注册获取key: https://www.seniverse.com/
        // testApi("心知天气",
        // "https://api.seniverse.com/v3/weather/now.json?key=YOUR_KEY&location=beijing");

        // 3. wttr.in（免费，无需key，但国内可能较慢）
        testApi("wttr.in (上海)", "https://wttr.in/Shanghai?format=j1");

        // 4. OpenMeteo（完全免费，无需key，推荐）
        // 使用经纬度查询，北京: 39.9042, 116.4074
        testApi("Open-Meteo (北京)",
                "https://api.open-meteo.com/v1/forecast?latitude=39.9042&longitude=116.4074&current=temperature_2m,weather_code,relative_humidity_2m&timezone=Asia/Shanghai");

        // 上海: 31.2304, 121.4737
        testApi("Open-Meteo (上海)",
                "https://api.open-meteo.com/v1/forecast?latitude=31.2304&longitude=121.4737&current=temperature_2m,weather_code,relative_humidity_2m&timezone=Asia/Shanghai");

        System.out.println();
    }

    /**
     * 通用API测试方法
     */
    private static void testApi(String name, String url) {
        long start = System.currentTimeMillis();
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .timeout(Duration.ofSeconds(10))
                    .header("User-Agent", "Mozilla/5.0")
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            long elapsed = System.currentTimeMillis() - start;

            if (response.statusCode() == 200) {
                String body = response.body();
                // 截取前300字符显示
                String preview = body.length() > 300 ? body.substring(0, 300) + "..." : body;
                System.out.println("✅ " + name + " (耗时: " + elapsed + "ms)");
                System.out.println("   响应: " + preview);
            } else {
                System.out.println("❌ " + name + " - HTTP " + response.statusCode());
            }
        } catch (Exception e) {
            long elapsed = System.currentTimeMillis() - start;
            System.out.println("❌ " + name + " (耗时: " + elapsed + "ms) - " + e.getMessage());
        }
        System.out.println();
    }
}
