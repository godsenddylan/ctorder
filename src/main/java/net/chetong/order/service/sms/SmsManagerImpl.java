package net.chetong.order.service.sms;

import javax.annotation.Resource;

import net.chetong.order.service.common.BaseService;

import org.springframework.stereotype.Service;

import com.chetong.aic.api.remoting.sms.SysSmsService;



/**
 * @author lijq
 * 
 */
@Service("smsManager")
public class SmsManagerImpl extends BaseService implements SmsManager {

	private String smsPath;
	private String smsId;
	private String smsPwd;
	private String smsEncode;
	private boolean isEmay = true;

	private String x_id = "cninsure";
	private String x_pwd = "cninsure123";
	@Resource
	private SysSmsService sysSmsService; 
	
	@Override
	public String sendMessageAD(final String mobile,final String content) {
		/*
		 * Get操作的格式为：
		 * http://service.winic.org/sys_port/gateway/?id=param1&pwd=param2
		 * &to=param3&content=param4&time=" 请求字符串例子:
		 * http://service.winic.org/sys_port
		 * /gateway/?id=test&pwd=123456&to=13600000000
		 * ,13128989997&content=hello!&time="
		 */
		String result = "0";
		/*if (isEmay) {
			// TODO 使用北京亿美的短信接口
			result = SDKClient.getClient().sendSMS(new String[] { mobile }, "【车童网】" + content, "", 5) + "";
		} else {

			Integer x_ac = 10;// 发送信息
			HttpURLConnection httpconn = null;

			StringBuilder sb = new StringBuilder();
			sb.append("http://service.winic.org/sys_port/gateway/?");
			sb.append("id=").append(x_id);
			sb.append("&pwd=").append(x_pwd);
			sb.append("&to=").append(mobile);
			try {
				sb.append("&content=").append(URLEncoder.encode(content, "gb2312"));
			} catch (UnsupportedEncodingException e1) {
				e1.printStackTrace();
			}
			sb.append("&content=").append(content);
			sb.append("&time=");
			try {
				URL url = new URL(sb.toString());
				httpconn = (HttpURLConnection) url.openConnection();
				BufferedReader rd = new BufferedReader(new InputStreamReader(httpconn.getInputStream()));
				result = rd.readLine();
				rd.close();
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				if (httpconn != null) {
					httpconn.disconnect();
					httpconn = null;
				}
			}
		}*/
		
		new Thread(new Runnable() {
			@Override
			public void run() {
				sysSmsService.sendSms(mobile, content);
			}
		}).start();
		
		return result;
	}


	public String getSmsPath() {
		return smsPath;
	}

	public void setSmsPath(String smsPath) {
		this.smsPath = smsPath;
	}

	public String getSmsId() {
		return smsId;
	}

	public String getSmsPwd() {
		return smsPwd;
	}

	public String getSmsEncode() {
		return smsEncode;
	}

	public void setSmsId(String smsId) {
		this.smsId = smsId;
	}

	public void setSmsPwd(String smsPwd) {
		this.smsPwd = smsPwd;
	}

	public void setSmsEncode(String smsEncode) {
		this.smsEncode = smsEncode;
	}
}
