import { useMutation } from '@tanstack/react-query';
import { useTranslation } from 'react-i18next';
import JSZip from 'jszip';
import { showSuccess, showError } from '../lib/toast-helpers';
import { fillOfficialForm5001, analyzePDFStructure } from '../lib/pdf-form-filler';
import { fillOfficialForm5000 } from '../lib/pdf-form-5000-filler';
import type { FormGenerationRequest, FormGenerationResponse, Form5000Data } from '../types/form.types';

// Generate form using official PDF template
const generateFormWithOfficialPDF = async (request: FormGenerationRequest): Promise<FormGenerationResponse> => {
  console.log('Generating official forms with data:', request);

  try {
    // Analyze PDF structure on first run (development only)
    if (import.meta.env.DEV) {
      await analyzePDFStructure('/forms/5001-template.pdf');
    }

    const formId = `form-${Date.now()}`;
    const { formData, includeForm5000 } = request;

    // Generate Form 5001 (always)
    console.log('📄 Generating Form 5001...');
    const pdf5001Blob = await fillOfficialForm5001(formData);

    // If only Form 5001 is requested
    if (!includeForm5000) {
      const pdfUrl = URL.createObjectURL(pdf5001Blob);
      return {
        formId,
        pdfUrl,
        fileName: `formulaire-5001-${formData.taxYear}.pdf`,
        generatedAt: new Date().toISOString(),
      };
    }

    // Generate Form 5000 (Attestation de Résidence)
    console.log('📄 Generating Form 5000...');
    const form5000Data: Form5000Data = {
      taxpayerName: formData.taxpayerName,
      profession: 'Particulier', // Default for individual
      address: formData.address,
      city: formData.city,
      postalCode: formData.postalCode,
      country: formData.country,
      email: '', // Could be added to Form5001Data if needed
      taxId: formData.taxId,
      residenceCountry: formData.country,
      taxYear: formData.taxYear,
      declarationDate: formatDate(new Date().toISOString()),
      declarationPlace: formData.city,
    };

    const pdf5000Blob = await fillOfficialForm5000(form5000Data);

    // Create ZIP file containing both forms
    console.log('📦 Creating ZIP archive...');
    const zip = new JSZip();

    zip.file(`formulaire-5000-${formData.taxYear}.pdf`, pdf5000Blob);
    zip.file(`formulaire-5001-${formData.taxYear}.pdf`, pdf5001Blob);

    const zipBlob = await zip.generateAsync({ type: 'blob' });
    const zipUrl = URL.createObjectURL(zipBlob);

    console.log('✅ ZIP archive created successfully');

    return {
      formId,
      pdfUrl: zipUrl,
      fileName: `formulaires-${formData.taxYear}.zip`,
      generatedAt: new Date().toISOString(),
    };
  } catch (error) {
    console.error('Error generating official PDFs:', error);
    throw error;
  }
};

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

export const useGenerateForm = () => {
  const { t } = useTranslation();

  return useMutation({
    mutationFn: generateFormWithOfficialPDF, // Use official PDF instead of mock
    onSuccess: () => {
      showSuccess(t('forms.generation_success'));
    },
    onError: (error) => {
      console.error('Form generation error:', error);
      showError(t('forms.generation_error'));
    },
  });
};

// Future: Real API call to Spring Boot BFF
// export const generateFormAPI = async (request: FormGenerationRequest): Promise<FormGenerationResponse> => {
//   const { data } = await apiClient.post<FormGenerationResponse>('/forms/generate', request);
//   return data;
// };
