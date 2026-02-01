export interface Form5001Data {
  // Personal information
  taxpayerName: string;
  taxId: string;
  address: string;
  city: string;
  postalCode: string;
  country: string;

  // Tax period
  taxYear: number;

  // Dividends list
  dividends: DividendEntry[];

  // Totals
  totalGrossAmount: number;
  totalWithholdingTax: number;
  totalTreatyAmount: number;
  totalReclaimableAmount: number;
}

export interface DividendEntry {
  securityName: string;
  isin: string;
  paymentDate: string;
  grossAmount: number;
  currency: string;
  withholdingTax: number;
  treatyAmount: number;
  reclaimableAmount: number;
  appliedRate: number;
}

export interface FormGenerationResponse {
  formId: string;
  pdfUrl?: string; // Legacy/Fallback
  pdf5000Url?: string; // URL for Form 5000
  pdf5001Url?: string; // URL for Form 5001
  zipUrl?: string; // URL for the ZIP archive (download)
  fileName: string;
  generatedAt: string;
}

export interface Form5000Data {
  // Personal information
  taxpayerName: string;
  profession: string;
  address: string;
  city: string;
  postalCode: string;
  country: string;
  email?: string;

  // Tax information
  taxId: string;
  residenceCountry: string; // "Suisse"
  taxYear: number;

  // Declaration date
  declarationDate: string;
  declarationPlace: string;

  // Banking information for refund
  iban?: string;
  swift?: string;
  bankName?: string;
}

export interface FormGenerationRequest {
  formData: Form5001Data;
  includeForm5000: boolean; // Generate both 5000 and 5001
}
