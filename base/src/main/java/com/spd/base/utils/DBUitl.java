//package com.spd.base.utils;
//
//
//import com.spd.base.R;
//import com.spd.base.db.DbDaoManage;
//
//import java.util.List;
//
///**
// * ----------Dragon be here!----------/
// * 　　　┏┓　　　┏┓
// * 　　┏┛┻━━━┛┻┓
// * 　　┃　　　　　　　┃
// * 　　┃　　　━　　　┃
// * 　　┃　┳┛　┗┳　┃
// * 　　┃　　　　　　　┃
// * 　　┃　　　┻　　　┃
// * 　　┃　　　　　　　┃
// * 　　┗━┓　　　┏━┛
// * 　　　　┃　　　┃神兽保佑
// * 　　　　┃　　　┃代码无BUG！
// * 　　　　┃　　　┗━━━┓
// * 　　　　┃　　　　　　　┣┓
// * 　　　　┃　　　　　　　┏┛
// * 　　　　┗┓┓┏━┳┓┏┛
// * 　　　　　┃┫┫　┃┫┫
// * 　　　　　┗┻┛　┗┻┛
// * ━━━━━━神兽出没━━━━━━
// *
// * @author :孙天伟
// * 联系方式:QQ:420401567
// * 功能描述:  数据库增删改查
// */
//public class DBUitl {
//    public DBUitl() {
//    }
//
//
//    /**
//     * 添加一条数据
//     *
//     * @param body
//     */
//    public void insertDtata(TwBody body) {
//        mDao.insertOrReplace(body);
//    }
//
//    public void insertDtatas(twTimeAndDatas body) {
//        TwApplication.getsInstance().getDaoSession().getTwTimeAndDatasDao().insertOrReplace(body);
//    }
//
//    /**
//     * 查找数据
//     *
//     * @param pNum   腕带编号
//     * @param runNum 体温标签编号
//     * @return
//     */
//    public TwBody whereData(String pNum, String runNum) {
//        TwBody user = mDao.queryBuilder().where(TwBodyDao.Properties.PeopleNun.eq(pNum),
//                TwBodyDao.Properties.RunningNumber.eq(runNum)).build().unique();
//        return user;
//    }
//
//    public void delete(String runNum) {
//        TwBody user = mDao.queryBuilder().where(TwBodyDao.Properties.RunningNumber.eq(runNum)).build().unique();
//        if (user != null) {
//            mDao.deleteByKey(user.getId());
//        }
//    }
//
//    /**
//     * 查数据
//     *
//     * @param runNum 体温标签编号
//     * @return
//     */
//    public boolean queryRunNum(String runNum) {
//        TwBody user = mDao.queryBuilder().where(TwBodyDao.Properties.RunningNumber.eq(runNum)).build().unique();
//        if (user != null) {
//            return true;
//        } else {
//            return false;
//        }
//    }
//
//    /**
//     * 根据 体温标签编号 查找整条数据
//     *
//     * @param runNum
//     * @return
//     */
//    public TwBody queryTwBody(String runNum) {
//        TwBody user = mDao.queryBuilder().where(TwBodyDao.Properties.RunningNumber.eq(runNum)).build().unique();
//        return user;
//    }
//
//    /**
//     * 查找所有数据
//     *
//     * @return
//     */
//    public List<TwBody> queryAll() {
//        List<TwBody> twBodies = mDao.loadAll();
//        if (twBodies != null && twBodies.size() > 0)
//            return twBodies;
//        return twBodies;
//    }
//
//    public void ChagePassIDs() {
//        List<TwBody> twBodies = mDao.loadAll();
//        if (twBodies != null && twBodies.size() > 0) {
//            for (int i = 0; i < twBodies.size(); i++) {
//                cahagePassID(twBodies.get(i).getRunningNumber(), R.drawable.pass_false);
//            }
//        }
//    }
//
//    /**
//     * 根据体温标签编号修改数据
//     *
//     * @param listTime
//     * @param listTemperatures
//     */
//    public void cahageData(String runNum, int Model, String Date, boolean Encrypt,
//                           int Resolution, int Interval, int TimeUnit,
//                           String isLowBattery, int i, List<String> listTime, List<String> listTemperatures, List<Long> listTimeLong) {
//        TwBody user = mDao.queryBuilder().where(
//                TwBodyDao.Properties.RunningNumber.eq(runNum)).build().unique();
//        if (user != null) {
//            user.setModel(Model);
//            user.setDate(Date);
//            user.setEncrypt(Encrypt);
//            user.setResolution(Resolution);
//            user.setInterval(Interval);
//            user.setTimeUnit(TimeUnit);
//            user.setIsLowBattery(isLowBattery);
//            user.setPassId(i);
//            user.setTwTime(listTime);
//            user.setTemperatures(listTemperatures);
//            user.setTwTimeLong(listTimeLong);
//            mDao.update(user);
//        }
//    }
//
//    /**
//     * 修改指定数据
//     *
//     * @param runNum
//     * @param time
//     */
//    public void cahageData(String runNum, String time) {
//
//        TwBody user = mDao.queryBuilder().where(
//                TwBodyDao.Properties.RunningNumber.eq(runNum)).build().unique();
//        if (user != null) {
//            user.setFirstTime(time);
//            mDao.update(user);
//        }
//    }
//
//    public void cahagePassID(String runNum, int id) {
//        TwBody user = mDao.queryBuilder().where(
//                TwBodyDao.Properties.RunningNumber.eq(runNum)).build().unique();
//        if (user != null) {
//            user.setPassId(id);
//            mDao.update(user);
//        }
//    }
//
//    /**
//     * * 修改指定数据
//     *
//     * @param runNum
//     * @param pNum
//     * @param name
//     * @param age
//     * @param gender
//     * @param bedNum
//     */
//
//
//    public void dialogUpData(String runNum1, String runNum, String pNum, String name, String age, String gender, String bedNum) {
//
//        TwBody user = mDao.queryBuilder().where(
//                TwBodyDao.Properties.RunningNumber.eq(runNum1)).build().unique();
//        if (user != null) {
//            user.setRunningNumber(runNum);
//            user.setPName(name);
//            user.setPaAge(age);
//            user.setPGender(gender);
//            user.setPBedNumber(bedNum);
//            user.setPeopleNun(pNum);
//            mDao.update(user);
//        }
//    }
//
//}
