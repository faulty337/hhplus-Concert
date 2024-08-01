package com.hhp.concert.Business.Repository;

public interface RedisRepository {

    Long getSortedSetRank(String str, String string);

    void addElementSortedSet(String str, String string, double l);
}
