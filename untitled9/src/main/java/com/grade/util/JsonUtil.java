package com.grade.util;

import java.util.List;

/**
 * 简单的 JSON 响应构建工具（不依赖第三方库）
 */
public class JsonUtil {

    /**
     * 构建成功响应（带数据）
     * @param dataJson 已经是 JSON 格式的字符串（如数组或对象）
     * @return 完整的 JSON 响应
     */
    public static String success(String dataJson) {
        return "{\"success\": true, \"data\": " + dataJson + "}";
    }

    /**
     * 构建成功响应（不带数据）
     */
    public static String success() {
        return "{\"success\": true}";
    }

    /**
     * 构建错误响应
     * @param message 错误信息
     */
    public static String error(String message) {
        return "{\"success\": false, \"message\": \"" + escapeJson(message) + "\"}";
    }

    /**
     * 将对象列表转为 JSON 数组（简化版，只支持基本属性）
     * 注意：这里只是示例，实际项目中应使用 Gson/Jackson
     */
    public static String toJsonArray(List<?> list) {
        // 由于本项目中我们已经在 Servlet 中手动拼接 JSON，此方法仅作占位
        return "[]";
    }

    /**
     * 转义 JSON 特殊字符
     */
    public static String escapeJson(String str) {
        if (str == null) return "";
        return str.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }
}