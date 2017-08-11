package net.chetong.order.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import org.apache.commons.lang.StringUtils;
public class DateUtil {

	
    // 短日期格式
    public static String DATE_FORMAT = "yyyy-MM-dd";    
    // 长日期格式
    public static String TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    
    public static String LONG_FORMAT = "yyyyMMddHHmmss";
    //当前时间 精确到分钟
    public static String TIME_FORMATM = "yyyy-MM-dd HH:mm";
    /**
     * 获取当前时间
     * @return
     * @author wufeng@chetong.net
     */
    public static String getNowDateFormatShort(){
    	SimpleDateFormat format = new SimpleDateFormat(DATE_FORMAT);
    	String nowDate = format.format(new Date());
    	return nowDate;
    }
    
    /**
     * 获取当前时间
     * @return
     * @author wufeng@chetong.net
     */
    public static String getNowDateFormatLong(){
    	SimpleDateFormat format = new SimpleDateFormat(LONG_FORMAT);
    	String nowDate = format.format(new Date());
    	return nowDate;
    }
    
    /**
     * 获取当前时间
     * @return
     * @author wufeng@chetong.net
     */
    public static String getNowDateFormatTime(){
    	SimpleDateFormat format = new SimpleDateFormat(TIME_FORMAT);
    	String nowDate = format.format(new Date());
    	return nowDate;
    }
    
    /**
     * 获取过去多少分钟的时间
     * @param date   
     * @param length 
     * @return
     * @author wufeng@chetong.net
     */
    public static String getAgoTimeByMinute(Date date,int length){
    	Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.set(Calendar.MINUTE, calendar.get(Calendar.MINUTE) - length);
		SimpleDateFormat df = new SimpleDateFormat(TIME_FORMAT);
		return df.format(calendar.getTime());
    }
    

	public static boolean isValidDate(String str, String paraDateFormat) {

		if (StringUtils.trimToNull(str) == null) {
			return false;
		}

		boolean convertSuccess = true;

		if (StringUtils.trimToNull(paraDateFormat) != null) {
			TIME_FORMAT = paraDateFormat;
		}

		SimpleDateFormat format = new SimpleDateFormat(TIME_FORMAT);

		// 设置lenient为false.
		// 否则SimpleDateFormat会比较宽松地验证日期，比如2007/02/29会被接受，并转换成2007/03/01
		format.setLenient(false);
		try {
			format.parse(str);
		} catch (Exception e) {
			e.printStackTrace();
			convertSuccess = false;
		}

		return convertSuccess;
	}

	 public static Date stringToDate(String str,String format) {
			if (null == str || str.equals(""))
				return null;
			
			if(format==null || format.equals("")){
				format="yyyy-MM-dd HH:mm:SS";
			}
			
			SimpleDateFormat sdf = new SimpleDateFormat(format);
			
			try {
				return sdf.parse(str);
			} catch (ParseException e) {
				e.printStackTrace();
			}
			
			return null;
	}
	
	public static String dateToString(Date formatDate, String paraDateFormat) {

		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(TIME_FORMAT);
		if (formatDate == null) {
			return simpleDateFormat.format(new Date());
		}

		if (StringUtils.trimToNull(paraDateFormat) != null) {
			TIME_FORMAT = paraDateFormat;
		}

		try {
			return simpleDateFormat.format(formatDate);
		} catch (Exception e) {
			e.printStackTrace();
			return simpleDateFormat.format(new Date());
		}
	}

	static long CONST_WEEK = 3600 * 1000 * 24 * 7;
	/**
	 * 得到两个时间间的星期数
	 * @author wufj@chetong.net
	 *         2015年12月16日 下午2:08:51
	 * @param startTime
	 * @param endTime
	 * @return
	 * @throws Exception
	 */
	public static int getWeekCount(String startTime, String endTime) throws Exception{
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Calendar before = Calendar.getInstance();
		Calendar after = Calendar.getInstance();
		before.setTime(sdf.parse(startTime));
		after.setTime(sdf.parse(endTime));
		int week = before.get(Calendar.DAY_OF_WEEK);
		before.add(Calendar.DATE, -week);
		week = after.get(Calendar.DAY_OF_WEEK);
		after.add(Calendar.DATE, 7 - week);
		int interval = (int) ((after.getTimeInMillis() - before
				.getTimeInMillis()) / CONST_WEEK);
		interval = interval - 1;
		return interval;
	}
	
	/**
	 * 得到两个时间间的星期数
	 * @author wufj@chetong.net
	 *         2015年12月21日 上午9:23:55
	 * @param before
	 * @param after
	 * @return
	 * @throws Exception
	 */
	public static int getWeekCount(Calendar before, Calendar after) throws Exception{
		int week = before.get(Calendar.DAY_OF_WEEK);
		before.add(Calendar.DATE, -week);
		week = after.get(Calendar.DAY_OF_WEEK);
		after.add(Calendar.DATE, 7 - week);
		int interval = (int) ((after.getTimeInMillis() - before
				.getTimeInMillis()) / CONST_WEEK);
		interval = interval - 1;
		return interval;
	}
	
	/**
	 * 得到两个时间间的月数
	 * @author wufj@chetong.net
	 *         2015年12月16日 下午2:08:51
	 * @param startTime
	 * @param endTime
	 * @return
	 * @throws Exception
	 */
	public static int getMonCount(Calendar startTime, Calendar endTime) throws Exception{
	       int iMonth = 0;     
	       int flag = 0;     
           if (endTime.equals(startTime)){
        	   return 0;   
           }     
           if (startTime.after(endTime)){     
               Calendar temp = startTime;     
               startTime = endTime;     
               endTime = temp;     
           }     
           if (endTime.get(Calendar.DAY_OF_MONTH) < startTime.get(Calendar.DAY_OF_MONTH)){
        	   flag = 1;     
           }
           if (endTime.get(Calendar.YEAR) > startTime.get(Calendar.YEAR)){
        	   iMonth = ((endTime.get(Calendar.YEAR) - startTime.get(Calendar.YEAR))
        			   * 12 + endTime.get(Calendar.MONTH) - flag)     
        			   - startTime.get(Calendar.MONTH);     
           }
           else{
        	   iMonth = endTime.get(Calendar.MONTH)     
        			   - startTime.get(Calendar.MONTH) - flag;     
           }
	       return iMonth;     
	}
	
	/**
	 * 得到两个时间间的月数
	 * @author wufj@chetong.net
	 *         2015年12月16日 下午2:08:51
	 * @param startTime
	 * @param endTime
	 * @return
	 * @throws Exception
	 */
	public static int getMonCount(String startTime, String endTime) throws Exception{
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	       int iMonth = 0;     
	       int flag = 0;     
           Calendar objCalendarDate1 = Calendar.getInstance();     
           objCalendarDate1.setTime(sdf.parse(startTime));     
    
           Calendar objCalendarDate2 = Calendar.getInstance();     
           objCalendarDate2.setTime(sdf.parse(endTime));     
    
           if (objCalendarDate2.equals(objCalendarDate1)){
        	   return 0;   
           }     
           if (objCalendarDate1.after(objCalendarDate2)){     
               Calendar temp = objCalendarDate1;     
               objCalendarDate1 = objCalendarDate2;     
               objCalendarDate2 = temp;     
           }     
           if (objCalendarDate2.get(Calendar.DAY_OF_MONTH) < objCalendarDate1.get(Calendar.DAY_OF_MONTH)){
        	   flag = 1;     
           }
           if (objCalendarDate2.get(Calendar.YEAR) > objCalendarDate1.get(Calendar.YEAR)){
        	   iMonth = ((objCalendarDate2.get(Calendar.YEAR) - objCalendarDate1.get(Calendar.YEAR))
        			   * 12 + objCalendarDate2.get(Calendar.MONTH) - flag)     
        			   - objCalendarDate1.get(Calendar.MONTH);     
           }
           else{
        	   iMonth = objCalendarDate2.get(Calendar.MONTH)     
        			   - objCalendarDate1.get(Calendar.MONTH) - flag;     
           }
	       return iMonth;     
	}

	/**
	 * 获取系统时间到分钟 yyyy-MM-dd HH:mm
	 * 
	 */
	public static ResultVO<Object> getNowDateMinute() throws Exception {
		ResultVO<Object> result = new ResultVO<Object>();
		SimpleDateFormat sdFormatter = new SimpleDateFormat(TIME_FORMATM);
		String retStrFormatNowDate = sdFormatter.format(new Date());	
		ProcessCodeEnum.SUCCESS.buildResultVO(result, retStrFormatNowDate);
		return result;
	}
	
	public static String getAskforTime(String limitTimeStr) throws ParseException{
		if(!StringUtil.isNullOrEmpty(limitTimeStr)){
			Date nowTime = new Date();
			SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			SimpleDateFormat sf2 = new SimpleDateFormat("HH:mm:ss");
			Date limitTime = sf.parse(limitTimeStr);
			
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(limitTime);
			int limitTimeDay = calendar.get(Calendar.DAY_OF_YEAR);
			calendar.setTime(nowTime);
			int nowDay = calendar.get(Calendar.DAY_OF_YEAR);
			
			if(0 == limitTimeDay - nowDay){
				return "今天 "+sf2.format(limitTime);
			}else if(1 == limitTimeDay - nowDay){
				return "明天"+sf2.format(limitTime);
			}else if(2 == limitTimeDay - nowDay){
				return "后天"+sf2.format(limitTime);
			}else{
				return limitTimeStr;
			}
		}
		return "";
	}
}
