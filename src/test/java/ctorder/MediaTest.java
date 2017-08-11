package ctorder;

import java.util.Arrays;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.chetong.order.model.PhotoNode;
import net.chetong.order.model.TagNode;
import net.chetong.order.model.form.QueryImageModel;
import net.chetong.order.service.media.WorkPhotoService;
import net.chetong.order.util.DateUtil;

@RunWith(value=SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"classpath:spring-config.xml","classpath:mybatis_config.xml","classpath:spring-mybatis.xml"})
public class MediaTest {
	@Autowired
	private WorkPhotoService workPhotoService;
	
	//@Test
	public void queryPhotos(){
		try {
			QueryImageModel query = new QueryImageModel("19362", "201623265656", "A1610000088", "1", "11");
			List<TagNode> queryPhotos = workPhotoService.queryPhotos(query).getResultObject();
			ObjectMapper objectMapper = new ObjectMapper();
			String resultStr = objectMapper.writeValueAsString(queryPhotos);
			System.err.println(resultStr);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	//@Test
	public void insertPhoto(){
		PhotoNode photo = new PhotoNode();
		photo.setOrderNo("A1610000088");
		photo.setCaseNo("201623265656");
		photo.setImageUrl("http://200681.image.myqcloud.com/200681/0/9b7d86ad-7885-4642-8c8b-0b96c073475b/original");
		photo.setTakephotoTime(DateUtil.getNowDateFormatTime());
		photo.setUserId("19362");
		photo.setServiceId("1");
		photo.setParentId(1089L);
		workPhotoService.insertPhoto(photo);
	}
	
	@Test
	public void deletePhoto(){
		workPhotoService.deletePhoto(Arrays.asList(3677599L,3677600L), "19362", "A1610000088", "1", "0");
	}
}
