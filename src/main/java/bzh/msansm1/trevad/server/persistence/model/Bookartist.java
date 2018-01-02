package bzh.msansm1.trevad.server.persistence.model;

import java.io.Serializable;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

/**
 * The persistent class for the BOOKARTIST database table.
 * 
 */
@Entity
@Table(name = "BOOKARTIST")
@NamedQuery(name = "Bookartist.findAll", query = "SELECT b FROM Bookartist b")
public class Bookartist implements Serializable {
    private static final long serialVersionUID = 1L;

    @EmbeddedId
    private BookartistPK id;

    // bi-directional many-to-one association to Artist
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ARTIST", nullable = false, insertable = false, updatable = false)
    private Artist artistBean;

    // bi-directional many-to-one association to Artisttype
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "TYPE")
    private Artisttype artisttype;

    // bi-directional many-to-one association to Book
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "BOOK", nullable = false, insertable = false, updatable = false)
    private Book bookBean;

    public Bookartist() {
    }

    public BookartistPK getId() {
        return this.id;
    }

    public void setId(BookartistPK id) {
        this.id = id;
    }

    public Artist getArtistBean() {
        return this.artistBean;
    }

    public void setArtistBean(Artist artistBean) {
        this.artistBean = artistBean;
    }

    public Artisttype getArtisttype() {
        return this.artisttype;
    }

    public void setArtisttype(Artisttype artisttype) {
        this.artisttype = artisttype;
    }

    public Book getBookBean() {
        return this.bookBean;
    }

    public void setBookBean(Book bookBean) {
        this.bookBean = bookBean;
    }

}