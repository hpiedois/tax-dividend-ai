## tax-dividend-api@1.0.0

This generator creates TypeScript/JavaScript client that utilizes [axios](https://github.com/axios/axios). The generated Node module can be used in the following environments:

Environment
* Node.js
* Webpack
* Browserify

Language level
* ES5 - you must have a Promises/A+ library installed
* ES6

Module system
* CommonJS
* ES6 module system

It can be used in both TypeScript and JavaScript. In TypeScript, the definition will be automatically resolved via `package.json`. ([Reference](https://www.typescriptlang.org/docs/handbook/declaration-files/consumption.html))

### Building

To build and compile the typescript sources to javascript use:
```
npm install
npm run build
```

### Publishing

First build the package then run `npm publish`

### Consuming

navigate to the folder of your consuming project and run one of the following commands.

_published:_

```
npm install tax-dividend-api@1.0.0 --save
```

_unPublished (not recommended):_

```
npm install PATH_TO_GENERATED_PACKAGE --save
```

### Documentation for API Endpoints

All URIs are relative to *http://localhost:8080/api*

Class | Method | HTTP request | Description
------------ | ------------- | ------------- | -------------
*AuthApi* | [**register**](docs/AuthApi.md#register) | **POST** /auth/register | Register a new user
*AuthApi* | [**verifyEmail**](docs/AuthApi.md#verifyemail) | **POST** /auth/verify | Verify email
*DividendsApi* | [**getDividendHistory**](docs/DividendsApi.md#getdividendhistory) | **GET** /dividends/history | Get dividend history
*DividendsApi* | [**getDividendStats**](docs/DividendsApi.md#getdividendstats) | **GET** /dividends/stats | Get dividend statistics
*DividendsApi* | [**parseDividendStatement**](docs/DividendsApi.md#parsedividendstatement) | **POST** /dividends/parse-statement | Parse a dividend statement (PDF/Image)
*DividendsApi* | [**updateDividendStatus**](docs/DividendsApi.md#updatedividendstatus) | **PATCH** /dividends/status | Update status for multiple dividends
*FormsApi* | [**deleteForm**](docs/FormsApi.md#deleteform) | **DELETE** /forms/{id} | Delete form
*FormsApi* | [**downloadForm**](docs/FormsApi.md#downloadform) | **GET** /forms/{id}/download | Download form
*FormsApi* | [**generateTaxForms**](docs/FormsApi.md#generatetaxforms) | **POST** /forms/generate | Generate tax forms
*FormsApi* | [**getForm**](docs/FormsApi.md#getform) | **GET** /forms/{id} | Get form metadata
*FormsApi* | [**getFormDownloadUrl**](docs/FormsApi.md#getformdownloadurl) | **GET** /forms/{id}/download-url | Get pre-signed download URL for form
*FormsApi* | [**listForms**](docs/FormsApi.md#listforms) | **GET** /forms | List user\&#39;s forms


### Documentation For Models

 - [DividendCase](docs/DividendCase.md)
 - [DividendData](docs/DividendData.md)
 - [DividendHistoryResponse](docs/DividendHistoryResponse.md)
 - [DividendStats](docs/DividendStats.md)
 - [FormDownloadUrlResponse](docs/FormDownloadUrlResponse.md)
 - [GenerateTaxFormsRequest](docs/GenerateTaxFormsRequest.md)
 - [GenerateTaxFormsResponse](docs/GenerateTaxFormsResponse.md)
 - [GeneratedForm](docs/GeneratedForm.md)
 - [ParseStatementResponse](docs/ParseStatementResponse.md)
 - [ParseStatementResponseMetadata](docs/ParseStatementResponseMetadata.md)
 - [RegisterRequest](docs/RegisterRequest.md)
 - [UpdateDividendStatusRequest](docs/UpdateDividendStatusRequest.md)


<a id="documentation-for-authorization"></a>
## Documentation For Authorization


Authentication schemes defined for the API:
<a id="bearerAuth"></a>
### bearerAuth

- **Type**: Bearer authentication (JWT)

