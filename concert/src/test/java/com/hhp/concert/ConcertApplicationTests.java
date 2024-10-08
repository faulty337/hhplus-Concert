package com.hhp.concert;

import com.hhp.concert.Infrastructure.DBRepository.concert.ConcertJpaRepository;
import com.hhp.concert.util.TestDatabaseManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@ActiveProfiles("test")
class ConcertApplicationTests {

	@Autowired
	private TestDatabaseManager testDatabaseManager;
	@Autowired
	private ConcertJpaRepository concertJpaRepository;

	@Test
	void contextLoads() {
	}



}
