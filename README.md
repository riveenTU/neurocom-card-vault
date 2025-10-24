# Neurocom Coding Task — Card Vault

A minimal Spring Boot application to **save** and **search** cardholder information securely.

## Features
- **POST** `/api/cards` — add a card (cardholder name + PAN). PAN is validated (length + Luhn), then encrypted with **AES-GCM** using a single key and **never logged**.
- **GET** `/api/cards/search?last4=1234` — search by last 4 digits. Returns: cardholder name, **masked PAN** (**** **** 1234), and created time.
- **Frontend**: Plain **HTML + forms** (no frameworks) in `src/main/resources/static/index.html`.

## Database Structure & Rationale
Using **H2** (file mode) via JPA for simplicity. Table `cards`:

| Column         | Type        | Notes |
|----------------|-------------|------|
| id             | BIGINT PK   | Auto-generated |
| cardholderName | VARCHAR     | Not null |
| panEncrypted   | VARCHAR     | Base64( IV (12B) + AES/GCM ciphertext ), **no plaintext PAN stored** |
| panLast4       | CHAR(4)     | Indexed for fast search; storing last4 in clear is acceptable for search while preserving full PAN confidentiality |
| createdTime    | TIMESTAMP   | Insertion instant |

**Why this design?**
- Support efficient **search by last 4** via index on `panLast4` without decrypting every row.
- **Encryption-at-rest** for PAN using AES-GCM with a **single symmetric key** as requested. Key read from `APP_AES_KEY_B64` (Base64 256-bit). Dev fallback generates a temp key (not for prod).
- **No plaintext PAN** persisted or logged. Responses return **masked** PAN only.

> For production: switch H2 to Postgres/MySQL, manage keys via a secrets manager (e.g., AWS KMS), add audit logs & rate limiting.

## Run (Dev)
- Requirements: JDK 17, Maven
- Optional: set an AES key (Base64-encoded 256-bit):
```
export APP_AES_KEY_B64=$(openssl rand -base64 32)
```
- Build & run:
```
mvn spring-boot:run
```
- Open: http://localhost:8080/

## Example Requests
```
# Add card
curl -s -X POST http://localhost:8080/api/cards \
  -H 'Content-Type: application/json' \
  -d '{"cardholderName":"Ada Lovelace","pan":"4111111111111111"}'

# Search by last 4
curl -s 'http://localhost:8080/api/cards/search?last4=1111'
```

## Notes
- Input validation uses Jakarta Validation (12–19 digits) + **Luhn** check.
- Controller and logging levels avoid printing sensitive data.
- Masking format: `**** **** 1234`.

**Author**: Riveen Usgodaarachchi • 2025-10-23
