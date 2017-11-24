package bzh.msansm1.trevad.server.persistence.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * The primary key class for the USERTV database table.
 * 
 */
@Embeddable
public class UsertvPK implements Serializable {
    // default serial version id, required for serializable classes.
    private static final long serialVersionUID = 1L;

    @Column(name = "USER", insertable = false, updatable = false, nullable = false)
    private int user;

    @Column(name = "TVSHOW", insertable = false, updatable = false, nullable = false)
    private int tvshow;

    public UsertvPK() {
    }

    public int getUser() {
        return this.user;
    }

    public void setUser(int user) {
        this.user = user;
    }

    public int getTvshow() {
        return this.tvshow;
    }

    public void setTvshow(int tvshow) {
        this.tvshow = tvshow;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof UsertvPK)) {
            return false;
        }
        UsertvPK castOther = (UsertvPK) other;
        return (this.user == castOther.user) && (this.tvshow == castOther.tvshow);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int hash = 17;
        hash = hash * prime + this.user;
        hash = hash * prime + this.tvshow;

        return hash;
    }
}