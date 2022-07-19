package com.linklip.linklipserver.controller.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class TestResponseDto {
    String name;
    int age;

    @Builder
    public TestResponseDto(String name, int age) {
        this.name = name;
        this.age = age;
    }
}
