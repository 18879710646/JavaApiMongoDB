package com.tanhua.dubbo.api;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sun.org.apache.xml.internal.security.keys.keyresolver.implementations.PrivateKeyResolver;
import com.tanhua.domain.db.Announcement;
import com.tanhua.domain.vo.PageResult;
import com.tanhua.dubbo.mapper.AnnouncementMapper;
import org.apache.dubbo.config.annotation.Reference;

public class AnnouncementApilmpl  implements AnnouncementApi{
    @Reference
    private AnnouncementMapper announcementMapper;
    @Override
    public PageResult<Announcement> findAll(int page, int size) {
        Page<Announcement> pages = new Page<>(page, size);
        IPage<Announcement> pageInfo = announcementMapper.selectPage(pages, new QueryWrapper<>());
        PageResult<Announcement> pageResult = new PageResult<>(pageInfo.getTotal(), pageInfo.getSize(), pageInfo.getCurrent(),
                pageInfo.getPages(), pageInfo.getRecords());


        return pageResult;
    }
}
