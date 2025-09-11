package com.example.spba;

import com.example.spba.domain.entity.Admin;
import com.example.spba.utils.FileUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Test;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.*;

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

    @Test
    public void main() {
        try {
            // 读取 Excel 文件
            FileInputStream fis = new FileInputStream(new File("C:\\Users\\ZhangX\\Desktop/HQ所得税费用.xlsx"));
            Workbook workbook = new XSSFWorkbook(fis);  // 使用 XSSFWorkbook 来读取 .xlsx 文件

            // 获取第一个工作表
            Sheet sheet = workbook.getSheetAt(0);

            // 遍历每一行
            for (Row row : sheet) {
                // 遍历每一列
                for (Cell cell : row) {
                    // 根据单元格类型进行处理
                    switch (cell.getCellType()) {
                        case STRING:
                            String cellValue = cell.getStringCellValue();
                            // 如果值为 "未知数据类型"，可以进行特定处理，比如输出"未定义"
                            if ("未知数据类型".equals(cellValue)) {
                                System.out.print("未定义\t");
                            } else {
                                System.out.print(cellValue + "\t");
                            }
                            break;
                        case NUMERIC:
                            System.out.print(cell.getNumericCellValue() + "\t");
                            break;
                        case BOOLEAN:
                            System.out.print(cell.getBooleanCellValue() + "\t");
                            break;
                        default:
                            System.out.print("未知数据类型\t");
                    }
                }
                System.out.println(); // 换行
            }

            // 关闭资源
            workbook.close();
            fis.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}


