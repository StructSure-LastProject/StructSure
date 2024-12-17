package fr.uge.structsure.dto.structure;

public record ApiResponseWrapper<T>(T data, int httpCode) {
}