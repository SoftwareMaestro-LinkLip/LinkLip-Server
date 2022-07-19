package com.linklip.linklipserver.controller;

import com.linklip.linklipserver.service.ContentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class ContentController {

    private final ContentService contentService;


}
