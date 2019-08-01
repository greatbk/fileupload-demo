package com.example.fileuploaddemo.entity;

import com.example.fileuploaddemo.helper.UploadHelper;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;

@Data
@NoArgsConstructor
public class UploadFile {

    /**
     * 파일명
     */
    private String filename;

    /**
     * 확장자
     */
    private String extension;

    /**
     * 파일크기
     */
    private long size;

    /**
     * BASE64로 인코딩된 파일 문자열
     */
    private String base64String;

    /**
     * 서버 기본 경로
     */
    private String serverBasePath;

    /**
     * 서버 경로
     */
    private String serverPath;

    /**
     * 서브디렉토리 경로
     */
    private String subPath;

    /**
     * 서버 파일명
     */
    private String serverFilename;

    /**
     * 원본 파일이름 사용여부
     */
    private boolean keepOriginalFilename;

    /**
     *  생성자
     * @param file 업로드된 파일
     */
    public UploadFile(MultipartFile file) {
        //기본값 설정
        keepOriginalFilename = false;

        if(file != null) {
            filename = file.getOriginalFilename();
            size = file.getSize();
            extension = parseExtension();
            serverFilename = createServerFilename();
            serverPath = createServerPath();
        }
    }

    /**
     * 초기화
     */
    public void init() {
        if(StringUtils.isNotEmpty(filename)) {
            extension = parseExtension();
            serverFilename = createServerFilename();
            serverPath = createServerPath();
        }
    }

    /**
     * 클래스 멤버 serverBasePath 값을 설정한다.
     * @param serverBasePath 서버 기본 경로
     */
    public void setServerBasePath(String serverBasePath) {
        this.serverBasePath = serverBasePath;
        serverPath = createServerPath();
    }

    /**
     * 클래스 멤버 subPath 값을 설정한다.
     * @param subPath 서브디렉토리 경로
     */
    public void setSubPath(String subPath) {
        this.subPath = subPath;
        serverPath = createServerPath();
    }

    /**
     * 클래스 멤버 keepOriginalFilename 값을 설정한다.
     * @param keepOriginalFilename 원본 파일이름 사용여부
     */
    public void setKeepOriginalFilename(boolean keepOriginalFilename) {
        this.keepOriginalFilename = keepOriginalFilename;
        serverFilename = createServerFilename();
    }

    /**
     * 파일 확장자를 반환한다.
     * @return 파일 확장자
     */
    private String parseExtension() {
        if(filename != null) {
            int index = filename.lastIndexOf(".");
            if(index >= 0) {
                return filename.substring(index + 1).toLowerCase();
            }
        }
        return null;
    }

    /**
     * 서버에 저장될 파일명을 반환한다.
     * @return 서버에 저장될 파일명
     */
    private String createServerFilename() {
        if(keepOriginalFilename) {
            return filename;
        }
        return String.format("CGI-%s%d", RandomStringUtils.randomAlphabetic(5), System.currentTimeMillis());
    }

    /**
     * 업로드된 파일의 서버 파일 전체 경로를 반환한다.
     * @return 업로드된 파일의 서버 파일 전체 경로
     */
    private String createServerPath() {
        if(StringUtils.isEmpty(subPath)) {
            return UploadHelper.toBasePath(serverBasePath);
        }
        return String.format("%s/%s", UploadHelper.toBasePath(serverBasePath), UploadHelper.toSubPath(subPath));
    }

    /**
     * JSON 문자열을 UploadFile 인스턴스 목록으로 반환한다.
     * @param jsonString JSON 문자열
     * @return UploadFile 인스턴스 목록
     * @throws Exception
     */
    public static List<UploadFile> bind(String jsonString) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        return Arrays.asList(mapper.readValue(jsonString, UploadFile[].class));
    }
}
