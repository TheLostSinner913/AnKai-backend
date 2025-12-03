package com.ankai;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

/**
 * 名人名言API测试类
 * 测试各种免费API是否可用（纯Java，无外部依赖）
 */
public class QuoteApiTest {

    public static void main(String[] args) {
        System.out.println("========== 名人名言API测试 ==========\n");

        // 测试1: Hitokoto 一言API
        testApi("Hitokoto 一言API",
                "https://v1.hitokoto.cn/?c=d&c=h&c=i&c=k&encode=json");

        // 测试2: 金山词霸每日一句
        testApi("金山词霸 每日一句",
                "https://open.iciba.com/dsapi/");

        // 测试3: 今日诗词API
        testApi("今日诗词 API",
                "https://v1.jinrishici.com/all.json");

        // 测试4: 随机名言API
        testApi("随机语录 API",
                "https://api.quotable.io/random");

        // 测试5: 韩小韩API
        testApi("韩小韩 一言API",
                "https://api.vvhan.com/api/ian/rand?type=json");

        System.out.println("\n========== 测试完成 ==========");
    }

    private static void testApi(String name, String url) {
        System.out.println("【测试】" + name);
        System.out.println("URL: " + url);

        try {
            HttpClient client = HttpClient.newBuilder()
                    .connectTimeout(Duration.ofSeconds(8))
                    .build();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .timeout(Duration.ofSeconds(8))
                    .header("User-Agent", "Mozilla/5.0")
                    .GET()
                    .build();

            long startTime = System.currentTimeMillis();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            long duration = System.currentTimeMillis() - startTime;

            System.out.println("状态码: " + response.statusCode() + " (耗时: " + duration + "ms)");

            if (response.statusCode() == 200) {
                String body = response.body();
                // 截取前500字符显示
                if (body.length() > 500) {
                    body = body.substring(0, 500) + "...";
                }
                System.out.println("✅ 成功!");
                System.out.println("响应: " + body);
            } else {
                System.out.println("❌ 失败: HTTP " + response.statusCode());
            }
        } catch (Exception e) {
            System.out.println("❌ 异常: " + e.getClass().getSimpleName() + " - " + e.getMessage());
        }
        System.out.println();
    }
}
