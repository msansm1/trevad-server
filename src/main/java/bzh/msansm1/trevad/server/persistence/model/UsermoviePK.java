package bzh.msansm1.trevad.server.persistence.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * The primary key class for the USERMOVIE database table.
 * 
 */
@Embeddable
public class UsermoviePK implements Serializable {
    // default serial version id, required for serializable classes.
    private static final long serialVersionUID = 1L;

    @Column(name = "USER", insertable = false, updatable = false, nullable = false)
    private int user;

    @Column(name = "MOVIE", insertable = false, updatable = false, nullable = false)
    private int movie;

    public UsermoviePK() {
    }

    public int getUser() {
        return this.user;
    }

    public void setUser(int user) {
        this.user = user;
    }

    public int getMovie() {
        return this.movie;
    }

    public void setMovie(int movie) {
        this.movie = movie;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof UsermoviePK)) {
            return false;
        }
        UsermoviePK castOther = (UsermoviePK) other;
        return (this.user == castOther.user) && (this.movie == castOther.movie);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int hash = 17;
        hash = hash * prime + this.user;
        hash = hash * prime + this.movie;

        return hash;
    }
}