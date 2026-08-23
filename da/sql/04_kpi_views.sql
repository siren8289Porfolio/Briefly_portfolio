-- KPI views (DESIGNED)
-- Denominator zero → NULL (N/A in reports)

CREATE OR REPLACE VIEW v_kpi_interest_mockjoin_cvr AS
SELECT
    d.full_date AS metric_date,
    p.product_id,
    p.risk_level,
    COUNT(DISTINCT CASE WHEN e.event_name = 'product_interest_add' THEN e.user_id_hash END) AS interests,
    COUNT(DISTINCT CASE WHEN e.event_name = 'mock_join_submit' THEN e.user_id_hash END) AS mock_joins,
    CASE
        WHEN COUNT(DISTINCT CASE WHEN e.event_name = 'product_interest_add' THEN e.user_id_hash END) = 0
        THEN NULL
        ELSE COUNT(DISTINCT CASE WHEN e.event_name = 'mock_join_submit' THEN e.user_id_hash END)
             / COUNT(DISTINCT CASE WHEN e.event_name = 'product_interest_add' THEN e.user_id_hash END)
    END AS interest_mockjoin_cvr
FROM dim_date d
LEFT JOIN fact_user_engagement e ON e.date_key = d.date_key
LEFT JOIN dim_product p ON p.product_id = e.product_id
GROUP BY d.full_date, p.product_id, p.risk_level;

CREATE OR REPLACE VIEW v_kpi_brief_view_rate AS
SELECT
    d.full_date AS metric_date,
    COUNT(DISTINCT CASE WHEN e.event_name = 'brief_view' THEN e.event_id END) AS brief_views,
    COUNT(DISTINCT e.user_id_hash) AS active_users,
    CASE
        WHEN COUNT(DISTINCT e.user_id_hash) = 0 THEN NULL
        ELSE COUNT(DISTINCT CASE WHEN e.event_name = 'brief_view' THEN e.event_id END)
             / COUNT(DISTINCT e.user_id_hash)
    END AS brief_view_rate
FROM dim_date d
LEFT JOIN fact_user_engagement e ON e.date_key = d.date_key
GROUP BY d.full_date;

CREATE OR REPLACE VIEW v_kpi_market_data_coverage AS
SELECT
    d.full_date AS metric_date,
    (SELECT COUNT(*) FROM dim_product WHERE status = 'ACTIVE') AS active_products,
    COUNT(DISTINCT m.security_id) AS products_with_price,
    CASE
        WHEN (SELECT COUNT(*) FROM dim_product WHERE status = 'ACTIVE') = 0 THEN NULL
        ELSE COUNT(DISTINCT m.security_id)
             / (SELECT COUNT(*) FROM dim_product WHERE status = 'ACTIVE')
    END AS market_data_coverage
FROM dim_date d
LEFT JOIN fact_market_snapshot m ON m.base_date = d.full_date
GROUP BY d.full_date;

CREATE OR REPLACE VIEW v_kpi_brief_context_segment AS
SELECT
    d.full_date AS metric_date,
    e.context_segment,
    COUNT(*) AS brief_views,
    COUNT(DISTINCT e.user_id_hash) AS unique_viewers
FROM dim_date d
JOIN fact_user_engagement e ON e.date_key = d.date_key AND e.event_name = 'brief_view'
GROUP BY d.full_date, e.context_segment;
