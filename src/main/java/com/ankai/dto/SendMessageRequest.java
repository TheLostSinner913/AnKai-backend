package com.ankai.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 发送消息请求DTO
 *
 * @author AnKai
 * @since 2024-01-01
 */
@Data
@Schema(description = "发送消息请求")
public class SendMessageRequest {

    /**
     * 接收者ID
     */
    @NotNull(message = "接收者ID不能为空")
    @Schema(description = "接收者ID")
    private Long receiverId;

    /**
     * 消息内容
     */
    @NotBlank(message = "消息内容不能为空")
    @Schema(description = "消息内容")
    private String content;
}
