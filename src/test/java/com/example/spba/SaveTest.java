package com.example.spba;

import com.example.spba.domain.entity.Admin;
import com.example.spba.utils.FileUtils;
import org.junit.jupiter.api.Test;

import java.io.IOException;

/**
 * @author ZhangX
 * 作者: JAVA祖父
 * 日期: 2025/9/4
 * 时间: 16:39
 */
public class SaveTest {
    @Test
    public void save() throws IOException {
        Admin admin = new Admin();
        admin.setIdentity_number("12345699999");
        String folderPath = FileUtils.createUserFolder(admin.getIdentity_number());
        System.out.println(folderPath);
    }
}
