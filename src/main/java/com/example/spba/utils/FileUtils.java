package com.example.spba.utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;

/**
 * @author ZhangX
 */
public class FileUtils {

    // 模板文件夹路径（固定）
    private static final String TEMPLATE_DIR = "D:/workSpace/中小会计师事务所审计底稿(企业会计准则)";
    // 存储客户资料的根路径
    private static final String CUSTOMER_BASE_DIR = "D:/workSpace/audit/customers";

    /**
     * 根据企业识别号创建用户文件夹，并复制模板文件
     *
     * @param identityNumber 企业识别号（Admin.identity_number）
     * @return 创建的文件夹路径
     * @throws IOException
     */
    public static String createUserFolder(String identityNumber) {
        try {
            // 日期+企业识别号
            String date = new SimpleDateFormat("yyyy").format(new Date());
            String folderName = date + "_" + identityNumber;

            // 目标路径
            Path targetDir = Paths.get(CUSTOMER_BASE_DIR, folderName);

            // 确保目录存在
            if (!Files.exists(targetDir)) {
                Files.createDirectories(targetDir);
            }

            // 复制模板目录到目标目录
            copyDirectory(Paths.get(TEMPLATE_DIR), targetDir);

            // 返回完整路径
            return targetDir.toAbsolutePath().toString();
        } catch (IOException e) {
            throw new RuntimeException("创建用户文件夹失败: " + e.getMessage(), e);
        }
    }


    /**
     * 递归复制目录
     */
    private static void copyDirectory(Path source, Path target) throws IOException {
        Files.walk(source).forEach(path -> {
            try {
                Path dest = target.resolve(source.relativize(path));
                if (Files.isDirectory(path)) {
                    if (!Files.exists(dest)) {
                        Files.createDirectories(dest);
                    }
                } else {
                    // 覆盖已存在文件
                    Files.copy(path, dest, StandardCopyOption.REPLACE_EXISTING);
                }
            } catch (IOException e) {
                throw new RuntimeException("复制文件失败: " + path, e);
            }
        });
    }
    public static String sanitize(String s){
        // 去除危险字符，防止路径穿越
        return s.replaceAll("[\\\\/:*?\"<>|]", "_").trim();
    }
    public static Path resolveVoucherDir(Path root, String company, LocalDate date, String voucherNo){
        return root.resolve(sanitize(company))
                .resolve(date.toString())
                .resolve(sanitize(voucherNo));
    }
}