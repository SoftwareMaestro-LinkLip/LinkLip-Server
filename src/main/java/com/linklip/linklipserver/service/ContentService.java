package com.linklip.linklipserver.service;

import static com.linklip.linklipserver.constant.ErrorResponse.*;

import com.linklip.linklipserver.domain.*;
import com.linklip.linklipserver.domain.content.Content;
import com.linklip.linklipserver.domain.content.Image;
import com.linklip.linklipserver.domain.content.Link;
import com.linklip.linklipserver.domain.content.Note;
import com.linklip.linklipserver.dto.content.*;
import com.linklip.linklipserver.dto.content.note.UpdateNoteRequest;
import com.linklip.linklipserver.exception.InvalidIdException;
import com.linklip.linklipserver.repository.CategoryRepository;
import com.linklip.linklipserver.repository.ContentRepository;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ContentService {

    private final CategoryRepository categoryRepository;
    private final ContentRepository contentRepository;
    private final S3Service s3Service;

    @Transactional
    public void saveLinkContent(SaveLinkRequest request, User owner) {

        Category category = getCategory(request.getCategoryId(), owner);

        Content content =
                Link.builder()
                        .linkUrl(request.getUrl())
                        .linkImg(request.getLinkImg())
                        .title(request.getTitle())
                        .text(request.getText())
                        .category(category)
                        .owner(owner)
                        .build();
        contentRepository.save(content);
    }

    public Page<ContentDto> findContentList(
            FindContentRequest request, Pageable pageable, User owner) {

        Page<Content> page =
                getContents(
                        pageable, owner, request.getCategoryId(), request.getTerm(), owner.getId());

        return page.map(
                (Content c) -> {
                    if (c instanceof Link) {
                        return new LinkDto((Link) c);
                    }

                    if (c instanceof Note) {
                        return new NoteDto((Note) c);
                    }

                    throw new InvalidIdException(INVALID_CONTENT_TYPE.getMessage());
                });
    }

    public ContentDto findContent(Long contentId, User owner) {

        Content content = getContent(contentId, owner);

        if (content instanceof Link) {
            return new LinkDto((Link) content);
        }

        if (content instanceof Note) {
            return new NoteDto((Note) content);
        }

        throw new InvalidIdException(INVALID_CONTENT_TYPE.getMessage());
    }

    @Transactional
    public void updateLinkContent(Long contentId, UpdateLinkRequest request, User owner) {

        Category category = getCategory(request.getCategoryId(), owner);
        Content content = getContent(contentId, owner);
        ((Link) content).update(request.getTitle(), category);
    }

    @Transactional
    public void deleteContent(Long contentId, User owner) {

        Content content = getContent(contentId, owner);
        content.delete();
        if (content instanceof Image) {
            s3Service.delete(((Image) content).getImageUrl());
        }
    }

    @Transactional
    public void saveNoteContent(SaveNoteRequest request, User owner) {

        Category category = getCategory(request.getCategoryId(), owner);
        Content content =
                Note.builder().text(request.getText()).category(category).owner(owner).build();
        contentRepository.save(content);
    }

    @Transactional
    public void updateNoteContent(Long contentId, UpdateNoteRequest request, User owner) {

        Category category = getCategory(request.getCategoryId(), owner);
        Content content = getContent(contentId, owner);
        ((Note) content).update(request.getText(), category);
    }

    @Transactional
    public void saveImageContent(SaveImageRequest request, MultipartFile imageFile, User owner)
            throws IOException {

        Category category = getCategory(request.getCategoryId(), owner);

        String imageUrl = s3Service.upload(imageFile);
        Content content =
                Image.builder().imageUrl(imageUrl).category(category).owner(owner).build();
        contentRepository.save(content);
    }

    private Category getCategory(Long categoryId, User owner) {
        return categoryId == null
                ? null
                : categoryRepository
                        .findByIdAndOwner(categoryId, owner)
                        .orElseThrow(
                                () -> new InvalidIdException(NOT_EXSIT_CATEGORY_ID.getMessage()));
    }

    private Content getContent(Long contentId, User owner) {
        return contentRepository
                .findByIdAndOwner(contentId, owner)
                .orElseThrow(() -> new InvalidIdException(NOT_EXSIT_CONTENT_ID.getMessage()));
    }

    private Page<Content> getContents(
            Pageable pageable, User owner, Long categoryId, String term, Long ownerId) {

        if (categoryId != null && StringUtils.hasText(term)) {
            return contentRepository.findByCategoryAndTermAndOwner(
                    categoryId, term, pageable, ownerId);
        }

        if (categoryId != null && !StringUtils.hasText(term)) {
            return contentRepository.findByCategoryAndOwner(categoryId, pageable, ownerId);
        }

        if (categoryId == null && StringUtils.hasText(term)) {
            return contentRepository.findByTermAndOwner(term, pageable, ownerId);
        }

        if (categoryId == null && !StringUtils.hasText(term)) {
            return contentRepository.findAllByOwner(pageable, owner);
        }

        throw new InvalidIdException(INVALID_CONTENT_TYPE.getMessage());
    }
}
