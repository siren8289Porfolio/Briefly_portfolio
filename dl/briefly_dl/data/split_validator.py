from collections import defaultdict
from typing import Iterable, List, Set

from briefly_dl.data.text_record import DataSplit, TextRecord


class SplitLeakageError(Exception):
    pass


class SplitValidator:
    """Ensures no document_id / parent_id leakage across train/val/test."""

    def validate(self, records: Iterable[TextRecord]) -> None:
        by_split: dict[DataSplit, Set[str]] = defaultdict(set)
        id_to_split: dict[str, DataSplit] = {}

        for record in records:
            doc_key = record.document_id
            if doc_key in id_to_split and id_to_split[doc_key] != record.split:
                raise SplitLeakageError(
                    f"document_id {doc_key} appears in multiple splits"
                )
            id_to_split[doc_key] = record.split
            by_split[record.split].add(doc_key)

            if record.parent_document_id:
                parent = record.parent_document_id
                if parent in id_to_split and id_to_split[parent] != record.split:
                    raise SplitLeakageError(
                        f"derived document {doc_key} leaks from parent {parent} split"
                    )

    def find_duplicates(self, document_ids: Iterable[str]) -> List[str]:
        seen: set[str] = set()
        dups: list[str] = []
        for doc_id in document_ids:
            if doc_id in seen:
                dups.append(doc_id)
            seen.add(doc_id)
        return dups
