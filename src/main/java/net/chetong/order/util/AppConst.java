package net.chetong.order.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AppConst {

    private static String[] accidentType={"单方","多方","玻璃","盗抢","自燃","水淹","其他"};
    private static String[] accidentDuty={"全责","主责","同责","次责","无责","待定"};
    private static String[] surveyDespType={"现场查勘,痕迹吻合,证件已验","现场查勘,痕迹吻合,证件未验","非现场查勘","无责销案","私了销案","拒赔销案"};
    //private static String[] repairFtType={"综合修理厂","4S店","快修店"};
    private static String[] repairFtType={"综合修理厂","4S店"};

    public static List<AppDict> appDictList=new ArrayList<AppDict>();
    
    //特殊地区（深圳、宁波、青岛、大连）
    //public static List<String> specialAreas = Arrays.asList(new String[]{"440300","330200","210200"});
    public static List<String> specialAreas = Arrays.asList(new String[]{"440300","330200"});

    public static String pin[]={"A","B","C","D","E","F","G","H","I","J","K","L","M","N","O","P","Q","R","S","T","U","V","W","X","Y","Z"};

    static {
        initData();
    }

    /**
     * 事故类型
     */
    private static void initData(){
        for(int i=0;i<accidentType.length;i++){
            AppDict model=new AppDict();
            model.setCode("accidentType");
            model.setName(accidentType[i]);
            model.setValue(String.valueOf(i));
            appDictList.add(model);
        }
        for(int i=0;i<accidentDuty.length;i++){
            AppDict model=new AppDict();
            model.setCode("accidentDuty");
            model.setName(accidentDuty[i]);
            model.setValue(String.valueOf(i));
            appDictList.add(model);
        }
        for(int i=0;i<surveyDespType.length;i++){
            AppDict model=new AppDict();
            model.setCode("surveyDespType");
            model.setName(surveyDespType[i]);
            model.setValue(String.valueOf(i));
            appDictList.add(model);
        }
        for(int i=0;i<repairFtType.length;i++){
            AppDict model=new AppDict();
            model.setCode("repairFtType");
            model.setName(repairFtType[i]);
            model.setValue(String.valueOf(i));
            appDictList.add(model);
        }
    }

    public List<AppDict> getAppDict(){
        return appDictList;
    }

    /**
     * 生成安全符号
     */
    public static String getVerifyCode(String caseNo){
        return PHPMd5.getInstance().getStringMd5(caseNo+"chetong");
    }

}
