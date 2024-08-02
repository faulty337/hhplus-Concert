package com.hhp.concert.Business.Repository;

public interface RedisRepository {

    Long getSortedSetRank(String str, String string);

    void addElementSortedSet(String str, String string, double l);

    long getSortedSetSize(String str);

    String getFirstElement(String str);

    void removeElementSortedSet(String str, String token);

    boolean isElementInSortedSet(String str, String token);
}
