package com.example.spba;

import com.example.spba.domain.entity.Admin;
import com.example.spba.utils.FileUtils;
import org.junit.jupiter.api.Test;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Date;

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
        admin.setIdentityNumber("12345699999");
        String folderPath = FileUtils.createUserFolder(admin.getIdentityNumber());
        System.out.println(folderPath);
    }
    @Test
    @GetMapping("/files/list")
    public List<Map<String, Object>> listFiles(@RequestParam String identityNumber) throws IOException {
        String date = new SimpleDateFormat("yyyy").format(new Date());
        String folderName = date + "_" + identityNumber;

        Path userDir = Paths.get("D:/audit/customers", folderName);

        List<Map<String, Object>> result = new ArrayList<>();
        Files.list(userDir).forEach(path -> {
            Map<String, Object> fileInfo = new HashMap<>();
            fileInfo.put("name", path.getFileName().toString());
            fileInfo.put("isDir", Files.isDirectory(path));
            fileInfo.put("path", path.toString());
            result.add(fileInfo);
        });

        return result;
    }
}
