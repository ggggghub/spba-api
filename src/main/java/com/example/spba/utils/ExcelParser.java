package com.example.spba.utils;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Excel 解析器
 * 默认解析最后一个工作表
 */
public class ExcelParser {

    private final String filePath;   // Excel 文件路径
    private final int sheetIndex;    // 工作表索引，-1 表示最后一个
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy/MM/dd");

    /**
     * 默认构造：解析最后一个工作表
     * @param filePath Excel 文件路径
     */
    public ExcelParser(String filePath) {
        this.filePath = filePath;
        this.sheetIndex = -1; // 默认最后一个
    }

    /**
     * 自定义构造：指定工作表索引
     * @param filePath Excel 文件路径
     * @param sheetIndex 工作表索引（0=第一个，-1=最后一个）
     */
    public ExcelParser(String filePath, int sheetIndex) {
        this.filePath = filePath;
        this.sheetIndex = sheetIndex;
    }

    /**
     * 解析 Excel 文件
     * 从第 9 行开始（rowIndex=8），排除最后 13 行
     * 返回结果包含：titleName、indexNo、data
     */
    public Map<String, Object> parse() throws IOException, InvalidFormatException {
        File file = new File(filePath);
        if (!file.exists()) {
            throw new RuntimeException("文件不存在: " + filePath);
        }

        Map<String, Object> resultMap = new HashMap<>();
        List<List<String>> data = new ArrayList<>();

        try (FileInputStream fis = new FileInputStream(file);
             Workbook workbook = WorkbookFactory.create(fis)) {

            int targetSheet = (sheetIndex == -1)
                    ? (workbook.getNumberOfSheets() - 1)
                    : sheetIndex;

            if (targetSheet < 0 || targetSheet >= workbook.getNumberOfSheets()) {
                throw new RuntimeException("工作表索引超出范围: " + targetSheet);
            }

            Sheet sheet = workbook.getSheetAt(targetSheet);
            int lastRow = sheet.getLastRowNum(); // 总行数

            // 读取标题（第一行第一个单元格）
            Row titleRow = sheet.getRow(0);
            String titleName = (titleRow != null) ? getCellString(titleRow.getCell(0)) : "";

            // 读取索引号（第二行第8列 H2）
            Row indexRow = sheet.getRow(1);
            String indexNo = (indexRow != null) ? getCellString(indexRow.getCell(7)) : "";

            // 内容区：第9行开始，去掉最后13行
            int startRow = 8;          // 从第9行开始（rowIndex=8）
            int endRow = lastRow - 13; // 去掉最后13行

            for (int r = startRow; r <= endRow; r++) {
                Row row = sheet.getRow(r);
                if (row == null) continue;

                List<String> rowData = new ArrayList<>();
                for (int c = 0; c < row.getLastCellNum(); c++) {
                    Cell cell = row.getCell(c);
                    rowData.add(getCellString(cell));
                }
                data.add(rowData);
            }

            // 封装结果
            resultMap.put("titleName", titleName);
            resultMap.put("indexNo", indexNo);
            resultMap.put("data", data);
        }

        return resultMap;
    }

    /** 单元格转字符串 */
    private static String getCellString(Cell cell) {
        if (cell == null) return "";
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue().trim();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return DATE_FORMAT.format(cell.getDateCellValue()); // 格式化日期
                }
                double val = cell.getNumericCellValue();
                if (val == (long) val) {
                    return String.valueOf((long) val); // 去掉小数点
                } else {
                    return String.valueOf(val);
                }
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                try {
                    return String.valueOf(cell.getNumericCellValue());
                } catch (Exception e) {
                    return cell.getCellFormula();
                }
            default:
                return "";
        }
    }
}
