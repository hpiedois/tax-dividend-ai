import { PDFDocument } from 'pdf-lib';
import type { Form5000Data } from '../types/form.types';

/**
 * Fill official Form 5000 PDF (Attestation de Résidence)
 * This is the residence certificate required before Form 5001
 */
export async function fillOfficialForm5000(formData: Form5000Data): Promise<Blob> {
  try {
    // Load the official PDF template
    const templateUrl = '/forms/5000-template.pdf';
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

    console.log('📝 Form 5000 - PDF Form Analysis:');
    console.log(`Total fields found: ${fields.length}`);

    if (fields.length > 0) {
      // PDF has interactive form fields
      console.log('Form fields available, filling...');
      fillForm5000Fields(form, formData);
    } else {
      console.warn('⚠️ No form fields found in Form 5000 - manual filling required');
      // For now, we'll still return the template
      // In production, you might want to use text overlay as fallback
    }

    // Save the filled PDF
    const pdfBytes = await pdfDoc.save();
    return new Blob([pdfBytes as unknown as BlobPart], { type: 'application/pdf' });

  } catch (error) {
    console.error('Error filling Form 5000:', error);
    throw new Error('Failed to generate Form 5000. Please try again.');
  }
}

/**
 * Fill Form 5000 interactive fields
 */
function fillForm5000Fields(form: any, formData: Form5000Data) {
  console.log('📝 Starting to fill Form 5000 fields...');

  // Section I: Nature des revenus - Check "Dividendes" checkbox
  try {
    // Try multiple possible field names for the dividends checkbox
    const possibleNames = ['dividendes', 'div', 'case1', 'checkbox1', 'revenu1'];
    let filled = false;

    for (const name of possibleNames) {
      try {
        const checkbox = form.getCheckBox(name);
        checkbox.check();
        console.log(`✓ Checked dividends checkbox: ${name}`);
        filled = true;
        break;
      } catch {
        // Try next name
      }
    }

    if (!filled) {
      console.warn('Could not find dividends checkbox');
    }
  } catch (error) {
    console.warn('Error checking dividends:', error);
  }

  // Section II: Désignation du bénéficiaire
  fillTextField(form, ['nom', 'nomPrenom', 'name', 'beneficiaire'], formData.taxpayerName, 'Name');
  fillTextField(form, ['profession', 'prof', 'formeJuridique'], formData.profession || 'Particulier', 'Profession');

  const fullAddress = `${formData.address}\n${formData.postalCode} ${formData.city}\n${formData.country}`;
  fillTextField(form, ['adresse', 'address', 'domicile'], fullAddress, 'Address');

  if (formData.email) {
    fillTextField(form, ['mel', 'email', 'mail'], formData.email, 'Email');
  }

  // Section III: Déclaration du bénéficiaire
  fillTextField(form, ['resident', 'paysResidence', 'etatResidence'], formData.residenceCountry, 'Residence country');
  fillTextField(form, ['etat', 'state'], formData.residenceCountry, 'State');

  // Tax ID
  fillTextField(form, ['numeroFiscal', 'nif', 'taxId', 'identifiantFiscal'], formData.taxId, 'Tax ID');

  // Date and place
  fillTextField(form, ['dateLieu', 'date', 'dateDeclaration'],
    `${formData.declarationPlace}, le ${formData.declarationDate}`, 'Date and place');

  // Section VIII: Banking information (if provided)
  if (formData.iban) {
    fillTextField(form, ['iban', 'IBAN', 'compteIBAN'], formData.iban, 'IBAN');
  }
  if (formData.swift) {
    fillTextField(form, ['swift', 'SWIFT', 'bic', 'BIC'], formData.swift, 'SWIFT');
  }
  if (formData.bankName) {
    fillTextField(form, ['banque', 'bank', 'nomBanque'], formData.bankName, 'Bank name');
  }

  // Country name in the treaty field
  fillTextField(form, ['etatContractant', 'pays', 'country'], formData.residenceCountry, 'Contracting state');

  // Tax year
  fillTextField(form, ['annee', 'year', 'exercice'], formData.taxYear.toString(), 'Tax year');

  console.log('✅ Form 5000 fields filling complete');
}

/**
 * Helper function to try multiple field names
 */
function fillTextField(
  form: any,
  possibleNames: string[],
  value: string,
  description: string
): boolean {
  for (const name of possibleNames) {
    try {
      const field = form.getTextField(name);
      field.setText(value);
      console.log(`✓ Filled ${description} (${name}): ${value.substring(0, 50)}${value.length > 50 ? '...' : ''}`);
      return true;
    } catch {
      // Try next name
    }
  }
  console.warn(`Could not fill ${description} - tried: ${possibleNames.join(', ')}`);
  return false;
}


