package com.tanhua.dubbo.api;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.tanhua.domain.db.Settings;
import com.tanhua.dubbo.mapper.SettingMapper;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;

@Service
public class SettingApiImpl implements SettingApi {
    @Autowired
    private SettingMapper settingMapper;
    //获取到陌生人的id
    @Override
    public Settings findByUserId(Long userId) {
        System.out.println("============来到了findById中");
        // 构建查询条件
        QueryWrapper<Settings> queryWrapper = new QueryWrapper<Settings>();
        // where user_id=userId
        queryWrapper.eq("user_id",userId);
        // 只查询一条记录，如果有多条则报错
        return settingMapper.selectOne(queryWrapper);
    }
// 更新通用设置
    @Override
    public void updateNotification(Settings settings) {

        settingMapper.updateById(settings);
    }

    // 保存通用设置
    @Override
    public void save(Settings settings) {
        settingMapper.insert(settings);
    }


}
