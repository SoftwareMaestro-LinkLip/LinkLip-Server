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
    private final S3Upload s3Upload;

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

        String term = request.getTerm();
        Long categoryId = request.getCategoryId();
        Long ownerId = owner.getId();

        Page<Content> page = null;

        if (categoryId != null && StringUtils.hasText(term)) {
            page =
                    contentRepository.findByCategoryAndTermAndOwner(
                            categoryId, term, pageable, ownerId);
        }

        if (categoryId != null && !StringUtils.hasText(term)) {
            page = contentRepository.findByCategoryAndOwner(categoryId, pageable, ownerId);
        }

        if (categoryId == null && StringUtils.hasText(term)) {
            page = contentRepository.findByTermAndOwner(term, pageable, ownerId);
        }

        if (categoryId == null && !StringUtils.hasText(term)) {
            page = contentRepository.findAllByOwner(pageable, owner);
        }

        return page.map(
                (c) -> {
                    // Link Content
                    if (c instanceof Link) {
                        return new LinkDto((Link) c);
                    }
                    // Note Content
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

        Content content = getContent(contentId, owner);

        String title = request.getTitle();
        Category category = getCategory(request.getCategoryId(), owner);
        ((Link) content).update(title, category);
    }

    @Transactional
    public void deleteContent(Long contentId, User owner) {

        Content content = getContent(contentId, owner);
        content.delete();
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
        String text = request.getText();
        ((Note) content).update(text, category);
    }

    @Transactional
    public void saveImageContent(SaveImageRequest request, MultipartFile imageFile, User owner)
            throws IOException {

        Category category = getCategory(request.getCategoryId(), owner);

        String imageUrl = s3Upload.upload(imageFile);
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
}
