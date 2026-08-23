from datetime import datetime
from typing import Optional

import pytest

from briefly_dl.data.split_validator import SplitLeakageError, SplitValidator
from briefly_dl.data.text_record import DataSplit, TextRecord


def _rec(doc_id: str, split: DataSplit, parent: Optional[str] = None) -> TextRecord:
    return TextRecord(
        document_id=doc_id,
        text="sample",
        text_version="1",
        timestamp=datetime(2026, 8, 21),
        split=split,
        parent_document_id=parent,
    )


def test_split_validator_accepts_clean_splits() -> None:
    SplitValidator().validate(
        [
            _rec("d1", DataSplit.TRAIN),
            _rec("d2", DataSplit.TEST),
        ]
    )


def test_split_validator_rejects_cross_split_document() -> None:
    with pytest.raises(SplitLeakageError):
        SplitValidator().validate(
            [
                _rec("d1", DataSplit.TRAIN),
                _rec("d1", DataSplit.TEST),
            ]
        )


def test_split_validator_rejects_parent_leakage() -> None:
    with pytest.raises(SplitLeakageError):
        SplitValidator().validate(
            [
                _rec("parent", DataSplit.TRAIN),
                _rec("child", DataSplit.TEST, parent="parent"),
            ]
        )
