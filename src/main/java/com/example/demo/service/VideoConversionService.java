package com.example.demo.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import com.example.demo.config.AwsConfigProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

@Service
public class VideoConversionService {

    private static final Logger logger = LoggerFactory.getLogger(VideoConversionService.class);

    private final AmazonS3 s3Client;
    private final String bucketName;

    @Autowired
    public VideoConversionService(AmazonS3 s3Client, AwsConfigProperties awsConfig) {
        this.s3Client = s3Client;
        this.bucketName = awsConfig.getS3BucketName();
    }

    public String uploadToS3(byte[] file, String folder) {
        String fileName = folder + "/" + UUID.randomUUID().toString() + ".mp4";
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(file.length);

        try {
            s3Client.putObject(new PutObjectRequest(bucketName, fileName, new ByteArrayInputStream(file), metadata));
            logger.info("Arquivo enviado para S3: {}", fileName);
        } catch (Exception e) {
            logger.error("Erro ao fazer upload para S3", e);
            throw new RuntimeException("Erro ao enviar para S3", e);
        }

        return fileName;
    }

    public File downloadVideoFromS3(String videoKey) throws IOException {
        S3Object s3Object = s3Client.getObject(new GetObjectRequest(bucketName, videoKey));
        S3ObjectInputStream inputStream = s3Object.getObjectContent();

        Path tempFilePath = Files.createTempFile("downloaded_", ".mp4");
        File tempFile = tempFilePath.toFile();

        try (FileOutputStream fos = new FileOutputStream(tempFile)) {
            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                fos.write(buffer, 0, length);
            }
        }

        logger.info("Vídeo baixado do S3: {}", tempFile.getAbsolutePath());
        return tempFile;
    }

    public byte[] convertVideo(File videoFile, String outputFormat) throws IOException, InterruptedException {
        String outputFilePath = Files.createTempFile("converted_", "." + outputFormat).toString();

        ProcessBuilder processBuilder = new ProcessBuilder(
                "ffmpeg", 
                "-i", videoFile.getAbsolutePath(),
                "-c:v", "libx264",
                "-preset", "fast",
                "-crf", "22",
                outputFilePath
        );

        Process process = processBuilder.start();
        int exitCode = process.waitFor();

        if (exitCode != 0) {
            throw new RuntimeException("Erro ao converter vídeo com FFmpeg");
        }

        File convertedFile = new File(outputFilePath);
        byte[] fileBytes = Files.readAllBytes(convertedFile.toPath());

        Files.delete(convertedFile.toPath());

        logger.info("Vídeo convertido e salvo temporariamente: {}", outputFilePath);
        return fileBytes;
    }

    public String uploadConvertedVideoToS3(byte[] convertedVideo) {
        String convertedFileName = "destino/" + UUID.randomUUID().toString() + ".mp4";
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(convertedVideo.length);

        try {
            s3Client.putObject(new PutObjectRequest(bucketName, convertedFileName, new ByteArrayInputStream(convertedVideo), metadata));
            logger.info("Vídeo convertido enviado para S3: {}", convertedFileName);
        } catch (Exception e) {
            logger.error("Erro ao fazer upload do vídeo convertido para S3", e);
            throw new RuntimeException("Erro ao enviar vídeo convertido para S3", e);
        }

        return convertedFileName;
    }

    public void deleteFromS3(String videoKey) {
        try {
            s3Client.deleteObject(new DeleteObjectRequest(bucketName, videoKey));
            logger.info("Arquivo deletado do S3: {}", videoKey);
        } catch (Exception e) {
            logger.error("Erro ao deletar arquivo do S3", e);
            throw new RuntimeException("Erro ao deletar arquivo do S3", e);
        }
    }

    public void processAndDeleteOriginalVideo(String videoKey) throws IOException, InterruptedException {
        File originalFile = downloadVideoFromS3(videoKey);

        byte[] convertedVideo = convertVideo(originalFile, "mp4");

        String convertedVideoKey = uploadConvertedVideoToS3(convertedVideo);

        deleteFromS3(videoKey);

        logger.info("Processamento concluído. Vídeo original deletado e convertido enviado para a pasta 'destino'.");
    }
}
