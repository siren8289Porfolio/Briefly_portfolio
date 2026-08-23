from dataclasses import dataclass, field
from datetime import datetime, timezone
from typing import Any, Dict, List, Optional
from uuid import uuid4


@dataclass
class ProvenanceRecord:
    """AI-R2: model, prompt, retrieval sources, timestamp."""

    run_id: str
    feature_key: str
    model_name: str
    model_version: str
    prompt_version: Optional[str]
    retrieval_sources: List[Dict[str, Any]]
    created_at: datetime
    confidence: Optional[float] = None

    def to_dict(self) -> Dict[str, Any]:
        return {
            "run_id": self.run_id,
            "feature_key": self.feature_key,
            "model_name": self.model_name,
            "model_version": self.model_version,
            "prompt_version": self.prompt_version,
            "retrieval_sources": self.retrieval_sources,
            "created_at": self.created_at.isoformat(),
            "confidence": self.confidence,
        }


class ProvenanceLogger:
    def __init__(self) -> None:
        self._records: List[ProvenanceRecord] = []

    def start_run(
        self,
        feature_key: str,
        model_name: str,
        model_version: str,
        prompt_version: Optional[str] = None,
        retrieval_sources: Optional[List[Dict[str, Any]]] = None,
        confidence: Optional[float] = None,
    ) -> ProvenanceRecord:
        record = ProvenanceRecord(
            run_id=str(uuid4()),
            feature_key=feature_key,
            model_name=model_name,
            model_version=model_version,
            prompt_version=prompt_version,
            retrieval_sources=list(retrieval_sources or []),
            created_at=datetime.now(timezone.utc),
            confidence=confidence,
        )
        self._records.append(record)
        return record

    def all_records(self) -> List[ProvenanceRecord]:
        return list(self._records)
