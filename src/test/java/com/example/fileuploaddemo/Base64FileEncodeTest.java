package com.example.fileuploaddemo;

import com.example.fileuploaddemo.helper.UploadHelper;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;

@Slf4j
public class Base64FileEncodeTest {

    private String testResourcePath;

    @Before
    public void setup() {
        testResourcePath = "./src/test/resources/images";
    }

    @Test
    public void test1() throws Exception {
        File file = new File(testResourcePath, "/animals/Alligator.jpg");
        String image1 = UploadHelper.encodeBase64(file);
        Assert.assertNotNull(image1);

        log.debug(image1);
    }
}
