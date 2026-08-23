from dataclasses import dataclass
from datetime import datetime
from enum import Enum
from typing import Optional


class LabelSource(str, Enum):
    APPROVED = "approved"
    PROXY = "proxy"
    RULE = "rule"


class DataSplit(str, Enum):
    TRAIN = "train"
    VALIDATION = "validation"
    TEST = "test"


@dataclass(frozen=True)
class TextRecord:
    document_id: str
    text: str
    text_version: str
    timestamp: datetime
    split: DataSplit
    label: Optional[str] = None
    label_source: Optional[LabelSource] = None
    parent_document_id: Optional[str] = None
