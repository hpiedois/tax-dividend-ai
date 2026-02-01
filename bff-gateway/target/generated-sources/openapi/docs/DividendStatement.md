

# DividendStatement


## Properties

| Name | Type | Description | Notes |
|------------ | ------------- | ------------- | -------------|
|**id** | **UUID** |  |  [optional] |
|**userId** | **UUID** |  |  [optional] |
|**sourceFileName** | **String** | Original filename of uploaded broker statement |  [optional] |
|**sourceFileS3Key** | **String** | S3/MinIO key for stored file |  [optional] |
|**broker** | **String** | Broker name (InteractiveBrokers, Swissquote, etc.) |  [optional] |
|**periodStart** | **LocalDate** | Start of period covered by statement |  [optional] |
|**periodEnd** | **LocalDate** | End of period covered by statement |  [optional] |
|**status** | **DividendStatementStatus** |  |  [optional] |
|**parsedAt** | **OffsetDateTime** |  |  [optional] |
|**validatedAt** | **OffsetDateTime** |  |  [optional] |
|**sentAt** | **OffsetDateTime** |  |  [optional] |
|**sentMethod** | **String** | Submission method (EMAIL, POSTAL, ONLINE) |  [optional] |
|**sentNotes** | **String** | User notes about submission |  [optional] |
|**paidAt** | **OffsetDateTime** |  |  [optional] |
|**paidAmount** | **BigDecimal** | Amount received from tax authority |  [optional] |
|**parsedBy** | **String** | Who/what triggered parsing (AI_AGENT, MANUAL, etc.) |  [optional] |
|**dividendCount** | **Integer** | Number of dividends extracted from statement |  [optional] |
|**totalGrossAmount** | **BigDecimal** | Total gross amount from all dividends |  [optional] |
|**totalReclaimable** | **BigDecimal** | Total reclaimable amount from all dividends |  [optional] |
|**createdAt** | **OffsetDateTime** |  |  [optional] |
|**updatedAt** | **OffsetDateTime** |  |  [optional] |



