package com.pwhintek.backend;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import com.pwhintek.backend.dto.DetailedArticleDTO;
import com.pwhintek.backend.dto.Result;
import com.pwhintek.backend.entity.Article;
import com.pwhintek.backend.entity.User;
import com.pwhintek.backend.mapper.ArticleMapper;
import com.pwhintek.backend.service.ArticleService;
import com.pwhintek.backend.utils.RedisStorageSolution;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import static com.pwhintek.backend.constant.RedisConstants.ARTICLE_PREFIX;
import static com.pwhintek.backend.constant.RedisConstants.ARTICLE_TTL;

/**
 * 文章管理测试类
 *
 * @author ChillyBlaze
 * @version 1.0
 * @since 2022 May 28 17:14
 */
@SpringBootTest
public class ArticleTests {

    @Autowired
    ArticleService articleService;
    @Autowired
    ArticleMapper articleMapper;
    @Autowired
    RedisStorageSolution redisStorageSolution;

    @Test
    void databaseTest() {
        List<Article> list = articleService.list();
        Page<Article> page = new Page<>(1, 5);
        LambdaQueryWrapper<Article> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Article::getUid, "1518515100758265857");
        Page<Article> page1 = articleService.page(page, wrapper);
        List<Article> records = page1.getRecords();
        for (Article record : records) {
            System.out.println(record);
        }
    }

    @Test
    void leftJoinTest() {
        IPage<DetailedArticleDTO> page = articleService.selectJoinListPage(new Page<>(2, 2), DetailedArticleDTO.class, new MPJLambdaWrapper<Article>()
                .selectAll(Article.class)
                .selectAs(Article::getId, DetailedArticleDTO::getId)
                .selectAs(User::getId, DetailedArticleDTO::getUid)
                .select(User::getUsername, User::getNickname, User::getPortrait)
                .leftJoin(User.class, User::getId, Article::getUid));
        List<DetailedArticleDTO> records = page.getRecords();
        for (DetailedArticleDTO record : records) {
            System.out.println(record);
        }
    }

    @Test
    void idListRedisTest() {
        List<Article> listO = articleService.list(new LambdaQueryWrapper<Article>().select(Article::getId));
        List<Long> listI = new ArrayList<>();
        for (Article article : listO) {
            listI.add(article.getId());
        }
        Function<Long, DetailedArticleDTO> f = r -> articleService.selectJoinOne(DetailedArticleDTO.class, new MPJLambdaWrapper<Article>()
                .selectAll(Article.class)
                .selectAs(Article::getId, DetailedArticleDTO::getId)
                .selectAs(User::getId, DetailedArticleDTO::getUid)
                .select(User::getUsername, User::getNickname, User::getPortrait)
                .leftJoin(User.class, User::getId, Article::getUid)
                .eq(Article::getId, r));
        List<DetailedArticleDTO> dtos = redisStorageSolution.queryWithIdList(ARTICLE_PREFIX, listI, DetailedArticleDTO.class, f, ARTICLE_TTL, TimeUnit.DAYS);
        for (DetailedArticleDTO dto : dtos) {
            System.out.println(dto);
        }
    }

    @Test
    void leftJoinOneTest() {
        Function<Long, DetailedArticleDTO> f = r -> articleService.selectJoinOne(DetailedArticleDTO.class, new MPJLambdaWrapper<Article>()
                .selectAll(Article.class)
                .selectAs(Article::getId, DetailedArticleDTO::getId)
                .selectAs(User::getId, DetailedArticleDTO::getUid)
                .select(User::getUsername, User::getNickname, User::getPortrait)
                .leftJoin(User.class, User::getId, Article::getUid)
                .eq(Article::getId, r));
        System.out.println(f.apply(1L));
    }

    @Test
    void selectPageTest() {
        // Result result = articleService.pageSelectById(1, "1518515100758265857");
        Result result = articleService.pageSelect(1);
        List<DetailedArticleDTO> data = (List<DetailedArticleDTO>) result.getData();
        for (DetailedArticleDTO datum : data) {
            System.out.println(datum);
        }
    }

    @Test
    void wrapperTest() {
        LambdaQueryWrapper<Article> wrapper = new LambdaQueryWrapper<Article>().select(Article::getId, Article::getCreateTime).orderByDesc(Article::getCreateTime);
        Page<Article> page = new Page<>(1, 3);
        for (Article record : articleService.list(wrapper)) {
            System.out.println(record);
        }
    }
}
