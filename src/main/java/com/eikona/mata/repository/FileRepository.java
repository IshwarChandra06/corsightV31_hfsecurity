package com.eikona.mata.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import com.eikona.mata.entity.FileSystemContent;


//@Repository
@RepositoryRestResource(path="files", collectionResourceRel="files")
public interface FileRepository extends JpaRepository<FileSystemContent, Long>{

}
