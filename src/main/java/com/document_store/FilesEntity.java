package com.document_store;

import static javax.persistence.GenerationType.IDENTITY;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "files")
public class FilesEntity implements java.io.Serializable {

	private static final long serialVersionUID = 7644250898859971557L;
	
	private Integer id;
	
	private String filename;
	
	private String filepath;

	public FilesEntity() {
	}

	public FilesEntity(String filename, String filepath) {
		this.filename = filename;
		this.filepath = filepath;
	}

	@Id
	@GeneratedValue(strategy = IDENTITY)

	@Column(name = "id", unique = true, nullable = false)
	public Integer getFileId() {
		return this.id;
	}

	public void setFileId(Integer id) {
		this.id = id;
	}

	@Column(name = "filename", length = 250)
	public String getFileName() {
		return this.filename;
	}

	public void setFileName(String filename) {
		this.filename = filename;
	}

	@Column(name = "filepath", length = 250)
	public String getFilePath() {
		return this.filepath;
	}

	public void setFilePath(String filepath) {
		this.filepath = filepath;
	}

}