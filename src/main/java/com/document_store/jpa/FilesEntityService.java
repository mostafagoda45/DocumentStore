package com.document_store.jpa;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.document_store.FilesEntity;

@Service
public class FilesEntityService {

	@Autowired
     FilesEntityrepo repo;

    public List<FilesEntity> getall() {
        return StreamSupport.stream(repo.findAll().spliterator(), false).collect(Collectors.toList());
    }
    
    public FilesEntity getOne(int id){
    	FilesEntity file=new FilesEntity();
    	file=repo.findOne(id);
        return file;
    }
    
    public FilesEntity addOne(FilesEntity file){
    	return repo.save(file);
    }
    
    public void deleteOne(int id){
    	repo.delete(id);
    }
}