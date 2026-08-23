from dataclasses import dataclass


@dataclass(frozen=True)
class TokenizerConfig:
    version: str
    max_length: int
    truncation: str = "tail"  # tail | head
    oov_token: str = "<UNK>"

    def __post_init__(self) -> None:
        if self.max_length <= 0:
            raise ValueError("max_length must be positive")
        if self.truncation not in ("tail", "head"):
            raise ValueError("truncation must be tail or head")
