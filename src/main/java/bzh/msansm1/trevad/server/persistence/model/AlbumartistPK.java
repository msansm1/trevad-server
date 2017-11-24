package bzh.msansm1.trevad.server.persistence.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * The primary key class for the ALBUMARTIST database table.
 * 
 */
@Embeddable
public class AlbumartistPK implements Serializable {
    // default serial version id, required for serializable classes.
    private static final long serialVersionUID = 1L;

    @Column(name = "ALBUM", insertable = false, updatable = false, nullable = false)
    private int album;

    @Column(name = "ARTIST", insertable = false, updatable = false, nullable = false)
    private int artist;

    public AlbumartistPK() {
    }

    public int getAlbum() {
        return this.album;
    }

    public void setAlbum(int album) {
        this.album = album;
    }

    public int getArtist() {
        return this.artist;
    }

    public void setArtist(int artist) {
        this.artist = artist;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof AlbumartistPK)) {
            return false;
        }
        AlbumartistPK castOther = (AlbumartistPK) other;
        return (this.album == castOther.album) && (this.artist == castOther.artist);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int hash = 17;
        hash = hash * prime + this.album;
        hash = hash * prime + this.artist;

        return hash;
    }
}