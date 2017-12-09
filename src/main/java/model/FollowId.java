package model;

import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
public class FollowId implements Serializable {
    int followerUserId;
    int followedUserId;
}
