package gk.crud.entity.board;

import gk.crud.entity.BaseEntity;
import gk.crud.entity.member.Member;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Post extends BaseEntity {

    @Id @GeneratedValue(generator = "post_id_seq")
    @Column(name = "post_id")
    private Long id;

    private String title;
    private String content;
    private int views;
    private int commentCount;
    private int likeCount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @OneToMany(mappedBy = "post", cascade = CascadeType.REMOVE)
    private List<Comment> comments = new ArrayList<>();

    @OneToMany(mappedBy = "post", cascade = CascadeType.REMOVE)
    private List<Likes> likes = new ArrayList<>();

    public Post(String title, String content, Member member) {
        this.title = title;
        this.content = content;
        this.member = member;
        member.getPosts().add(this);
        views = 0;
        commentCount = 0;
        likeCount = 0;
    }

    public void plusViews() {
        views++;
    }

    public void plusCommentCount() {
        commentCount++;
    }

    public void minusCommentCount() {
        commentCount--;
    }

    public void plusLikeCount() {
        likeCount++;
    }

    public void minusLikeCount() {
        likeCount--;
    }

    public void updatePost(Post post) {
        this.title = post.getTitle();
        this.content = post.getContent();
    }
}
