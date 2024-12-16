package fr.uge.structsure.dto;

public record ApiResponseWrapper<T>(T data, int httpCode) {
}

