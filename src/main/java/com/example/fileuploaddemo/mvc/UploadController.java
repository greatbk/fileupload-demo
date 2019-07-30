package com.example.fileuploaddemo.mvc;

import com.example.fileuploaddemo.entity.UploadFile;
import com.example.fileuploaddemo.exception.BizException;
import com.example.fileuploaddemo.helper.UploadHelper;
import lombok.extern.slf4j.Slf4j;
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

    /**
     * 파일을 업로드한다.
     * @param files 업로드한 파일
     * @param param 업로드한 파일 정보
     * @return 서버에 업로드된 파일 정보
     * @throws Exception
     */
    @PostMapping("/api/co/file/upload")
    public ResponseEntity<List<UploadFile>> upload(@RequestParam("file") MultipartFile[] files, @RequestParam Map<String, String> param) throws Exception {
        if(log.isDebugEnabled()) {
            log.debug("{} -> upload", this.getClass().getName());
            log.debug(param.toString());
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

        //basePath 파라미터
        String basePath = param.get("basePath");
        if(StringUtils.isEmpty(basePath)) {
            throw new BizException("basePath 설정정보를 찾을 수 없습니다.");
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
            if(UploadHelper.isDenyFileType(uploadFile)) {
                throw new BizException("허용되지 않는 확장자입니다. [" + uploadFile.getExt() + "]");
            }

            //이미지인 경우 파일 확장자 체크
            if(isImage && !UploadHelper.isFileTypeImage(uploadFile)) {
                throw new BizException("이미지 형식의 파일 확장자가 아닙니다.");
            }

            try {
                save(file, uploadFile);
                uploadFiles.add(uploadFile);
            } catch(Exception e) {
                e.printStackTrace();
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
    public @ResponseBody HttpEntity<byte[]> downlaod(@RequestBody UploadFile uploadFile) throws Exception {
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
            headers.setContentType(getMimeType(uploadFile));
            headers.set("Content-Disposition", "attachment; filename=" + URLEncoder.encode(uploadFile.getFilename(), "UTF-8") + ";");
            headers.set("Content-Transfer-Encoding", "binary");
            headers.setContentLength(bytes.length);
            return new HttpEntity<>(bytes, headers);
        } catch(Exception e) {
            e.printStackTrace();
            throw new BizException("파일 다운로드시 오류가 발생하였습니다.");
        }
    }

    /**
     * 파일을 저장한다.
     * @param file 업로드한 파일
     * @param uploadFile 업로드한 파일 정보
     * @throws IOException
     */
    private void save(MultipartFile file, UploadFile uploadFile) throws IOException {
        File serverPath = new File(uploadFile.getServerPath());
        if(serverPath != null && !serverPath.exists()) {
            serverPath.mkdirs();
        }

        File filePath = new File(uploadFile.getServerPath(), uploadFile.getServerFilename());
        file.transferTo(filePath);
    }

    /**
     * 확장자에 대한 마임타입을 반환한다.
     * @param uploadFile 업로드한 파일 정보
     * @return 마임타입
     */
    private MediaType getMimeType(UploadFile uploadFile) {
        if("gif".equals(uploadFile.getExt())) {
            return MediaType.IMAGE_GIF;
        } else if ("png".equals(uploadFile.getExt())) {
            return MediaType.IMAGE_PNG;
        } else if ("jpg".equals(uploadFile.getExt()) || "jpeg".equals(uploadFile.getExt())) {
            return MediaType.IMAGE_JPEG;
        }
        return MediaType.APPLICATION_OCTET_STREAM;
    }
}
