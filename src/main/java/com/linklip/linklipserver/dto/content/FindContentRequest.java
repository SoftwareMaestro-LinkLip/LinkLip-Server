package com.linklip.linklipserver.dto.content;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class FindContentRequest {

    private String categoryTerm;

    private String allTerm;
}
