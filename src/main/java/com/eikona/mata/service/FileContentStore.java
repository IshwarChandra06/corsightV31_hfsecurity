package com.eikona.mata.service;

import org.springframework.content.commons.repository.ContentStore;
import org.springframework.stereotype.Component;

import com.eikona.mata.entity.FileSystemContent;


@Component
public interface FileContentStore extends ContentStore<FileSystemContent, String> {
}
