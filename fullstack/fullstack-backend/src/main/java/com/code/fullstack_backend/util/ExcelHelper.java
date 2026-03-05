package com.code.fullstack_backend.util;

import com.code.fullstack_backend.dto.UserRequestDTO;
import org.apache.poi.ss.usermodel.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ExcelHelper {
    public static String TYPE = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";

    public static boolean hasExcelFormat(MultipartFile file) {
        return TYPE.equals(file.getContentType());
    }


    // ExcelHelper.java (conceptual improvements)
    public static List<UserRequestDTO> excelToUsers(InputStream is) {
        try (Workbook workbook = WorkbookFactory.create(is)) {
            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rows = sheet.iterator();
            List<UserRequestDTO> userList = new ArrayList<>();
            int rowNumber = 0;
            var formatter = new org.apache.poi.ss.usermodel.DataFormatter();

            while (rows.hasNext()) {
                Row currentRow = rows.next();
                if (rowNumber++ == 0) continue; // skip header

                UserRequestDTO user = new UserRequestDTO();
                user.setUsername(formatter.formatCellValue(currentRow.getCell(0)));
                user.setName(formatter.formatCellValue(currentRow.getCell(1)));
                user.setEmail(formatter.formatCellValue(currentRow.getCell(2)));
                user.setAddress(formatter.formatCellValue(currentRow.getCell(3)));

                // optional: basic validations / skip if mandatory fields are blank
                if (user.getUsername() == null || user.getUsername().isBlank()) continue;
                if (user.getEmail() == null || user.getEmail().isBlank()) continue;

                userList.add(user);
            }
            return userList;
        } catch (Exception e) {
            throw new RuntimeException("Fail to parse Excel file: " + e.getMessage());
        }
    }

}