package com.linklip.linklipserver.controller.dto;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TestResponseDtoTest {

    @Test
    void testCI() {
        //Given
        TestResponseDto testResponseDto = TestResponseDto.builder()
                .name("test")
                .build();
        //When

        //Then
        Assertions.assertThat(testResponseDto.getName()).isEqualTo("test1");
    }
}