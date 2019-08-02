package com.example.fileuploaddemo.mvc;

import com.example.fileuploaddemo.entity.UploadFile;
import com.example.fileuploaddemo.exception.BizException;
import com.example.fileuploaddemo.helper.UploadHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
public class UploadController {

    @Value("${file.upload.basePath}")
    private String basePath;

    /**
     * 파일을 업로드한다. (Form)
     * @param files 업로드한 파일
     * @param param 업로드한 파일 정보
     * @return 서버에 업로드된 파일 정보
     * @throws Exception
     */
    @PostMapping("/api/co/file/upload")
    public ResponseEntity<List<UploadFile>> upload(@RequestParam("file") MultipartFile[] files, @RequestParam Map<String, String> param) throws BizException {
        if(log.isDebugEnabled()) {
            log.debug("{} -> upload", this.getClass().getName());
            log.debug(param.toString());
        }

        if(StringUtils.isEmpty(basePath)) {
            throw new BizException("basePath 설정이 잘못되었습니다.");
        }
        if(files == null) {
            throw new BizException("파일을 찾을 수 없습니다.");
        }
        if(files.length == 0) {
            throw new BizException("파일을 찾을 수 없습니다.");
        }
        if(param == null) {
            throw new BizException("잘못된 설정정보입니다.");
        }

        //subPath 파라미터
        String subPath = param.get("subPath");
        if(StringUtils.isEmpty(subPath)) {
            throw new BizException("subPath 설정정보를 찾을 수 없습니다.");
        }

        //keepOriginalFilename 파라미터
        boolean keepOriginalFilename = false;
        if(param.containsKey("keepOriginalFilename")) {
            keepOriginalFilename = Boolean.parseBoolean(param.get("keepOriginalFilename"));
        }

        //isImage 파라미터
        boolean isImage = false;
        if(param.containsKey("isImage")) {
            isImage = Boolean.parseBoolean(param.get("isImage"));
        }

        //파일을 저장
        List<UploadFile> uploadFiles = new ArrayList<>();
        for(MultipartFile file : files) {
            UploadFile uploadFile = new UploadFile(file);
            if(uploadFile == null) {
                throw new BizException("잘못된 파일입니다. index:[" + uploadFiles.size() + 1 + "]");
            }

            //객체 정보 설정
            uploadFile.setSubPath(subPath);
            uploadFile.setServerBasePath(basePath);
            uploadFile.setKeepOriginalFilename(keepOriginalFilename);
            log.debug(uploadFile.toString());

            //기본 확장자 체크
            if(UploadHelper.isDenyExtension(uploadFile)) {
                throw new BizException("허용되지 않는 확장자입니다. [" + uploadFile.getExtension() + "]");
            }

            //이미지인 경우 파일 확장자 체크
            if(isImage && !UploadHelper.isImageExtension(uploadFile)) {
                throw new BizException("이미지 형식의 파일 확장자가 아닙니다.");
            }

            //파일저장
            try {
                UploadHelper.save(file, uploadFile);
                uploadFiles.add(uploadFile);

            } catch(IOException e) {
                throw new BizException("파일 업로드시 오류가 발생하였습니다.");
            }
        }
        return new ResponseEntity<>(uploadFiles, HttpStatus.OK);
    }

    /**
     * 서버에 업로드되어 있는 파일을 다운로드한다.
     * @param uploadFile 파일 정보
     * @return 파일
     * @throws Exception
     */
    @GetMapping("/api/co/file/download")
    public @ResponseBody HttpEntity<byte[]> downlaod(@RequestBody UploadFile uploadFile) throws BizException {
        if(log.isDebugEnabled()) {
            log.debug("{} -> download", this.getClass().getName());
            log.debug(uploadFile.toString());
        }

        if(uploadFile == null) {
            throw new BizException("잘못된 입력정보입니다.");
        }
        if(StringUtils.isEmpty(uploadFile.getServerPath())) {
            throw new BizException("serverPath 정보가 입력되지 않았습니다.");
        }
        if(StringUtils.isEmpty(uploadFile.getSubPath())) {
            throw new BizException("subPath 정보가 입력되지 않았습니다.");
        }
        if(StringUtils.isEmpty(uploadFile.getServerFilename())) {
            throw new BizException("serverFilename 정보가 입력되지 않았습니다.");
        }
        if(StringUtils.isEmpty(uploadFile.getFilename())) {
            throw new BizException("filename 정보가 입력되지 않았습니다.");
        }

        File file = new File(uploadFile.getServerPath(), uploadFile.getServerFilename());
        if(!file.exists()) {
            throw new BizException("파일을 찾을 수 없습니다.");
        }

        try {
            byte[] bytes = FileCopyUtils.copyToByteArray(file);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(UploadHelper.getMimeType(uploadFile));
            headers.set("Content-Disposition", "attachment; filename=" + URLEncoder.encode(uploadFile.getFilename(), "UTF-8") + ";");
            headers.set("Content-Transfer-Encoding", "binary");
            headers.setContentLength(bytes.length);
            return new HttpEntity<>(bytes, headers);

        } catch(IOException e) {
            throw new BizException("파일 다운로드시 오류가 발생하였습니다.");
        }
    }

    /**
     * 파일을 업로드한다. (Base64)
     * @param files 업로드한 파일 정보
     * @return 서버에 업로드된 파일 정보
     * @throws Exception
     */
    @PostMapping("/api/co/file/upload2")
    public ResponseEntity<List<UploadFile>> upload2(@RequestBody List<UploadFile> files) throws BizException {
        if(log.isDebugEnabled()) {
            log.debug("{} -> upload2", this.getClass().getName());
        }

        if(StringUtils.isEmpty(basePath)) {
            throw new BizException("basePath 설정이 잘못되었습니다.");
        }
        if(files == null) {
            throw new BizException("파일을 찾을 수 없습니다.");
        }
        if(files.size() == 0) {
            throw new BizException("파일을 찾을 수 없습니다.");
        }

        //파일을 저장
        int index = 1;
        for(UploadFile uploadFile : files) {
            if(uploadFile == null) {
                throw new BizException("잘못된 파일입니다. index:[" + index + "]");
            }
            log.debug(uploadFile.toString());

            //filename 파라미터 체크
            if(StringUtils.isEmpty(uploadFile.getFilename())) {
                throw new BizException("filename 설정정보를 찾을 수 없습니다. index:[" + index + "]");
            }

            //base64String 파라미터 체크
            if(StringUtils.isEmpty(uploadFile.getBase64String())) {
                throw new BizException("파일을 찾을 수 없습니다. index:[" + index + "]");
            }

            //subPath 파라미터 체크
            if(StringUtils.isEmpty(uploadFile.getSubPath())) {
                throw new BizException("subPath 설정정보를 찾을 수 없습니다. index:[" + index + "]");
            }

            //기본 확장자 체크
            if(UploadHelper.isDenyExtension(uploadFile)) {
                throw new BizException("허용되지 않는 확장자입니다. [" + uploadFile.getExtension() + "]");
            }

            try {
                uploadFile.init();
                UploadHelper.save(uploadFile);

            } catch(IOException e) {
                throw new BizException("파일 업로드시 오류가 발생하였습니다. index:[" + index + "]");
            }
            ++index;
        }
        return new ResponseEntity<>(files, HttpStatus.OK);
    }
}
