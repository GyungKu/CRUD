package gk.crud.repository;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import gk.crud.dto.PostCondition;
import gk.crud.dto.PostListDto;
import gk.crud.entity.board.Post;
import gk.crud.entity.member.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.util.StringUtils;

import java.util.List;

import static gk.crud.entity.board.QLikes.*;
import static gk.crud.entity.board.QPost.*;
import static gk.crud.entity.member.QMember.*;
import static org.springframework.util.StringUtils.*;

@RequiredArgsConstructor
public class PostQuerydslRepositoryImpl implements PostQuerydslRepository{

    private final JPAQueryFactory queryFactory;

    //게시글 목록, 검색, 정렬
    @Override
    public Page<PostListDto> findSearchByAll(PostCondition condition, Pageable pageable, String query) {
        List<PostListDto> list = queryFactory
                .select(Projections.fields(PostListDto.class, post.member.userId, post.id.as("postId"), post.title,
                        post.content, post.views, post.commentCount, post.likeCount, post.creationDate, post.lastModifiedDate))
                .from(post)
                .leftJoin(post.member, member)
                .where(searchCondition(condition))
                .orderBy(orderSpecifier(query))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> countQuery = queryFactory
                .select(post.count())
                .from(post)
                .where(searchCondition(condition));

        return PageableExecutionUtils.getPage(list, pageable, countQuery::fetchOne);
    }

    //내 게시글 목록, 검색, 정렬
    @Override
    public Page<PostListDto> findSearchMyPostByAll(PostCondition condition, Pageable pageable, Member memberQ, String query) {

        List<PostListDto> list = queryFactory
                .select(Projections.fields(PostListDto.class, post.member.userId, post.id.as("postId"), post.title,
                        post.content, post.views, post.commentCount, post.likeCount, post.creationDate, post.lastModifiedDate))
                .from(post)
                .leftJoin(post.member, member)
                .where(myPostSearch(condition), post.member.eq(memberQ))
                .orderBy(orderSpecifier(query))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> countQuery = queryFactory
                .select(post.count())
                .from(post)
                .where(myPostSearch(condition), post.member.eq(memberQ));

        return PageableExecutionUtils.getPage(list, pageable, countQuery::fetchOne);
    }

    @Override
    public Page<PostListDto> findSearchMyLikesByPost(PostCondition condition, Pageable pageable, Member memberQ, String query) {

        List<PostListDto> list = queryFactory
                .select(Projections.fields(PostListDto.class, post.member.userId, post.id.as("postId"), post.title,
                        post.content, post.views, post.commentCount, post.likeCount, post.creationDate, post.lastModifiedDate))
                .from(post)
                .leftJoin(post.member, member)
                .rightJoin(post.likes, likes)
                .where(myPostSearch(condition), likes.member.eq(memberQ))
                .orderBy(orderSpecifier(query))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> countQuery = queryFactory
                .select(post.count())
                .from(post)
                .where(myPostSearch(condition), post.member.eq(memberQ));

        return PageableExecutionUtils.getPage(list, pageable, countQuery::fetchOne);
    }

    @Override
    public Boolean findHasLike(Member member, Post post) {
        Long count = queryFactory
                .select(likes.count())
                .from(likes)
                .where(likes.member.eq(member), likes.post.eq(post))
                .fetchOne();

        System.out.println("count = " + count);

        if (count == 1) {
            return true;
        }
        return false;
    }

    //검색
    private BooleanExpression searchCondition(PostCondition condition) {
        if (hasText(condition.getSelected())) {
            if(hasText(condition.getQuery())) {
                if(condition.getSelected().equals("title")) {
                    return post.title.contains(condition.getQuery());
                }else if(condition.getSelected().equals("content")) {
                    return post.content.contains(condition.getQuery());
                }else if(condition.getSelected().equals("writerId")) {
                    return post.member.userId.contains(condition.getQuery());
                } else {
                    throw new IllegalArgumentException("잘못된 요청입니다.");
                }
            }
        }
        return null;
    }

    //내 게시글 검색
    private BooleanExpression myPostSearch(PostCondition condition) {
        if (hasText(condition.getSelected())) {
            if(hasText(condition.getQuery())) {
                if(condition.getSelected().equals("title")) {
                    return post.title.contains(condition.getQuery());
                }else if(condition.getSelected().equals("content")) {
                    return post.content.contains(condition.getQuery());
                } else {
                    throw new IllegalArgumentException("잘못된 요청입니다.");
                }
            }
        }
        return null;
    }

    //정렬
    private OrderSpecifier orderSpecifier (String query) {

        if(StringUtils.hasText(query)) {
            switch (query) {
                case "createDate": return new OrderSpecifier(Order.DESC, post.creationDate);
                case "title": return new OrderSpecifier(Order.ASC, post.title);
                case "cmtCount": return new OrderSpecifier(Order.DESC, post.commentCount);
                case "views": return new OrderSpecifier(Order.DESC, post.views);
             }
        }
        return new OrderSpecifier(Order.DESC, post.creationDate);
    }
}
