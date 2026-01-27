export interface DividendData {
  securityName: string;
  isin: string;
  grossAmount: number;
  currency: string;
  paymentDate: string;
  withholdingTax: number;
  reclaimableAmount: number;
  frenchRate: number; // Tracks which rate was applied (PFU or progressive)
}
