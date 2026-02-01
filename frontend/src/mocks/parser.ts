import { calculateTaxReclaim, TAX_RATES } from './tax-calculator';
import type { DividendData } from '../types/dividend.types';

const MOCK_COMPANIES = [
    { name: "AIR LIQUIDE SA", isin: "FR0000120073" },
    { name: "LVMH MOET HENNESSY", isin: "FR0000121014" },
    { name: "TOTALENERGIES SE", isin: "FR0000120271" },
    { name: "AXA SA", isin: "FR0000120628" },
    { name: "SANOFI", isin: "FR0000120578" },
    { name: "L'OREAL", isin: "FR0000120321" },
];

export async function parseDividendPDF(file: File): Promise<DividendData> {
    console.log("Parsing file:", file.name);

    // Simulate network/OCR delay with some randomness
    const delay = 1500 + Math.random() * 2000;

    // Pick a random company
    const company = MOCK_COMPANIES[Math.floor(Math.random() * MOCK_COMPANIES.length)];

    // Generate random amounts
    const grossAmount = Math.floor(100 + Math.random() * 5000);

    // Always apply progressive rate (25%) for demo purposes so we have something to reclaim
    const frenchRate = TAX_RATES.FRENCH_PROGRESSIVE;

    // Use correct tax calculation logic
    const taxCalc = calculateTaxReclaim(grossAmount, frenchRate);

    return new Promise((resolve) => {
        setTimeout(() => {
            resolve({
                securityName: company.name,
                isin: company.isin,
                grossAmount: taxCalc.grossAmount,
                currency: "EUR",
                paymentDate: new Date().toISOString().split('T')[0],
                withholdingTax: taxCalc.withholdingTax,
                reclaimableAmount: taxCalc.reclaimableAmount,
                appliedRate: taxCalc.appliedRate,
                status: 'OPEN',
            });
        }, delay);
    });
}
