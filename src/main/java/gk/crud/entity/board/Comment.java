package gk.crud.entity.board;

import gk.crud.entity.BaseEntity;
import gk.crud.entity.member.Member;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Comment extends BaseEntity {

    @Id @GeneratedValue(generator = "comment_id_seq")
    @Column(name = "comment_id")
    private Long id;

    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    public Comment(String content, Post post, Member member) {
        this.content = content;
        this.post = post;
        this.member = member;
    }

    public void create() {
        post.getComments().add(this);
        member.getComments().add(this);
        post.plusCommentCount();
    }

    public void removeComment() {
        post.minusCommentCount();
        post.getComments().remove(this);
        member.getComments().remove(this);
    }

    public void updateComment(Comment commentUpdateParam) {
        this.content = commentUpdateParam.getContent();
    }
}
