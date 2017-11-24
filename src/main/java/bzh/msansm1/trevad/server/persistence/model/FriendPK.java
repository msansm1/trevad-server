package bzh.msansm1.trevad.server.persistence.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * The primary key class for the FRIEND database table.
 * 
 */
@Embeddable
public class FriendPK implements Serializable {
    // default serial version id, required for serializable classes.
    private static final long serialVersionUID = 1L;

    @Column(name = "USER", insertable = false, updatable = false, nullable = false)
    private int user;

    @Column(name = "FRIEND", insertable = false, updatable = false, nullable = false)
    private int friend;

    public FriendPK() {
    }

    public int getUser() {
        return this.user;
    }

    public void setUser(int user) {
        this.user = user;
    }

    public int getFriend() {
        return this.friend;
    }

    public void setFriend(int friend) {
        this.friend = friend;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof FriendPK)) {
            return false;
        }
        FriendPK castOther = (FriendPK) other;
        return (this.user == castOther.user) && (this.friend == castOther.friend);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int hash = 17;
        hash = hash * prime + this.user;
        hash = hash * prime + this.friend;

        return hash;
    }
}