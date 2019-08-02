package com.example.fileuploaddemo;

import com.example.fileuploaddemo.entity.UploadFile;
import com.example.fileuploaddemo.helper.UploadHelper;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class Base64FileEncodeTest {

    private String testResourcePath;

    @Before
    public void setup() {
        testResourcePath = "./src/test/resources/images";
    }

    private UploadFile createUploadFile(String base64String) {
        UploadFile uploadFile = new UploadFile();
        uploadFile.setBase64String(base64String);
        uploadFile.setSubPath("notice/10001");
        uploadFile.setFilename("Alligator.jpg");
        uploadFile.setKeepOriginalFilename(true);
        return uploadFile;
    }

    @Test
    public void test1() throws Exception {
        File file = new File(testResourcePath, "/animals/Alligator.jpg");
        String base64String = UploadHelper.encodeBase64(file);

        Assert.assertNotNull(base64String);
        log.debug(base64String);

        UploadFile uploadFile = createUploadFile(base64String);
        String jsonString = UploadFile.toJson(uploadFile);

        Assert.assertNotNull(jsonString);
        log.debug("[" + jsonString + "]");
    }

    @Test
    public void test2() throws Exception {
        File file = new File("C:/upload/dis/sample/big-file.zip");
        String base64String = UploadHelper.encodeBase64(file);

        Assert.assertNotNull(base64String);
        log.debug(base64String);

        UploadFile uploadFile = createUploadFile(base64String);
        String jsonString = UploadFile.toJson(uploadFile);

        Assert.assertNotNull(jsonString);
        log.debug("[" + jsonString + "]");
    }
}
