/**
 * Field Checker - Identifies missing fields in form 5001
 * This tool helps identify which fields are filled and which are missing
 */

import { PDFDocument } from 'pdf-lib';

export interface FieldStatus {
  name: string;
  type: string;
  filled: boolean;
  category: 'header' | 'treaty' | 'dividend_table' | 'totals' | 'checkboxes' | 'unknown';
}

/**
 * Analyze which fields are filled and which are missing
 */
export async function checkFieldCoverage(pdfUrl: string): Promise<{
  total: number;
  filled: number;
  missing: number;
  fields: FieldStatus[];
  summary: Record<string, { total: number; filled: number }>;
}> {
  const pdfBytes = await fetch(pdfUrl).then(res => res.arrayBuffer());
  const pdfDoc = await PDFDocument.load(pdfBytes);
  const form = pdfDoc.getForm();
  const fields = form.getFields();

  // Fields that we currently fill
  const filledFieldPatterns = [
    /^a1$/,        // Creditor name (page 1)
    /^a1a$/,       // Creditor name (page 2)
    /^a3$/,        // Article number
    /^a3a$/,       // Article number (copy)
    /^a4$/,        // Country
    /^a4a$/,       // Country (copy)
    /^a5$/,        // Reduced rate % (main field)
    /^a5a$/,       // Reduced rate % (main field, copy)
    /^a55$/,       // Reduced rate % (alternative field)
    /^a55a$/,      // Reduced rate % (alternative field, copy)
    /^a6$/,        // Date and place of signature
    /^a6a$/,       // Date and place of signature (copy)
    /^cac1$/,      // Checkbox 1 (France-Switzerland convention)
    /^b\d{1,2}1$/,    // Company name (col 1)
    /^b\d{1,2}1a$/,   // Company name (col 1, page 2)
    /^b\d{1,2}2$/,    // Payment date (col 2)
    /^b\d{1,2}2a$/,   // Payment date (col 2, page 2)
    /^b\d{1,2}6$/,    // Unit value (col 6)
    /^b\d{1,2}6a$/,   // Unit value (col 6, page 2)
    /^b\d{1,2}7$/,    // Treaty-eligible withholding (col 9)
    /^b\d{1,2}7a$/,   // Treaty-eligible withholding (col 9, page 2)
    /^b\d{1,2}8$/,    // Actually withheld (col 10)
    /^b\d{1,2}8a$/,   // Actually withheld (col 10, page 2)
    /^zc1\d{1,2}$/,   // Total gross amount (col 7)
    /^zc1\d{1,2}a$/,  // Total gross amount (col 7, page 2)
    /^zc2\d{1,2}$/,   // Total withholding tax (col 8)
    /^zc2\d{1,2}a$/,  // Total withholding tax (col 8, page 2)
    /^zc3\d{1,2}$/,   // Reclaimable amount (col 11)
    /^zc3\d{1,2}a$/,  // Reclaimable amount (col 11, page 2)
    /^zc4$/,          // Grand total (page 1)
    /^zc4a$/,         // Grand total (page 2)
  ];

  const fieldStatuses: FieldStatus[] = fields.map(field => {
    const name = field.getName();
    const type = field.constructor.name;
    const filled = filledFieldPatterns.some(pattern => pattern.test(name));
    const category = categorizeField(name);

    return { name, type, filled, category };
  });

  // Calculate summary by category
  const summary: Record<string, { total: number; filled: number }> = {};
  fieldStatuses.forEach(({ category, filled }) => {
    if (!summary[category]) {
      summary[category] = { total: 0, filled: 0 };
    }
    summary[category].total++;
    if (filled) {
      summary[category].filled++;
    }
  });

  const filled = fieldStatuses.filter(f => f.filled).length;
  const missing = fieldStatuses.length - filled;

  return {
    total: fieldStatuses.length,
    filled,
    missing,
    fields: fieldStatuses,
    summary,
  };
}

/**
 * Categorize a field by its name pattern
 */
function categorizeField(name: string): FieldStatus['category'] {
  if (/^a\d+a?$/.test(name)) return 'header';
  if (/^b\d{1,3}[1-8]a?$/.test(name)) return 'dividend_table';
  if (/^zc/.test(name)) return 'totals';
  if (/^cac/.test(name)) return 'checkboxes';
  return 'unknown';
}

/**
 * Print a nice report of field coverage
 */
export function printFieldReport(result: Awaited<ReturnType<typeof checkFieldCoverage>>) {
  console.log('\n📊 FIELD COVERAGE REPORT\n');
  console.log(`Total fields: ${result.total}`);
  console.log(`✅ Filled: ${result.filled} (${((result.filled / result.total) * 100).toFixed(1)}%)`);
  console.log(`❌ Missing: ${result.missing} (${((result.missing / result.total) * 100).toFixed(1)}%)`);

  console.log('\n📋 By Category:\n');
  Object.entries(result.summary).forEach(([category, stats]) => {
    const percentage = ((stats.filled / stats.total) * 100).toFixed(1);
    console.log(`  ${category.padEnd(20)} ${stats.filled}/${stats.total} (${percentage}%)`);
  });

  console.log('\n❌ Missing Fields:\n');
  const missingByCategory = result.fields
    .filter(f => !f.filled)
    .reduce((acc, field) => {
      if (!acc[field.category]) acc[field.category] = [];
      acc[field.category].push(field.name);
      return acc;
    }, {} as Record<string, string[]>);

  Object.entries(missingByCategory).forEach(([category, fieldNames]) => {
    console.log(`  ${category}:`);
    console.log(`    ${fieldNames.slice(0, 10).join(', ')}${fieldNames.length > 10 ? ` ... (${fieldNames.length - 10} more)` : ''}`);
  });
}
