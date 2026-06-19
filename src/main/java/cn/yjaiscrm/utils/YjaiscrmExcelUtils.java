package cn.iyque.utils;

import com.alibaba.excel.EasyExcel;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.List;


@Slf4j
public class IYqueExcelUtils {

    public  static <T> void exprotExcel(HttpServletResponse response, Class<T> clazz, List<T> datas, String fileName){

        try {


            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setCharacterEncoding("utf-8");
            String fileEName = URLEncoder.encode(fileName, "UTF-8").replaceAll("\\+", "%20");
            response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileEName + ".xlsx");

            EasyExcel.write(response.getOutputStream(), clazz).sheet(fileName).doWrite(datas);
        } catch (IOException e) {
           log.error("excel导出错误:"+e.getMessage());
        }


    }
}
