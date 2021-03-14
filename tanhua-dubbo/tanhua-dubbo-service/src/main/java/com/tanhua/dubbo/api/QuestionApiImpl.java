package com.tanhua.dubbo.api;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.tanhua.domain.db.Question;
import com.tanhua.dubbo.mapper.QuestionMapper;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;

@Service
public class QuestionApiImpl implements QuestionApi {
    @Autowired
    private QuestionMapper questionMapper;
    // 获取到ID
    @Override
    public Question findByUserId(Long userId) {
        System.out.println("userID获取到的保存问题+==="+userId);
        QueryWrapper<Question> user = new QueryWrapper<>();
        QueryWrapper<Question> id = user.eq("user_Id", userId);

        Question question = questionMapper.selectOne(id);

        return question;
    }



    //添加陌生人问题
    @Override
    public void save(Question question) {

        questionMapper.insert(question);
    }


    //更新陌生人问题
    @Override
    public void update(Question question) {
        questionMapper.updateById(question);
    }
}
