package com.example.fileuploaddemo.helper;

import com.example.fileuploaddemo.entity.UploadFile;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

@Slf4j
public class UploadHelper {

    /**
     * 이미지 확장자 배열
     */
    private static String[] IMAGE_EXTENSIONS = new String[]{"gif", "png", "jpg", "jpeg"};

    /**
     *  이미지 파일의 확장자를 가진 경우 true를 반환한다.
     * @param uploadFile 업로드된 파일 정보
     * @return true 이미지파일 확장자, false 이미지파일 확장자가 아님
     */
    public static boolean isImageExtension(UploadFile uploadFile) {
        if(Arrays.asList(IMAGE_EXTENSIONS).contains(uploadFile.getExtension())) {
            return true;
        }
        return false;
    }

    /**
     * 업로드해서 안되는 확장자
     */
    private static String[] DENY_EXTENSIONS = new String[] {
            "asp", "jsp", "php", "js",
            "xml", "json", "yml", "yaml", "properties",
            "exe", "bat", "com",
            "ini", "conf",
            "pl", "rb", "ry",
            "py", "pyc", "pyd", "pyo",
            "gradle",
            "sh", "zsh", "csh",
            "deb", "rpm"
    };

    /**
     * 업로드해서는 안되는 확장자인 경우에 true를 반환한다.
     * @param uploadFile 업로드된 파일 정보
     * @return ture 업로드 가능한 확장자, false 업로드 불가능한 확장자
     */
    public static boolean isDenyExtension(UploadFile uploadFile) {
        if(Arrays.asList(DENY_EXTENSIONS).contains(uploadFile.getExtension())) {
            return true;
        }
        return false;
    }

    /**
     * 디렉토리 경로에서 마지막 경로 구분 문자열을 제거한다.
     * @param path 디렉토리 경로
     * @return 마지막 경로 구분 문자를 제거한 디렉토리 경로
     */
    private static String removeLastPath(String path) {
        if(path != null) {
            path = path.trim();
        }

        if(StringUtils.isNotEmpty(path)) {
            String lastStr = path.substring(path.length() - 1);
            if("/".equals(lastStr) || "\\".equals(lastStr)) {
                return path.substring(0, path.length() - 1);
            }
        }
        return path;
    }

    /**
     * 디렉토리 경로에서 처음 경로 구분 문자열을 제거한다.
     * @param path 디렉토리 경로
     * @return 처음 경로 구분 문자가 제거된 디렉토리 경로
     */
    private static String removeFirstPath(String path) {
        if(path != null) {
            path = path.trim();
        }

        if(StringUtils.isNotEmpty(path)) {
            String firstChar = path.substring(0, 1);
            if("/".equals(firstChar) || "\\".equals(firstChar)) {
                return path.substring(1);
            }
        }
        return path;
    }

    /**
     * 디렉토리 시작 경로에서 마지막의 경로 문자열을 제거한다.
     * @param path 디렉토리 시작 경로
     * @return 마지막 경로 문자열이 제거된 디렉토리 시작 경로
     */
    public static String toBasePath(String path) {
        return removeLastPath(path);
    }

    /**
     * 디렉토리 중간 경로에서 처음, 마지막의 경로 문자열을 제거한다.
     * @param path 디렉토리 중간 경로
     * @return 처음, 마지막 경로 문자열이 제거된 디렉토리 중간 경로
     */
    public static String toSubPath(String path) {
        return removeFirstPath(removeLastPath(path));
    }

    /**
     * BASE64로 인코딩된 문자열을 파일로 반환한다.
     * @param uploadFile 업로드된 파일 정보
     * @return 파일
     */
    private static File decodeBase64(UploadFile uploadFile) throws IOException {
        if(uploadFile != null) {
            File file = new File(uploadFile.getServerPath(), uploadFile.getServerFilename());
            byte[] bytes = Base64.decodeBase64(uploadFile.getBase64String());
            FileUtils.writeByteArrayToFile(file, bytes);

            uploadFile.setSize(file.length());
            return file;
        }
        return null;
    }

    /**
     * 파일을 저장한다.
     * @param file 업로드한 파일
     * @param uploadFile 업로드한 파일 정보
     * @throws IOException
     */
    public static void save(MultipartFile file, UploadFile uploadFile) throws IOException {
        if(file != null && uploadFile != null) {
            createDirectory(uploadFile);
            File filePath = new File(uploadFile.getServerPath(), uploadFile.getServerFilename());
            file.transferTo(filePath);
        }
    }

    /**
     * BASE64 문자열로 전달받은 파일을 저장한다.
     * @param uploadFile
     * @throws IOException
     */
    public static void save(UploadFile uploadFile) throws IOException {
        if(uploadFile != null) {
            createDirectory(uploadFile);
            decodeBase64(uploadFile);
        }
    }

    /**
     * 파일 저장을 위한 디렉토리를 생성한다.
     * @param uploadFile 업로드한 파일 정보
     */
    private static void createDirectory(UploadFile uploadFile) {
        File serverPath = new File(uploadFile.getServerPath());
        if(serverPath != null && !serverPath.exists()) {
            serverPath.mkdirs();
        }
    }

    /**
     * 확장자에 대한 마임타입을 반환한다.
     * @param uploadFile 업로드한 파일 정보
     * @return 마임타입
     */
    public static MediaType getMimeType(UploadFile uploadFile) {
        if("gif".equals(uploadFile.getExtension())) {
            return MediaType.IMAGE_GIF;
        } else if ("png".equals(uploadFile.getExtension())) {
            return MediaType.IMAGE_PNG;
        } else if ("jpg".equals(uploadFile.getExtension()) || "jpeg".equals(uploadFile.getExtension())) {
            return MediaType.IMAGE_JPEG;
        }
        return MediaType.APPLICATION_OCTET_STREAM;
    }
}
