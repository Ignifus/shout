package model;

import javax.persistence.*;

@Entity
@Table(name="upvotes")
public class Upvote {

    @EmbeddedId
    private UpvoteId id;

    @MapsId("userId")
    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    @MapsId("shoutId")
    @ManyToOne
    @JoinColumn(name = "shout_id", referencedColumnName = "id")
    private Shout shout;

    public Upvote(User user, Shout shout) {
        this.user = user;
        this.shout = shout;
    }

    public Upvote() {

    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Shout getShout() {
        return shout;
    }

    public void setShout(Shout shout) {
        this.shout = shout;
    }
}
