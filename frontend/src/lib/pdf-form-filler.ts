import { PDFDocument, rgb, StandardFonts } from 'pdf-lib';
import type { Form5001Data, DividendEntry } from '../types/form.types';

/**
 * Fill official Form 5001 PDF with dividend data
 *
 * This function loads the official French tax form 5001 and fills it with the provided data.
 * If the PDF has interactive form fields, it will use them. Otherwise, it will overlay text.
 */
export async function fillOfficialForm5001(formData: Form5001Data): Promise<Blob> {
  try {
    // Load the official PDF template
    const templateUrl = '/forms/5001-template.pdf';
    const existingPdfBytes = await fetch(templateUrl).then(res => {
      if (!res.ok) {
        throw new Error(`Failed to load PDF template: ${res.statusText}`);
      }
      return res.arrayBuffer();
    });

    const pdfDoc = await PDFDocument.load(existingPdfBytes);

    // Try to get the form (if it has interactive fields)
    const form = pdfDoc.getForm();
    const fields = form.getFields();

    console.log('PDF Form Analysis:');
    console.log(`Total fields found: ${fields.length}`);

    if (fields.length > 0) {
      // PDF has interactive form fields
      console.log('Form fields:');
      fields.forEach(field => {
        const name = field.getName();
        const type = field.constructor.name;
        console.log(`- ${name} (${type})`);
      });

      // Fill the fields (you'll need to adjust field names based on actual PDF)
      try {
        fillFormFields(form, formData);
      } catch (_error) {
        console.error('Error filling form fields:', error);
        // Fallback to text overlay
        await fillWithTextOverlay(pdfDoc, formData);
      }
    } else {
      // PDF doesn't have form fields - use text overlay
      console.log('No form fields found, using text overlay');
      await fillWithTextOverlay(pdfDoc, formData);
    }

    // Save the filled PDF
    const pdfBytes = await pdfDoc.save();
    // Create Blob from Uint8Array (TypeScript strict mode workaround)
    return new Blob([pdfBytes as unknown as BlobPart], { type: 'application/pdf' });

  } catch (_error) {
    console.error('Error filling PDF:', error);
    throw new Error('Failed to generate PDF form. Please try again.');
  }
}

/**
 * Fill interactive form fields (if available)
 * Based on official form 5001 field mapping
 */
function fillFormFields(form: any, formData: Form5001Data) {
  console.log('📝 Starting to fill form fields...');

  // 1. Fill header information (creditor name)
  try {
    const nameField = form.getTextField('a1');
    nameField.setText(formData.taxpayerName);
    console.log(`✓ Filled a1 (creditor name): ${formData.taxpayerName}`);
  } catch (_error) {
    console.warn('Could not fill field a1 (creditor name)');
  }

  // Also fill the 'a' version for duplicate pages
  try {
    const nameFieldA = form.getTextField('a1a');
    nameFieldA.setText(formData.taxpayerName);
    console.log(`✓ Filled a1a (creditor name copy)`);
  } catch (_error) {
    // Ignore if doesn't exist
  }

  // 2. Fill treaty information (France-Switzerland convention)
  try {
    // Article of the convention (typically Article 15 for dividends)
    const articleField = form.getTextField('a3');
    articleField.setText('15');
    console.log(`✓ Filled a3 (article): 15`);
  } catch (_error) {
    console.warn('Could not fill field a3 (article)');
  }

  try {
    // Country (Switzerland for Swiss residents claiming back French tax)
    const countryField = form.getTextField('a4');
    countryField.setText('Suisse');
    console.log(`✓ Filled a4 (country): Suisse`);
  } catch (_error) {
    console.warn('Could not fill field a4 (country)');
  }

  try {
    // Reduced rate percentage (15% under France-Switzerland treaty)
    const rateField1 = form.getTextField('a5');
    rateField1.setText('15');
    console.log(`✓ Filled a5 (reduced rate %): 15`);
  } catch (_error) {
    console.warn('Could not fill field a5 (rate)');
  }

  try {
    // Also fill a55 if it exists (might be duplicate or different field)
    const rateField2 = form.getTextField('a55');
    rateField2.setText('15');
    console.log(`✓ Filled a55 (reduced rate): 15%`);
  } catch (_error) {
    console.warn('Could not fill field a55 (rate)');
  }

  // Also fill the 'a' version for duplicate pages
  try {
    form.getTextField('a3a').setText('15');
    form.getTextField('a4a').setText('Suisse');
    form.getTextField('a5a').setText('15');
    form.getTextField('a55a').setText('15');
    console.log(`✓ Filled treaty fields for page copies`);
  } catch (_error) {
    // Ignore if fields don't exist
  }

  // 2b. Check the first checkbox (France-Switzerland convention)
  try {
    const checkbox1 = form.getCheckBox('cac1');
    checkbox1.check();
    console.log(`✓ Checked cac1 (France-Switzerland convention)`);
  } catch (_error) {
    console.warn('Could not check cac1 (maybe not a checkbox or different field type)');
  }

  // 2c. Fill a6 (date and place of signature)
  try {
    const today = new Date();
    const dateStr = formatDate(today.toISOString());
    const place = formData.city || 'Suisse';
    const dateAndPlace = `${place}, le ${dateStr}`;

    const dateField = form.getTextField('a6');
    dateField.setText(dateAndPlace);
    console.log(`✓ Filled a6 (date and place): ${dateAndPlace}`);
  } catch (_error) {
    console.warn('Could not fill field a6 (date and place)');
  }

  // Fill a6a for page copies
  try {
    const today = new Date();
    const dateStr = formatDate(today.toISOString());
    const place = formData.city || 'Suisse';
    const dateAndPlace = `${place}, le ${dateStr}`;

    const dateFieldA = form.getTextField('a6a');
    dateFieldA.setText(dateAndPlace);
    console.log(`✓ Filled a6a (date and place copy)`);
  } catch (_error) {
    // Ignore if doesn't exist
  }

  // 3. Fill dividend table - First page (lines 1-18)
  const maxLinesPerPage = 18;
  const firstPageDividends = formData.dividends.slice(0, maxLinesPerPage);

  firstPageDividends.forEach((dividend, index) => {
    const lineNumber = index + 1;
    fillDividendLine(form, dividend, lineNumber, false);
  });

  // 4. Fill dividend table - Second page (lines 1-18 with 'a' suffix)
  if (formData.dividends.length > maxLinesPerPage) {
    const secondPageDividends = formData.dividends.slice(maxLinesPerPage, maxLinesPerPage * 2);

    secondPageDividends.forEach((dividend, index) => {
      const lineNumber = index + 1;
      fillDividendLine(form, dividend, lineNumber, true);
    });
  }

  // 5. Fill total amounts
  fillTotalFields(form, formData);

  console.log('✅ Form fields filling complete');
}

/**
 * Fill a single dividend line in the table
 *
 * Field mapping for each line:
 * - b[X]1: Company name
 * - b[X]2: Payment date
 * - b[X]3: Number of shares (not in our data, leave empty)
 * - b[X]4: Participation % (not in our data, leave empty)
 * - b[X]5: Threshold date (not in our data, leave empty)
 * - b[X]6: Unit value per share
 * - zc1[X]: Total gross amount
 * - zc2[X]: Total withholding tax
 * - b[X]7: Treaty-eligible withholding (15% of gross for FR-CH convention)
 * - b[X]8: Actually withheld amount
 * - zc3[X]: Reclaimable amount
 */
function fillDividendLine(
  form: any,
  dividend: DividendEntry,
  lineNumber: number,
  isSecondPage: boolean
) {
  const suffix = isSecondPage ? 'a' : '';
  const line = lineNumber.toString();

  try {
    // Column 1: Company name
    const companyField = form.getTextField(`b${line}1${suffix}`);
    companyField.setText(dividend.securityName);

    // Column 2: Payment date
    const dateField = form.getTextField(`b${line}2${suffix}`);
    dateField.setText(formatDate(dividend.paymentDate));

    // Column 6: Unit value (use gross amount as proxy)
    const unitValueField = form.getTextField(`b${line}6${suffix}`);
    unitValueField.setText(formatAmount(dividend.grossAmount));

    // Column 7: Total gross amount (calculated field)
    const totalField = form.getTextField(`zc1${line}${suffix}`);
    totalField.setText(formatAmount(dividend.grossAmount));

    // Column 8: Total withholding tax
    const withholdingField = form.getTextField(`zc2${line}${suffix}`);
    withholdingField.setText(formatAmount(dividend.withholdingTax));

    // Column 9: Treaty-eligible withholding (15% of gross for FR-CH convention)
    // This is the amount that SHOULD be withheld under the treaty
    const treatyEligibleAmount = dividend.grossAmount * 0.15; // 15% rate
    const treatyEligibleField = form.getTextField(`b${line}7${suffix}`);
    treatyEligibleField.setText(formatAmount(treatyEligibleAmount));

    // Column 10: Actually withheld amount
    const actualWithheldField = form.getTextField(`b${line}8${suffix}`);
    actualWithheldField.setText(formatAmount(dividend.treatyAmount));

    // Column 11: Reclaimable amount
    const reclaimField = form.getTextField(`zc3${line}${suffix}`);
    reclaimField.setText(formatAmount(dividend.reclaimableAmount));

    console.log(`✓ Filled line ${lineNumber}${suffix ? ' (page 2)' : ''}: ${dividend.securityName}`);

  } catch (_error) {
    console.error(`❌ Error filling line ${lineNumber}${suffix}:`, error);
  }
}

/**
 * Fill total fields at bottom of table
 */
function fillTotalFields(form: any, formData: Form5001Data) {
  try {
    // First page total
    const totalField = form.getTextField('zc4');
    totalField.setText(formatAmount(formData.totalReclaimableAmount));
    console.log(`✓ Filled total (zc4): ${formatAmount(formData.totalReclaimableAmount)}`);
  } catch (_error) {
    console.warn('Could not fill total field zc4');
  }

  // Second page total (if exists)
  try {
    const totalFieldA = form.getTextField('zc4a');
    totalFieldA.setText(formatAmount(formData.totalReclaimableAmount));
  } catch (_error) {
    // Ignore if doesn't exist
  }
}

/**
 * Format date for PDF form (DD/MM/YYYY)
 */
function formatDate(dateString: string): string {
  try {
    const date = new Date(dateString);
    const day = date.getDate().toString().padStart(2, '0');
    const month = (date.getMonth() + 1).toString().padStart(2, '0');
    const year = date.getFullYear();
    return `${day}/${month}/${year}`;
  } catch {
    return dateString;
  }
}

/**
 * Format amount for PDF form (2 decimal places)
 */
function formatAmount(amount: number): string {
  return amount.toFixed(2);
}

/**
 * Overlay text on PDF (fallback method)
 * This adds text directly to the PDF pages at specific coordinates
 */
async function fillWithTextOverlay(pdfDoc: PDFDocument, formData: Form5001Data) {
  const pages = pdfDoc.getPages();
  const firstPage = pages[0];
  const font = await pdfDoc.embedFont(StandardFonts.Helvetica);
  const fontSize = 10;

  // You'll need to adjust these coordinates based on the actual PDF layout
  // Use a PDF viewer with coordinate display to find exact positions

  // Personal information (approximate positions - NEEDS ADJUSTMENT)
  firstPage.drawText(formData.taxpayerName, {
    x: 150,
    y: 700,
    size: fontSize,
    font,
    color: rgb(0, 0, 0),
  });

  firstPage.drawText(formData.taxId, {
    x: 150,
    y: 680,
    size: fontSize,
    font,
    color: rgb(0, 0, 0),
  });

  firstPage.drawText(`${formData.address}, ${formData.postalCode} ${formData.city}`, {
    x: 150,
    y: 660,
    size: fontSize,
    font,
    color: rgb(0, 0, 0),
  });

  firstPage.drawText(formData.taxYear.toString(), {
    x: 150,
    y: 640,
    size: fontSize,
    font,
    color: rgb(0, 0, 0),
  });

  // Add warning that this is a draft
  firstPage.drawText('⚠️ DOCUMENT GÉNÉRÉ AUTOMATIQUEMENT - VÉRIFIER AVANT SOUMISSION', {
    x: 50,
    y: 50,
    size: 8,
    font,
    color: rgb(1, 0, 0),
  });

  // Dividends table (if there's space on page 1, otherwise use additional pages)
  let yPosition = 580;
  formData.dividends.slice(0, 10).forEach((dividend) => {
    if (yPosition > 100) {
      firstPage.drawText(
        `${dividend.securityName} - ${dividend.grossAmount.toFixed(2)} ${dividend.currency}`,
        {
          x: 100,
          y: yPosition,
          size: 8,
          font,
          color: rgb(0, 0, 0),
        }
      );
      yPosition -= 20;
    }
  });

  console.log('✓ Text overlay applied to PDF');
}

/**
 * Analyze PDF structure (utility function for development)
 */
export async function analyzePDFStructure(pdfUrl: string) {
  const pdfBytes = await fetch(pdfUrl).then(res => res.arrayBuffer());
  const pdfDoc = await PDFDocument.load(pdfBytes);

  const form = pdfDoc.getForm();
  const fields = form.getFields();
  const pages = pdfDoc.getPages();

  const analysis = {
    pageCount: pages.length,
    fieldCount: fields.length,
    fields: fields.map(field => ({
      name: field.getName(),
      type: field.constructor.name,
    })),
    pagesDimensions: pages.map((page, i) => ({
      page: i + 1,
      width: page.getWidth(),
      height: page.getHeight(),
    })),
  };

  console.log('PDF Structure Analysis:', JSON.stringify(analysis, null, 2));
  return analysis;
}
