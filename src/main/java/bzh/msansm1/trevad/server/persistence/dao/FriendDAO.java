package bzh.msansm1.trevad.server.persistence.dao;

import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.persistence.TypedQuery;

import bzh.msansm1.trevad.server.json.friend.JsonFriend;
import bzh.msansm1.trevad.server.persistence.model.Friend;
import bzh.msansm1.trevad.server.persistence.model.FriendPK;

/**
 * DAO for FRIEND table
 * 
 * @author msansm1
 *
 */
@RequestScoped
public class FriendDAO extends Dao {

    public void updateFriend(Friend friend) {
        em.merge(friend);
    }

    public void saveFriend(Friend friend) {
        em.persist(friend);
    }

    public void removeFriend(Friend friend) {
        em.remove(friend);
        em.flush();
    }

    public List<Friend> getFriends() {
        return em.createQuery("from Friend", Friend.class).getResultList();
    }

    public Friend getFriend(FriendPK id) {
        return em.find(Friend.class, id);
    }

    public List<JsonFriend> getFriendsForUser(Integer userId) {
        TypedQuery<JsonFriend> q = em
                .createQuery("SELECT NEW bzh.msansm1.trevad.server.json.friend.JsonFriend(f.id.user, f.id.friend, "
                        + "f.user1.login, f.isaccepted, f.issharedcollection)" + "from Friend f "
                        + "where f.id.user=:userId", JsonFriend.class);
        q.setParameter("userId", userId);
        return q.getResultList();
    }

    public JsonFriend getFriendForUser(Integer userId, Integer friendId) {
        TypedQuery<JsonFriend> q = em
                .createQuery("SELECT NEW bzh.msansm1.trevad.server.json.friend.JsonFriend(f.id.user, f.id.friend, "
                        + "f.user1.login, f.isaccepted, f.issharedcollection)" + "from Friend f "
                        + "where f.id.user=:userId and f.id.friend=:friendId", JsonFriend.class);
        q.setParameter("userId", userId);
        q.setParameter("friendId", friendId);
        List<JsonFriend> res = q.getResultList();
        if (!res.isEmpty()) {
            return res.get(0);
        }
        return null;
    }

    public List<JsonFriend> getFriendsListForUser(Integer userId, int from, int limit, String orderBy,
            String orderDir) {
        String dir = "DESC";
        if (orderDir != null) {
            dir = orderDir;
        }
        return em
                .createQuery("SELECT NEW bzh.msansm1.trevad.server.json.friend.JsonFriend(f.id.user, f.id.friend, "
                        + "f.user1.login, f.isaccepted, f.issharedcollection)" + "from Friend f "
                        + "where f.id.user=:userId" + " ORDER BY " + orderBy + " " + dir, JsonFriend.class)
                .setParameter("userId", userId).setFirstResult(from).setMaxResults(limit).getResultList();
    }
}
