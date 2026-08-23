# Evidence Pack Template

Copy this folder to `qa/evidence/<run_id>/` when executing tests.

```text
<run_id>/
├── META.json          # build_sha, env, tester, started_at, finished_at
├── results.csv        # case_id, status, notes
├── logs/              # server / test logs
├── screenshots/       # E2E
├── security/          # ZAP / manual WSTG notes
└── waivers/           # approved Critical/High waivers if any
```

## META.json example

```json
{
  "run_id": "qa-20260823-001",
  "build_sha": "REPLACE",
  "environment": "local",
  "tester": "REPLACE",
  "status": "NOT_TESTED"
}
```

**Rule:** `status=PASSED` requires non-empty results + artifacts.
