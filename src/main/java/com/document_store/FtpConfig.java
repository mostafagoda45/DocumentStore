package com.document_store;

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.net.ftp.FTPFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.expression.common.LiteralExpression;
import org.springframework.integration.annotation.Gateway;
import org.springframework.integration.annotation.Header;

import org.springframework.integration.annotation.MessagingGateway;
import org.springframework.integration.annotation.ServiceActivator;

import org.springframework.integration.file.FileNameGenerator;
import org.springframework.integration.file.remote.session.CachingSessionFactory;
import org.springframework.integration.file.remote.session.SessionFactory;
import org.springframework.integration.ftp.outbound.FtpMessageHandler;
import org.springframework.integration.ftp.session.DefaultFtpSessionFactory;
import org.springframework.integration.ftp.session.FtpRemoteFileTemplate;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHandler;


@Configuration
public class FtpConfig {
	
	 	@Value("ftp.hamys.me")
	    private String ftpHost;
	 
	    @Value("21")
	    private int ftpPort;
	 
	    @Value("hamys.me")
	    private String ftpUser;
	 
	    @Value("HamyS(FTP)")
	    private String ftpPasword;
	 
	    @Value("public_html/uploads/")
	    private String ftpRemoteDirectoryUpload;
	 
	    @Value("public_html/downloads/")
	    private String ftpRemoteDirectoryDownload;
	 
	    @Value("C:\\Users\\ahmad\\Downloads\\ftp\\downloaded")
	    private String ftpLocalDirectoryDownload;
	    
	    @Value("${ftp.remote.directory.download.filter:*.*}")
	    private String ftpRemoteDirectoryDownloadFilter;
	    
	    @Autowired
	    private FtpRemoteFileTemplate template;
	    
	    @Bean
	    public SessionFactory<FTPFile> ftpSessionFactory() {
	        DefaultFtpSessionFactory factory = new DefaultFtpSessionFactory();
	        factory.setHost(ftpHost);
	        factory.setPort(ftpPort);
	        factory.setUsername(ftpUser);
	        factory.setPassword(ftpPasword);
	        factory.setClientMode(2);
	        
	        return new CachingSessionFactory<FTPFile>(factory);
	    }
	    
	    /* UPLOAD */
	    @Bean
	    @ServiceActivator(inputChannel = "ulFtpChannel")
	    public MessageHandler handler() {
	        FtpMessageHandler handler = new FtpMessageHandler(ftpSessionFactory());
	        handler.setRemoteDirectoryExpression(new LiteralExpression(ftpRemoteDirectoryUpload));
	        handler.setFileNameGenerator(new FileNameGenerator() {
	            @Override
	            public String generateFileName(Message<?> message) {
	                    return message.getHeaders().get("file_name").toString();
	            }
	        });
	        return handler;
	    }
	    
	    
	    @SuppressWarnings("deprecation")
	    @MessagingGateway
	    public interface UploadGateway {
	 
			@Gateway(requestChannel = "ulFtpChannel")
	        void upload(byte[] file,@Header("file_name") String filename);
	 
	    }
	    /* UPLOAD */
	    
	    /* DOWNLOAD */
	    @Bean
	    public FtpRemoteFileTemplate template() {
	        return new FtpRemoteFileTemplate(ftpSessionFactory());
	    }

	    public Map<InputStream,String> download(String fileName, String filePath) throws Exception {
	        //template.get(filePath+fileName, inputStream -> FileCopyUtils.copy(inputStream, new FileOutputStream(new File(ftpLocalDirectoryDownload+File.separator+fileName))));
	        String ftpUrl = "ftp://%s:%s@%s/%s;type=i";
	        ftpUrl = String.format(ftpUrl, ftpUser, ftpPasword, ftpHost, filePath+fileName);
	        URL url = new URL(ftpUrl);
            URLConnection conn = url.openConnection();
            InputStream inputStream = conn.getInputStream();
            String contentType = URLConnection.guessContentTypeFromStream(inputStream);
            
            Map<InputStream,String> map=new HashMap<InputStream,String>();  
            
            map.put(inputStream,contentType);  
            return map;
	    }
	    /* DOWNLOAD */
	    
	    /* DELETE */
	    public void delete(String fileName, String filePath) throws Exception {
	        template.remove(filePath+fileName);
	    }
	    /* DELETE */
	}