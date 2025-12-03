package com.ankai.controller;

import com.ankai.common.Result;
import com.ankai.service.AnnouncementService;
import com.ankai.service.MessageService;
import com.ankai.service.TodoService;
import com.ankai.service.UserService;
import com.ankai.utils.SecurityUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ankai.entity.User;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * 首页仪表盘控制器
 *
 * @author AnKai
 */
@Slf4j
@RestController
@RequestMapping("/dashboard")
@RequiredArgsConstructor
@Tag(name = "首页仪表盘")
public class DashboardController {

    private final UserService userService;
    private final TodoService todoService;
    private final AnnouncementService announcementService;
    private final MessageService messageService;
    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    // 今日诗词API - 快速稳定（推荐）
    private static final String JINRISHICI_API = "https://v1.jinrishici.com/all.json";
    // 一言API（Hitokoto）- 备用
    private static final String HITOKOTO_API = "https://v1.hitokoto.cn/?c=d&c=h&c=i&c=k&encode=json";
    // 太平洋IP定位API
    private static final String IP_LOCATION_API = "https://whois.pconline.com.cn/ipJson.jsp?json=true&ip=";
    // Open-Meteo天气API
    private static final String WEATHER_API = "https://api.open-meteo.com/v1/forecast?current=temperature_2m,weather_code,relative_humidity_2m&timezone=Asia/Shanghai";

    // 主要城市经纬度映射表
    private static final Map<String, double[]> CITY_COORDINATES = new HashMap<>();
    static {
        // 直辖市
        CITY_COORDINATES.put("北京", new double[] { 39.9042, 116.4074 });
        CITY_COORDINATES.put("天津", new double[] { 39.3434, 117.3616 });
        CITY_COORDINATES.put("上海", new double[] { 31.2304, 121.4737 });
        CITY_COORDINATES.put("重庆", new double[] { 29.4316, 106.9123 });
        // 省会城市
        CITY_COORDINATES.put("石家庄", new double[] { 38.0428, 114.5149 });
        CITY_COORDINATES.put("太原", new double[] { 37.8706, 112.5489 });
        CITY_COORDINATES.put("呼和浩特", new double[] { 40.8414, 111.7500 });
        CITY_COORDINATES.put("沈阳", new double[] { 41.8057, 123.4315 });
        CITY_COORDINATES.put("长春", new double[] { 43.8171, 125.3235 });
        CITY_COORDINATES.put("哈尔滨", new double[] { 45.8038, 126.5350 });
        CITY_COORDINATES.put("南京", new double[] { 32.0603, 118.7969 });
        CITY_COORDINATES.put("杭州", new double[] { 30.2741, 120.1551 });
        CITY_COORDINATES.put("合肥", new double[] { 31.8206, 117.2272 });
        CITY_COORDINATES.put("福州", new double[] { 26.0745, 119.2965 });
        CITY_COORDINATES.put("南昌", new double[] { 28.6820, 115.8579 });
        CITY_COORDINATES.put("济南", new double[] { 36.6512, 117.1201 });
        CITY_COORDINATES.put("郑州", new double[] { 34.7466, 113.6254 });
        CITY_COORDINATES.put("武汉", new double[] { 30.5928, 114.3055 });
        CITY_COORDINATES.put("长沙", new double[] { 28.2282, 112.9388 });
        CITY_COORDINATES.put("广州", new double[] { 23.1291, 113.2644 });
        CITY_COORDINATES.put("南宁", new double[] { 22.8170, 108.3665 });
        CITY_COORDINATES.put("海口", new double[] { 20.0440, 110.1999 });
        CITY_COORDINATES.put("成都", new double[] { 30.5728, 104.0668 });
        CITY_COORDINATES.put("贵阳", new double[] { 26.6470, 106.6302 });
        CITY_COORDINATES.put("昆明", new double[] { 25.0389, 102.7183 });
        CITY_COORDINATES.put("拉萨", new double[] { 29.6500, 91.1000 });
        CITY_COORDINATES.put("西安", new double[] { 34.3416, 108.9398 });
        CITY_COORDINATES.put("兰州", new double[] { 36.0611, 103.8343 });
        CITY_COORDINATES.put("西宁", new double[] { 36.6171, 101.7782 });
        CITY_COORDINATES.put("银川", new double[] { 38.4872, 106.2309 });
        CITY_COORDINATES.put("乌鲁木齐", new double[] { 43.8256, 87.6168 });
        // 特别行政区
        CITY_COORDINATES.put("香港", new double[] { 22.3193, 114.1694 });
        CITY_COORDINATES.put("澳门", new double[] { 22.1987, 113.5439 });
        CITY_COORDINATES.put("台北", new double[] { 25.0330, 121.5654 });
        // 重要城市
        CITY_COORDINATES.put("深圳", new double[] { 22.5431, 114.0579 });
        CITY_COORDINATES.put("苏州", new double[] { 31.2990, 120.5853 });
        CITY_COORDINATES.put("青岛", new double[] { 36.0671, 120.3826 });
        CITY_COORDINATES.put("大连", new double[] { 38.9140, 121.6147 });
        CITY_COORDINATES.put("厦门", new double[] { 24.4798, 118.0894 });
        CITY_COORDINATES.put("宁波", new double[] { 29.8683, 121.5440 });
        CITY_COORDINATES.put("无锡", new double[] { 31.4906, 120.3119 });
        CITY_COORDINATES.put("东莞", new double[] { 23.0207, 113.7518 });
        CITY_COORDINATES.put("佛山", new double[] { 23.0218, 113.1219 });
        CITY_COORDINATES.put("珠海", new double[] { 22.2710, 113.5767 });
    }

    // 备用本地名人名言列表（API调用失败时使用）
    private static final String[][] FALLBACK_QUOTES = {
            { "业精于勤，荒于嬉；行成于思，毁于随。", "韩愈" },
            { "天行健，君子以自强不息。", "《周易》" },
            { "千里之行，始于足下。", "老子" },
            { "知之为知之，不知为不知，是知也。", "孔子" },
            { "学而不思则罔，思而不学则殆。", "孔子" },
            { "书山有路勤为径，学海无涯苦作舟。", "韩愈" },
            { "宝剑锋从磨砺出，梅花香自苦寒来。", "《警世贤文》" },
            { "不积跬步，无以至千里；不积小流，无以成江海。", "荀子" },
            { "三人行，必有我师焉。", "孔子" },
            { "路漫漫其修远兮，吾将上下而求索。", "屈原" },
            { "海纳百川，有容乃大；壁立千仞，无欲则刚。", "林则徐" },
            { "天下兴亡，匹夫有责。", "顾炎武" },
            { "生于忧患，死于安乐。", "孟子" },
            { "穷则独善其身，达则兼济天下。", "孟子" },
            { "纸上得来终觉浅，绝知此事要躬行。", "陆游" },
            { "博观而约取，厚积而薄发。", "苏轼" },
            { "读书破万卷，下笔如有神。", "杜甫" },
            { "敏而好学，不耻下问。", "孔子" },
            { "人生自古谁无死，留取丹心照汗青。", "文天祥" },
            { "先天下之忧而忧，后天下之乐而乐。", "范仲淹" },
            { "莫等闲，白了少年头，空悲切。", "岳飞" },
            { "静以修身，俭以养德。", "诸葛亮" },
            { "非淡泊无以明志，非宁静无以致远。", "诸葛亮" },
            { "吾生也有涯，而知也无涯。", "庄子" },
            { "己所不欲，勿施于人。", "孔子" },
            { "温故而知新，可以为师矣。", "孔子" },
            { "工欲善其事，必先利其器。", "孔子" },
            { "学无止境。", "荀子" },
            { "少壮不努力，老大徒伤悲。", "《长歌行》" },
            { "读万卷书，行万里路。", "刘彝" }
    };

    @GetMapping("/weather")
    @Operation(summary = "根据IP获取天气信息")
    public Result<Map<String, Object>> getWeather(HttpServletRequest request) {
        Map<String, Object> weather = new HashMap<>();

        // 1. 获取客户端IP
        String clientIp = getClientIp(request);
        log.info("客户端IP: {}", clientIp);

        // 2. 根据IP获取城市
        String city = getCityByIp(clientIp);
        log.info("定位城市: {}", city);
        weather.put("city", city);

        // 3. 获取城市经纬度
        double[] coords = getCityCoordinates(city);

        // 4. 调用天气API
        Map<String, Object> weatherData = fetchWeatherData(coords[0], coords[1]);
        weather.putAll(weatherData);

        return Result.success(weather);
    }

    @GetMapping("/stats")
    @Operation(summary = "获取首页统计数据")
    public Result<Map<String, Object>> getStats() {
        Long userId = SecurityUtils.getCurrentUserId();
        Map<String, Object> stats = new HashMap<>();

        // 已启用的用户总数（MyBatis Plus @TableLogic 会自动添加 deleted=0 条件）
        long activeUsers = userService.count(new LambdaQueryWrapper<User>().eq(User::getStatus, 1));
        stats.put("totalUsers", activeUsers);

        // 在线用户数（从Redis获取token数量）
        Set<String> onlineTokens = redisTemplate.keys("token:*");
        int onlineCount = onlineTokens != null ? onlineTokens.size() : 1;
        stats.put("onlineUsers", onlineCount);

        // 待办事项数量
        stats.put("pendingTodos", todoService.countPendingTodos(userId));

        // 未读消息数量
        stats.put("unreadMessages", messageService.getUnreadCount(userId));

        // 未读公告数量
        stats.put("unreadAnnouncements", announcementService.countUnreadAnnouncements(userId));

        return Result.success(stats);
    }

    @GetMapping("/greeting")
    @Operation(summary = "获取欢迎语信息")
    public Result<Map<String, Object>> getGreeting() {
        Map<String, Object> greeting = new HashMap<>();

        // 当前日期
        LocalDate today = LocalDate.now();
        greeting.put("date", today.format(DateTimeFormatter.ofPattern("yyyy年MM月dd日")));
        greeting.put("weekday", getWeekday(today));

        // 时间段问候语
        int hour = java.time.LocalTime.now().getHour();
        String timeGreeting;
        if (hour < 6) {
            timeGreeting = "夜深了";
        } else if (hour < 9) {
            timeGreeting = "早上好";
        } else if (hour < 12) {
            timeGreeting = "上午好";
        } else if (hour < 14) {
            timeGreeting = "中午好";
        } else if (hour < 18) {
            timeGreeting = "下午好";
        } else if (hour < 22) {
            timeGreeting = "晚上好";
        } else {
            timeGreeting = "夜深了";
        }
        greeting.put("timeGreeting", timeGreeting);

        // 节日提醒（简单示例，可扩展）
        String holiday = getHolidayReminder(today);
        greeting.put("holiday", holiday);

        // 名人名言（从一言API获取，每次刷新都不同）
        Map<String, String> quote = getRandomQuote();
        greeting.put("quote", quote.get("content"));
        greeting.put("quoteAuthor", quote.get("author"));

        return Result.success(greeting);
    }

    /**
     * 获取随机名言：优先今日诗词API，其次一言API，最后本地备用
     */
    private Map<String, String> getRandomQuote() {
        Map<String, String> result = new HashMap<>();
        HttpClient client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(3))
                .build();

        // 1. 优先尝试今日诗词API（快速稳定）
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(JINRISHICI_API))
                    .timeout(Duration.ofSeconds(3))
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                JsonNode json = objectMapper.readTree(response.body());
                String content = json.has("content") ? json.get("content").asText() : null;
                String origin = json.has("origin") ? json.get("origin").asText() : "";
                String author = json.has("author") ? json.get("author").asText() : "";

                if (content != null && !content.isEmpty()) {
                    result.put("content", content);
                    result.put("author", !author.isEmpty() ? author + "《" + origin + "》" : "《" + origin + "》");
                    log.debug("今日诗词API成功: {}", content);
                    return result;
                }
            }
        } catch (Exception e) {
            log.debug("今日诗词API调用失败: {}", e.getMessage());
        }

        // 2. 备用：尝试一言API
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(HITOKOTO_API))
                    .timeout(Duration.ofSeconds(5))
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                JsonNode json = objectMapper.readTree(response.body());
                String content = json.has("hitokoto") ? json.get("hitokoto").asText() : null;
                String from = json.has("from") ? json.get("from").asText() : "";
                String fromWho = json.has("from_who") && !json.get("from_who").isNull()
                        ? json.get("from_who").asText()
                        : "";

                if (content != null && !content.isEmpty()) {
                    result.put("content", content);
                    String authorStr = !fromWho.isEmpty() ? fromWho : (!from.isEmpty() ? "《" + from + "》" : "佚名");
                    result.put("author", authorStr);
                    log.debug("一言API成功: {}", content);
                    return result;
                }
            }
        } catch (Exception e) {
            log.debug("一言API调用失败: {}", e.getMessage());
        }

        // 3. 最终：使用本地备用名言
        int index = new Random().nextInt(FALLBACK_QUOTES.length);
        result.put("content", FALLBACK_QUOTES[index][0]);
        result.put("author", FALLBACK_QUOTES[index][1]);
        log.debug("使用本地备用名言");
        return result;
    }

    private String getWeekday(LocalDate date) {
        String[] weekdays = { "星期一", "星期二", "星期三", "星期四", "星期五", "星期六", "星期日" };
        return weekdays[date.getDayOfWeek().getValue() - 1];
    }

    private String getHolidayReminder(LocalDate date) {
        int month = date.getMonthValue();
        int day = date.getDayOfMonth();

        // 公历节日
        if (month == 1 && day == 1)
            return "🎉 元旦快乐！";
        if (month == 2 && day == 14)
            return "💕 情人节快乐！";
        if (month == 3 && day == 8)
            return "🌸 妇女节快乐！";
        if (month == 4 && day == 1)
            return "🤡 愚人节，小心被整哦！";
        if (month == 5 && day == 1)
            return "🎊 劳动节快乐！";
        if (month == 5 && day == 4)
            return "🌟 青年节快乐！";
        if (month == 6 && day == 1)
            return "🧒 儿童节快乐！";
        if (month == 7 && day == 1)
            return "🎂 建党节！";
        if (month == 8 && day == 1)
            return "⭐ 建军节！";
        if (month == 9 && day == 10)
            return "👨‍🏫 教师节快乐！";
        if (month == 10 && day == 1)
            return "🇨🇳 国庆节快乐！";
        if (month == 12 && day == 25)
            return "🎄 圣诞节快乐！";

        // 可扩展农历节日...

        return null;
    }

    /**
     * 获取客户端真实IP
     */
    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        // 多个代理时，取第一个IP
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip;
    }

    // 默认城市（本地开发或IP定位失败时使用）
    private static final String DEFAULT_CITY = "天津";

    /**
     * 根据IP获取城市名（使用太平洋IP库）
     */
    private String getCityByIp(String ip) {
        // 本地IP返回默认城市
        if (ip == null || ip.isEmpty() || "127.0.0.1".equals(ip) || "0:0:0:0:0:0:0:1".equals(ip)) {
            log.info("本地IP，使用默认城市: {}", DEFAULT_CITY);
            return DEFAULT_CITY;
        }

        try {
            HttpClient client = HttpClient.newBuilder()
                    .connectTimeout(Duration.ofSeconds(3))
                    .build();

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(IP_LOCATION_API + ip))
                    .timeout(Duration.ofSeconds(3))
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                String body = response.body().trim();
                // 太平洋返回的JSON可能有BOM或空白，需要清理
                if (body.startsWith("\ufeff")) {
                    body = body.substring(1);
                }
                // 提取city字段
                JsonNode json = objectMapper.readTree(body);
                String city = json.has("city") ? json.get("city").asText() : "";
                // 移除"市"后缀
                if (city.endsWith("市")) {
                    city = city.substring(0, city.length() - 1);
                }
                if (!city.isEmpty()) {
                    log.info("IP定位成功: {} -> {}", ip, city);
                    return city;
                }
            }
        } catch (Exception e) {
            log.warn("IP定位失败: {}", e.getMessage());
        }
        log.info("IP定位失败，使用默认城市: {}", DEFAULT_CITY);
        return DEFAULT_CITY;
    }

    /**
     * 获取城市经纬度
     */
    private double[] getCityCoordinates(String city) {
        // 先精确匹配
        if (CITY_COORDINATES.containsKey(city)) {
            return CITY_COORDINATES.get(city);
        }
        // 模糊匹配（处理"天津市"这种情况）
        for (Map.Entry<String, double[]> entry : CITY_COORDINATES.entrySet()) {
            if (city.contains(entry.getKey()) || entry.getKey().contains(city)) {
                return entry.getValue();
            }
        }
        // 默认返回天津
        return CITY_COORDINATES.get(DEFAULT_CITY);
    }

    /**
     * 调用Open-Meteo API获取天气数据
     */
    private Map<String, Object> fetchWeatherData(double lat, double lon) {
        Map<String, Object> result = new HashMap<>();

        try {
            String url = WEATHER_API + "&latitude=" + lat + "&longitude=" + lon;

            HttpClient client = HttpClient.newBuilder()
                    .connectTimeout(Duration.ofSeconds(5))
                    .build();

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .timeout(Duration.ofSeconds(5))
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                JsonNode json = objectMapper.readTree(response.body());
                JsonNode current = json.get("current");

                if (current != null) {
                    // 温度
                    double temp = current.has("temperature_2m") ? current.get("temperature_2m").asDouble() : 0;
                    result.put("temp", Math.round(temp));

                    // 湿度
                    int humidity = current.has("relative_humidity_2m") ? current.get("relative_humidity_2m").asInt()
                            : 0;
                    result.put("humidity", humidity);

                    // 天气代码转描述
                    int weatherCode = current.has("weather_code") ? current.get("weather_code").asInt() : 0;
                    result.put("desc", getWeatherDescription(weatherCode));
                    result.put("icon", getWeatherIcon(weatherCode));

                    return result;
                }
            }
        } catch (Exception e) {
            log.warn("获取天气失败: {}", e.getMessage());
        }

        // 失败时返回模拟数据
        result.put("temp", 20);
        result.put("humidity", 50);
        result.put("desc", "晴");
        result.put("icon", "sunny");
        return result;
    }

    /**
     * WMO天气代码转描述
     */
    private String getWeatherDescription(int code) {
        if (code == 0)
            return "晴";
        if (code == 1)
            return "晴间多云";
        if (code == 2)
            return "多云";
        if (code == 3)
            return "阴";
        if (code >= 45 && code <= 48)
            return "雾";
        if (code >= 51 && code <= 55)
            return "毛毛雨";
        if (code >= 56 && code <= 57)
            return "冻雨";
        if (code >= 61 && code <= 65)
            return "雨";
        if (code >= 66 && code <= 67)
            return "冻雨";
        if (code >= 71 && code <= 77)
            return "雪";
        if (code >= 80 && code <= 82)
            return "阵雨";
        if (code >= 85 && code <= 86)
            return "阵雪";
        if (code >= 95 && code <= 99)
            return "雷暴";
        return "未知";
    }

    /**
     * WMO天气代码转图标名
     */
    private String getWeatherIcon(int code) {
        if (code == 0)
            return "sunny";
        if (code >= 1 && code <= 2)
            return "partly-cloudy";
        if (code == 3)
            return "cloudy";
        if (code >= 45 && code <= 48)
            return "fog";
        if (code >= 51 && code <= 67)
            return "rainy";
        if (code >= 71 && code <= 77)
            return "snowy";
        if (code >= 80 && code <= 82)
            return "rainy";
        if (code >= 85 && code <= 86)
            return "snowy";
        if (code >= 95 && code <= 99)
            return "thunderstorm";
        return "sunny";
    }
}
