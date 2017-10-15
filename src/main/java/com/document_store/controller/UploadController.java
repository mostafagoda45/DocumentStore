package com.document_store.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.document_store.FilesEntity;
import com.document_store.FtpConfig;

import com.document_store.FtpConfig.UploadGateway;
import com.document_store.jpa.FilesEntityService;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

@RestController
public class UploadController {
	
	@Autowired
	FilesEntityService filesEntityService;
	
	@Autowired
	FtpConfig dlGateway;
	
	@Autowired
    private UploadGateway ulGateway;
	
    private static String UPLOADED_FOLDER = "public_html/uploads/";

    @GetMapping("/allFiles")
    public List<FilesEntity> getFiles() {
        return filesEntityService.getall();
    }
    
    @PostMapping("/upload") 
    public void FileUpload(@RequestParam("file") MultipartFile file) {

        try {

        	byte[] bytes = file.getBytes();

            ulGateway.upload(bytes,file.getOriginalFilename());
            
            FilesEntity fileentity = new FilesEntity();
            
            fileentity.setFileName(file.getOriginalFilename());
            fileentity.setFilePath(UPLOADED_FOLDER);
            
            filesEntityService.addOne(fileentity);

        } catch (IOException e) {
//        	response.addHeader("error", e.getMessage() );
        }
    }
    
    
    @PostMapping("/download") 
    public void FileDownload(@RequestParam("fileToDownload") int fileid, HttpServletResponse response) throws Exception {

        try {
            FilesEntity fileentity =  filesEntityService.getOne(fileid);
            String fileName = fileentity.getFileName();
            String filePath = fileentity.getFilePath();
            Map<InputStream,String> file = dlGateway.download(fileName, filePath);
            String contentType = null;
            InputStream is = new InputStream() {@Override public int read() throws IOException {return 0;}};
            for (Map.Entry<InputStream, String> entry : file.entrySet())
            {
                System.out.println("File Type: "+entry.getValue());
                is = entry.getKey();
                contentType = entry.getValue();
            }
            //if uncommented -> will get download link but only text files working.
            if(contentType != null)	{ response.setContentType(contentType); }
            else response.setContentType("application/octet-stream");
            response.addHeader("Content-Disposition", "attachment; filename=" + fileName);
            
            FileCopyUtils.copy(is, response.getOutputStream());
            
        } catch (IOException e) {
        	response.addHeader("error", e.getMessage() );
        }
    }
    @PostMapping("/delete") 
    public void FileDelete(@RequestParam("fileToDelete") int fileid, HttpServletResponse response) throws Exception {
    	
    	 try {
    		 
    		 FilesEntity fileentity =  filesEntityService.getOne(fileid);
    		 String fileName = fileentity.getFileName();
             String filePath = fileentity.getFilePath();
             
             dlGateway.delete(fileName, filePath);
             filesEntityService.deleteOne(fileentity.getFileId());

         } catch (IOException e) {
         	response.addHeader("error", e.getMessage() );
         }
    }
}