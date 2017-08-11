package ctorder;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Resource;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import net.chetong.order.dao.CommExeSqlDAO;

@RunWith(value=SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"classpath:spring-config.xml","classpath:spring-mybatis.xml"})
public class MyTest {
	
	@Resource
	private CommExeSqlDAO commExeSqlDAO;
	
	@Test
	public void test(){
	}
	
	private void importGroupData(){
		List<Map<String, String>> datas = read("D://桌面//公估费对接//公司名称及对应ID对照表2.xls", "cbs_id","cbs_name","ct_name");
		if(datas==null){
			System.err.println("未读取到数据");
			return ;
		}
		List<Map<String, String>> failData = new ArrayList<>();
		for (int i = 0; i < datas.size(); i++) {
			Map<String, String> data = datas.get(i);
			String userId = commExeSqlDAO.queryForObject("test_mapper.getUserIdForOrgName", data.get("ct_name"));
			data.put("ct_id", userId);
			data.put("type","5");
			data.put("note","机构映射");
			System.out.println(data);
			commExeSqlDAO.insertVO("test_mapper.insertMappingData", data);
		}
		System.out.println("读取数据："+datas.size());
		System.out.println("失败数据："+failData.size());
		for (int i = 0; i < failData.size(); i++) {
			System.out.println("失败数据："+failData.get(i));
		}
	}
	
	private void importWtData(){
		List<Map<String, String>> datas = read("D://桌面//公估费对接//车童网传递数据所需资料//委托人ID对应表.xls", "cbs_id","cbs_name","ct_id");
		if(datas==null){
			System.err.println("未读取到数据");
			return ;
		}
		List<Map<String, String>> failData = new ArrayList<>();
		for (int i = 0; i < datas.size(); i++) {
			Map<String, String> data = datas.get(i);
			String[] ctLoginNames = new String[]{};
			if(data.get("ct_id")!=null){
				ctLoginNames = data.get("ct_id").toString().split(",");
			}
			for (int j = 0; j < ctLoginNames.length; j++) {
				if(ctLoginNames[j]==null||"".equals(ctLoginNames[j])) continue;
				Map<String, Object> userMap = commExeSqlDAO.queryForObject("test_mapper.getUserIdForLoginName", ctLoginNames[j]);
				if(userMap!=null){
					Map<String, Object> insertData = new HashMap<>();
					insertData.put("ct_id", userMap.get("userId"));
					insertData.put("ct_name", userMap.get("orgName"));
					insertData.put("cbs_id", data.get("cbs_id"));
					insertData.put("cbs_name", data.get("cbs_name"));
					insertData.put("type", "4");
					insertData.put("note","委托公司映射");
					System.out.println(data);
					commExeSqlDAO.insertVO("test_mapper.insertMappingData", insertData);
				}else{
					failData.add(data);
				}
			}
		}
		System.out.println("读取数据："+datas.size());
		System.out.println("失败数据："+failData.size());
		for (int i = 0; i < failData.size(); i++) {
			System.out.println("失败数据："+failData.get(i));
		}
	}
	
	private List<Map<String, String>> read(String path, String... keys){
		System.out.println("读取数据开始……");
		FileInputStream in = null;
		List<Map<String, String>> resultList = new ArrayList<>();
		try {
			in = new FileInputStream(new File(path));
			HSSFWorkbook workbook = new HSSFWorkbook(in);
			HSSFSheet sheet = workbook.getSheetAt(0);
			
			int lastRowNum = sheet.getLastRowNum();
			for (int i = 1; i <= lastRowNum; i++) {
				Map<String, String> map = new HashMap<>();
				Row row = sheet.getRow(i);
				for (int j = 0; j < keys.length; j++) {
					Cell cell = row.getCell(j);
					if(cell==null) continue;
					String stringCellValue = null;
					cell.setCellType(Cell.CELL_TYPE_STRING);
					stringCellValue = cell.getStringCellValue();
					stringCellValue = stringCellValue==null?"":stringCellValue;
					map.put(keys[j], stringCellValue);
				}
				resultList.add(map);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			if(in!=null){
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		System.out.println("读取数据完成……");
		return resultList;
	}
	
	
}
