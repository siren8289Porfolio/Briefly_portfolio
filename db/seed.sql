USE briefly;

-- Admin: admin@briefly.com / admin1234
-- User:  user@briefly.com / admin1234
INSERT INTO users (email, password_hash, name, role) VALUES
('admin@briefly.com', '$2a$10$Se7PRgezlXzKE50LqJbL7uLhRIPoNbAVHYO3i13ZZomv46OSGRrQq', '관리자', 'ADMIN'),
('user@briefly.com',  '$2a$10$Se7PRgezlXzKE50LqJbL7uLhRIPoNbAVHYO3i13ZZomv46OSGRrQq', '일반사용자', 'USER');

INSERT INTO funds (name, description, risk_grade, expected_return, status) VALUES
('Briefly 글로벌 성장 펀드', '글로벌 성장주에 분산 투자하는 중위험 상품입니다.', 3, 8.50, 'ACTIVE'),
('Briefly 안정 채권 펀드', '국내 우량 채권 중심의 저위험 상품입니다.', 2, 4.20, 'ACTIVE'),
('Briefly 테크 혁신 펀드', '기술 혁신 섹터에 집중 투자하는 고위험 상품입니다.', 4, 12.30, 'ACTIVE');

INSERT INTO fund_reports (fund_id, title, content, report_date) VALUES
(1, '2026년 1분기 운용 브리프', '글로벌 성장주 비중을 유지하며 변동성 대비 분산을 강화했습니다.', '2026-03-31'),
(2, '2026년 1분기 운용 브리프', '금리 환경을 반영해 듀레이션을 단축했습니다.', '2026-03-31');

INSERT INTO risk_alerts (fund_id, title, message, previous_grade, new_grade) VALUES
(3, '위험등급 변경 알림', '테크 섹터 변동성 확대로 위험등급이 상향 조정되었습니다.', 3, 4);
