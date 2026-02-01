export interface DividendData {
  securityName: string;
  isin: string;
  grossAmount: number;
  currency: string;
  paymentDate: string;
  withholdingTax: number;
  reclaimableAmount: number;
  appliedRateType?: string; // Tracks which rate was applied (PFU or progressive)
  status: 'OPEN' | 'SENT' | 'PAID';
  id?: string;
  appliedRate?: number;
}
