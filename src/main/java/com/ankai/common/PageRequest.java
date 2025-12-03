package com.ankai.common;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;



/**
 * 分页查询请求类
 *
 * @author AnKai
 * @since 2024-01-01
 */
@Data
public class PageRequest {

    /**
     * 当前页码
     */
    @NotNull(message = "页码不能为空")
    @Min(value = 1, message = "页码最小为1")
    private Long current = 1L;

    /**
     * 每页大小
     */
    @NotNull(message = "每页大小不能为空")
    @Min(value = 1, message = "每页大小最小为1")
    @Max(value = 100, message = "每页大小最大为100")
    private Long size = 10L;

    /**
     * 排序字段
     */
    private String sortField;

    /**
     * 排序方式 (asc/desc)
     */
    private String sortOrder = "desc";
}
