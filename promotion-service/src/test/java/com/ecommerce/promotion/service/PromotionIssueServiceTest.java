package com.ecommerce.promotion.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.ecommerce.commonlib.exception.BusinessException;
import com.ecommerce.promotion.config.PromotionIssueProperties;
import com.ecommerce.promotion.repository.PromotionIssueRepository;
import com.ecommerce.promotion.repository.PromotionRepository;
import com.ecommerce.promotion.service.CouponStockStore.ClaimResult;
import com.ecommerce.promotion.service.CouponWindowCache.CouponWindow;
import java.time.Instant;
import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.dao.QueryTimeoutException;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.transaction.PlatformTransactionManager;

class PromotionIssueServiceTest {

    private static final Long PROMOTION_ID = 7L;
    private static final String USER_ID = "user-1";

    private final PromotionRepository promotionRepository = mock(PromotionRepository.class);
    private final PromotionIssueRepository promotionIssueRepository = mock(PromotionIssueRepository.class);
    private final CouponStockStore store = mock(CouponStockStore.class);
    private final CouponWindowCache windowCache = mock(CouponWindowCache.class);
    private PromotionIssueService service;

    @BeforeEach
    void setUp() {
        PromotionIssueProperties properties = new PromotionIssueProperties();
        properties.setStore(PromotionIssueProperties.Store.REDIS);
        service = new PromotionIssueService(promotionRepository, promotionIssueRepository, store,
                windowCache, properties, mock(PlatformTransactionManager.class));
        Instant now = Instant.now();
        when(windowCache.get(PROMOTION_ID)).thenReturn(Optional.of(
                new CouponWindow(PROMOTION_ID, true, 10, now.minusSeconds(60), now.plusSeconds(60))));
        Jwt jwt = Jwt.withTokenValue("t").header("alg", "none").subject(USER_ID).build();
        SecurityContextHolder.getContext().setAuthentication(new JwtAuthenticationToken(jwt));
    }

    @AfterEach
    void clearSecurity() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void redisOutageStopsIssuingWith503() {
        when(store.tryClaim(PROMOTION_ID, USER_ID)).thenThrow(new RedisConnectionFailureException("down"));

        BusinessException ex = assertThrows(BusinessException.class, () -> service.issue(PROMOTION_ID));

        assertEquals("PRM-6004", ex.getErrorCode());
        assertEquals(503, ex.getStatus().value());
        verify(promotionIssueRepository, never()).saveAndFlush(any());
    }

    @Test
    void redisTimeoutWhileRebuildingAlsoReturns503() {
        when(store.tryClaim(PROMOTION_ID, USER_ID)).thenReturn(ClaimResult.NOT_LOADED);
        when(store.loadIfAbsent(PROMOTION_ID, 10)).thenThrow(new QueryTimeoutException("slow"));

        BusinessException ex = assertThrows(BusinessException.class, () -> service.issue(PROMOTION_ID));

        assertEquals("PRM-6004", ex.getErrorCode());
    }

    @Test
    void failedMysqlWriteGivesTheClaimBack() {
        when(store.tryClaim(PROMOTION_ID, USER_ID)).thenReturn(ClaimResult.CLAIMED);
        when(promotionIssueRepository.saveAndFlush(any())).thenThrow(new IllegalStateException("db down"));

        assertThrows(IllegalStateException.class, () -> service.issue(PROMOTION_ID));

        verify(store).undoClaim(PROMOTION_ID, USER_ID, true);
    }

    @Test
    void soldOutAndAlreadyClaimedHaveTheirOwnCodes() {
        when(store.tryClaim(PROMOTION_ID, USER_ID)).thenReturn(ClaimResult.SOLD_OUT);
        assertEquals("PRM-6002",
                assertThrows(BusinessException.class, () -> service.issue(PROMOTION_ID)).getErrorCode());

        when(store.tryClaim(PROMOTION_ID, USER_ID)).thenReturn(ClaimResult.ALREADY_CLAIMED);
        assertEquals("PRM-6003",
                assertThrows(BusinessException.class, () -> service.issue(PROMOTION_ID)).getErrorCode());
    }

    @Test
    void closedCouponIsNotOpen() {
        Instant now = Instant.now();
        when(windowCache.get(PROMOTION_ID)).thenReturn(Optional.of(
                new CouponWindow(PROMOTION_ID, true, 10, now.plusSeconds(3600), now.plusSeconds(7200))));

        BusinessException ex = assertThrows(BusinessException.class, () -> service.issue(PROMOTION_ID));

        assertEquals("PRM-6001", ex.getErrorCode());
        verify(store, never()).tryClaim(any(), any());
    }
}
