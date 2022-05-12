package com.qianmi.b2b.scheduling.core.supports.json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * <p>Date: 2022-03-25 10:31.</p>
 *
 * @author Administrator
 * @version 0.1.0
 */
@UtilityClass
@SuppressWarnings("unused")
public final class JsonUtils {

    public static <T> String toJson(@Nullable T object) {
        if (Objects.isNull(object)) {
            return null;
        }
        try {
            return get().writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw wrapException(e);
        }
    }

    public static <T> T parseObject(@Nullable String text, Class<T> clazz) {
        if (Objects.isNull(text)) {
            return null;
        }
        try {
            return get().readValue(text, clazz);
        } catch (IOException e) {
            throw wrapException(e);
        }
    }

    public static <T> T parseObject(String text, TypeReference<T> typeReference) {
        if (Objects.isNull(text)) {
            return null;
        }
        try {
            return get().readValue(text, typeReference);
        } catch (Exception e) {
            throw wrapException(e);
        }
    }

    public static <T> T parseObject(String text, JavaType type) {
        if (Objects.isNull(text)) {
            return null;
        }
        try {
            return get().readValue(text, type);
        } catch (Exception e) {
            throw wrapException(e);
        }
    }

    @NotNull
    public static <T> List<T> parseArray(@Nullable String text, @NotNull Class<T> clazz) {
        if (StringUtils.isBlank(text)) {
            return Collections.emptyList();
        }
        final JavaType type = get().getTypeFactory().constructParametricType(List.class, clazz);
        try {
            return get().readValue(text, type);
        } catch (IOException e) {
            throw wrapException(e);
        }
    }

    private static JsonException wrapException(final Exception cause) {
        return new JsonException("Json parse exception", cause);
    }

    private static ObjectMapper get() {
        return ObjectMapperManager.get();
    }
}
