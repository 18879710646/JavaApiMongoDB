package com.tanhua.server.service;
import com.tanhua.domain.db.Announcement;
import com.tanhua.domain.vo.AnnouncementVo;
import com.tanhua.domain.vo.PageResult;
import com.tanhua.dubbo.api.AnnouncementApi;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.beans.BeanUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;


/**
 * 查询公告列表
 */
@Service
@Slf4j
public class AnnounService {
    @Reference
    private AnnouncementApi announcementApi;
    public ResponseEntity announcements(int page, int pagesize) {
        // 调用接口开实现查询，获取数据
        PageResult<Announcement> pageResult = announcementApi.findAll(page, pagesize);
        //  获取到公告的所有对象,并且获取到生成的不报空指针异常和增删的集合,存入到集合中
        List<Announcement> items = pageResult.getItems();
        // 创建一个泛型为announcementVo的list集合，后面来添加数据
        List<AnnouncementVo> list=new ArrayList<>();
        for (Announcement item : items) {
            // 对数据封装为announcentVo对象
            AnnouncementVo vo = new AnnouncementVo();
            // 拷贝数据属性到vo中
            BeanUtils.copyProperties(item, vo);
            if(item.getCreated() != null) {
                vo.setCreateDate(new SimpleDateFormat("yyyy-MM-dd hh:mm").format(item.getCreated()));
            }
            // 把封装好的数据添加到list集合中
            list.add(vo);
        }
        // 把数据封装为一个pageResult
        PageResult<Announcement> result = new PageResult(
                pageResult.getCounts(),pageResult.getPage(),pageResult.getPages(),pageResult.getPagesize(),list
        );
        return ResponseEntity.ok(result);



    }
}
