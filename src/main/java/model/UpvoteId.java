package model;

import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
public class UpvoteId implements Serializable {
    int userId;
    int shoutId;
}
