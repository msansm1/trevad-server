package bzh.msansm1.trevad.server.persistence.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * The primary key class for the USERALBUM database table.
 * 
 */
@Embeddable
public class UseralbumPK implements Serializable {
    // default serial version id, required for serializable classes.
    private static final long serialVersionUID = 1L;

    @Column(name = "USER", insertable = false, updatable = false, nullable = false)
    private int user;

    @Column(name = "ALBUM", insertable = false, updatable = false, nullable = false)
    private int album;

    public UseralbumPK() {
    }

    public int getUser() {
        return this.user;
    }

    public void setUser(int user) {
        this.user = user;
    }

    public int getAlbum() {
        return this.album;
    }

    public void setAlbum(int album) {
        this.album = album;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof UseralbumPK)) {
            return false;
        }
        UseralbumPK castOther = (UseralbumPK) other;
        return (this.user == castOther.user) && (this.album == castOther.album);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int hash = 17;
        hash = hash * prime + this.user;
        hash = hash * prime + this.album;

        return hash;
    }
}