# Briefly Public Data Catalog

> Evidence: **DESIGNED** — 카탈로그·매핑·사용 규칙 문서화. 실 API 연동 로그 없음 → NOT TESTED.

외부 금융 Context 수집 시 **금융위 API 우선**, KRX Marketplace는 보조.  
모든 사용은 Context/Evidence only. 자동매매·투자권유·자동 위험 확정 금지.

---

## 1. Sources

| Source | URL / ID | Key Fields (대표) | Update / Notes | Mapping | Usage Rule |
| --- | --- | --- | --- | --- | --- |
| 금융위원회_주식시세정보 | https://www.data.go.kr/data/15094808/openapi.do · `apis.data.go.kr/1160100/service/GetStockSecuritiesInfoService` | `basDt`, `srtnCd`, `isinCd`, `itmsNm`, `mrktCtg`, `clpr`, `vs`, `fltRt`, `mkp`, `hipr`, `lopr`, `trqu`, `trPrc` | 일 1회 (영업일 기준일+1 오후 업데이트 가능). 개발 10,000 트래픽 | Product / Security → `srtnCd` 또는 `isinCd` | Context only. 화면 표시 시 `source_reference_date` + `fetched_at` 구분. |
| 금융위원회_KRX상장종목정보 | https://www.data.go.kr/data/15094775/openapi.do (`publicDataPk=15094775`) | `basDt`, `srtnCd`, `isinCd`, `mrktCtg`, `itmsNm`, `corpNm`, `corpRegnNo` | 일 1회 | Master: ISIN / 단축코드 / 법인등록번호 canonical | Security Master. 시세·공시 조인 키. |
| 금융위원회 기업기본정보 / 주식발행정보 등 | data.go.kr 금융위 관련 OpenAPI (예: 15043184, 15043423) | 법인등록번호, 종목명, 발행주식수, 상장일, 액면가 | 일 1회 | Company Master | 기업 Context. Brief 근거용. |
| OPEN DART 공시검색 / 주요정보 | https://opendart.fss.or.kr/ (`/api/list.json` 등, 인증키 필요) | `corp_code`, `corp_name`, `stock_code`, `report_nm`, `rcept_no`, `rcept_dt`, `pblntf_ty` | 공시 발생 시 준실시간 | `corp_code` / `stock_code` → Company / Security | Evidence only. 자동 위험 확정 금지. 원문 링크 권장. |
| KRX Data Marketplace OPEN API (보조) | https://openapi.krx.co.kr/ | 일별매매정보, 종목기본정보, 지수 시세 | 서비스별 상이 | 보조 검증 / 확장 | 금융위 API 우선. 라이선스·트래픽 확인 후 사용. |

---

## 2. Canonical Keys

| Domain | Canonical | Alternates | Notes |
| --- | --- | --- | --- |
| Security | `isin` (ISIN) | `srtn_cd` (단축코드), 내부 `security_id` | ISIN 우선. 없으면 srtn_cd로 provisional id |
| Company | `corp_code` (DART) | `corp_regn_no` (법인등록번호) | 공시 조인은 corp_code |
| Market Snapshot | `(security_id, base_date)` | — | Idempotent upsert |
| Disclosure | `(corp_code, rcept_no)` | — | Dedup 100% |

---

## 3. Provenance Fields (필수)

모든 external raw/mart row:

| Field | Meaning |
| --- | --- |
| `source_dataset` | 카탈로그 source id (예: `FSC_STOCK_PRICE`) |
| `source_name` | 사람이 읽는 출처명 |
| `source_url` | 수집 endpoint 또는 data.go.kr 페이지 |
| `base_date` / `reference_date` | 데이터 기준일 (시세 basDt, 공시 rcept_dt) |
| `fetched_at` | 실제 수집 시각 (UTC 권장) |
| `checksum` | raw payload SHA-256 |
| `pipeline_run_id` | 실행 단위 |

**주의:** 금융위 API는 보유기관(KRX 등) 연계 후 제공. 실시간이 아니며, **기준일자(`base_date`)와 `fetched_at`을 반드시 구분** 저장·표시한다.

---

## 4. Catalog Enum (코드)

`com.briefly.de.catalog.DataSourceCatalog` 와 동기화:

| Code | Layer target |
| --- | --- |
| `FSC_STOCK_PRICE` | raw → staging market → mart market_snapshot |
| `FSC_KRX_LISTED` | raw → staging security_master → mart dim_security |
| `FSC_COMPANY_INFO` | raw → staging company → mart dim_company |
| `OPENDART_DISCLOSURE` | raw → staging disclosure → mart fact_disclosure |
| `KRX_OPENAPI_AUX` | optional reconcile |

---

## 5. Out of Scope

- 실시간 HFT 시세
- 자동 매매 신호 생성
- 개인 계좌 연동
- 투자 권유·자동 위험 grade 확정
