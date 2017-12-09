package model;

import javax.persistence.*;

@Entity
@Table(name="follows")
public class Follow {
    @EmbeddedId
    private FollowId id;

    @MapsId("followerUserId")
    @ManyToOne
    @JoinColumn(name = "follower_user_id", referencedColumnName = "id")
    private User followerUser;

    @MapsId("followedUserId")
    @ManyToOne
    @JoinColumn(name = "followed_user_id", referencedColumnName = "id")
    private User followedUser;

    public Follow(User followerUser, User followedUser) {
        this.followerUser = followerUser;
        this.followedUser = followedUser;
    }

    public Follow() {

    }

    public FollowId getId() {
        return id;
    }

    public void setId(FollowId id) {
        this.id = id;
    }

    public User getFollowerUser() {
        return followerUser;
    }

    public void setFollowerUser(User followerUser) {
        this.followerUser = followerUser;
    }

    public User getFollowedUser() {
        return followedUser;
    }

    public void setFollowedUser(User followedUser) {
        this.followedUser = followedUser;
    }
}
