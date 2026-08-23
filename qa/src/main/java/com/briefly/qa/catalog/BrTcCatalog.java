package com.briefly.qa.catalog;

import java.util.List;
import java.util.Optional;

/** Machine-readable BR-TC catalog (DESIGNED). */
public final class BrTcCatalog {

    public enum Priority { P0, P1 }

    public enum EvidenceStatus {
        DESIGNED, PLANNED, NOT_TESTED, PASSED
    }

    public record TestCase(
            String id,
            String scenario,
            Priority priority,
            EvidenceStatus evidenceStatus,
            String relatedFr
    ) {}

    private static final List<TestCase> CASES = List.of(
            tc("BR-TC-001", "세션 고정 방지", Priority.P0, "FR-001"),
            tc("BR-TC-002", "CSRF 차단", Priority.P0, "NFR-SEC"),
            tc("BR-TC-003", "관심상품 더블클릭 UNIQUE", Priority.P0, "FR-003"),
            tc("BR-TC-004", "비정상 신청 전이 거부", Priority.P0, "FR-004/POL"),
            tc("BR-TC-005", "타인 신청 취소 403", Priority.P0, "FR-004"),
            tc("BR-TC-006", "관리자 URL 우회 403", Priority.P0, "FR-007"),
            tc("BR-TC-007", "XSS 브리프 무해화", Priority.P0, "FR-005"),
            tc("BR-TC-008", "SQL injection 차단", Priority.P0, "NFR-SEC"),
            tc("BR-TC-009", "금액 BigDecimal 정밀도", Priority.P0, "FR-004"),
            tc("BR-TC-010", "미발행 브리프 목록 제외", Priority.P1, "FR-005"),
            tc("BR-TC-011", "AI 추천/자동승인 경계", Priority.P0, "AI-SCOPE"),
            tc("BR-TC-012", "이벤트 재전송 멱등", Priority.P1, "DA"),
            tc("BR-TC-013", "위험등급 1~5", Priority.P0, "FR-002/POL"),
            tc("BR-TC-014", "Application PENDING 고정", Priority.P0, "FR-004"),
            tc("BR-TC-015", "INACTIVE Fund 목록 제외", Priority.P0, "FR-002")
    );

    private BrTcCatalog() {}

    private static TestCase tc(String id, String scenario, Priority p, String fr) {
        return new TestCase(id, scenario, p, EvidenceStatus.DESIGNED, fr);
    }

    public static List<TestCase> all() {
        return CASES;
    }

    public static List<TestCase> p0() {
        return CASES.stream().filter(c -> c.priority() == Priority.P0).toList();
    }

    public static Optional<TestCase> find(String id) {
        return CASES.stream().filter(c -> c.id().equals(id)).findFirst();
    }

    public static long designedCount() {
        return CASES.stream().filter(c -> c.evidenceStatus() == EvidenceStatus.DESIGNED).count();
    }

    public static boolean hasPassedWithoutEvidence() {
        return CASES.stream().anyMatch(c -> c.evidenceStatus() == EvidenceStatus.PASSED);
    }
}
