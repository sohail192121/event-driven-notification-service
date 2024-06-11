# Prototype boundaries

The sender executes inside a local lock. Failed sends consume rate capacity but remain retryable. Crash-after-send deduplication needs provider idempotency and a durable delivery ledger.
