/**
 * Debug utilities - exposes functions to browser console for development
 * Only loaded in development mode
 */

import { PDFDocument } from 'pdf-lib';
import { analyzePDFStructure, fillOfficialForm5001 } from './pdf-form-filler';
import { checkFieldCoverage, printFieldReport } from './field-checker';
import { analyzeForm5000Structure, generateForm5000FieldMap } from './analyze-form-5000';

// Expose debug functions to window object
declare global {
  interface Window {
    debugPDF: {
      // Form 5001 tools
      analyzePDFStructure: typeof analyzePDFStructure;
      fillOfficialForm5001: typeof fillOfficialForm5001;
      testPDF: () => Promise<any>;
      generateFieldMap: () => Promise<void>;
      checkMissingFields: () => Promise<any>;

      // Form 5000 tools
      analyzeForm5000: typeof analyzeForm5000Structure;
      generateForm5000FieldMap: typeof generateForm5000FieldMap;
      testForm5000: () => Promise<any>;
    };
  }
}

/**
 * Generate a PDF with each field filled with its own name
 * This helps identify which field name corresponds to which position in the form
 */
async function generateFieldMap() {
  console.log('🗺️ Generating field map PDF...');

  try {
    // Load the official PDF template
    const templateUrl = '/forms/5001-template.pdf';
    const existingPdfBytes = await fetch(templateUrl).then(res => res.arrayBuffer());
    const pdfDoc = await PDFDocument.load(existingPdfBytes);

    // Get the form and all fields
    const form = pdfDoc.getForm();
    const fields = form.getFields();

    console.log(`📝 Found ${fields.length} fields`);

    let filledCount = 0;
    let errorCount = 0;

    // Fill each field with its own name
    fields.forEach((field) => {
      const fieldName = field.getName();
      const fieldType = field.constructor.name;

      try {
        if (fieldType === 'PDFTextField' || fieldType === 'PDFTextField2') {
          // It's a text field - fill it with its name
          const textField = form.getTextField(fieldName);
          textField.setText(fieldName);
          filledCount++;

          if (filledCount <= 10) {
            console.log(`✓ Filled: ${fieldName}`);
          }
        } else if (fieldType === 'PDFCheckBox') {
          // It's a checkbox - just check it
          const checkbox = form.getCheckBox(fieldName);
          checkbox.check();
          filledCount++;
        }
      } catch (error) {
        errorCount++;
        if (errorCount <= 5) {
          console.warn(`⚠️ Could not fill field: ${fieldName}`, error);
        }
      }
    });

    console.log(`✅ Filled ${filledCount} fields (${errorCount} errors)`);

    // Flatten the form to make it read-only (optional)
    // form.flatten();

    // Save the PDF
    const pdfBytes = await pdfDoc.save();
    const blob = new Blob([pdfBytes as any], { type: 'application/pdf' });
    const url = URL.createObjectURL(blob);

    // Download the PDF
    const link = document.createElement('a');
    link.href = url;
    link.download = '5001-FIELD-MAP.pdf';
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);

    console.log('📥 Field map PDF downloaded: 5001-FIELD-MAP.pdf');
    console.log('👀 Open the PDF to see which field name appears where!');

  } catch (error) {
    console.error('❌ Error generating field map:', error);
  }
}

/**
 * Initialize debug utilities
 * Call this in App.tsx (development only)
 */
export function initDebugUtils() {
  if (typeof window === 'undefined') return;

  window.debugPDF = {
    // Form 5001 tools
    analyzePDFStructure,
    fillOfficialForm5001,
    generateFieldMap,

    // Quick test function for Form 5001
    async testPDF() {
      console.log('🔍 Analyzing Form 5001 structure...');
      const analysis = await analyzePDFStructure('/forms/5001-template.pdf');
      console.log('✅ Analysis complete:');
      console.log('📄 Pages:', analysis.pageCount);
      console.log('📝 Form fields:', analysis.fieldCount);
      if (analysis.fields.length > 0) {
        console.log('🔖 Field names:', analysis.fields.map(f => f.name).join(', '));
      } else {
        console.log('⚠️ No interactive fields found - will use text overlay');
      }
      console.log('📐 Page dimensions:', analysis.pagesDimensions);
      return;
    },

    // Check which fields are filled and which are missing (Form 5001)
    async checkMissingFields() {
      console.log('🔍 Checking Form 5001 field coverage...');
      const result = await checkFieldCoverage('/forms/5001-template.pdf');
      printFieldReport(result);
      return result;
    },

    // Form 5000 tools
    analyzeForm5000: analyzeForm5000Structure,
    generateForm5000FieldMap,

    // Quick test function for Form 5000
    async testForm5000() {
      console.log('🔍 Analyzing Form 5000 structure...');
      const analysis = await analyzeForm5000Structure('/forms/5000-template.pdf');
      console.log('✅ Form 5000 analysis complete');
      return analysis;
    }
  };

  console.log('🛠️ Debug utilities loaded!');
  console.log('');
  console.log('📚 Form 5001 commands:');
  console.log('  - debugPDF.testPDF()                           // Quick test');
  console.log('  - debugPDF.analyzePDFStructure("/forms/5001-template.pdf")  // Analyze structure');
  console.log('  - debugPDF.generateFieldMap()                  // Generate field mapping PDF');
  console.log('  - debugPDF.checkMissingFields()                // Check which fields are missing');
  console.log('');
  console.log('📚 Form 5000 commands:');
  console.log('  - debugPDF.testForm5000()                      // Analyze Form 5000');
  console.log('  - debugPDF.analyzeForm5000()                   // Detailed analysis');
  console.log('  - debugPDF.generateForm5000FieldMap()          // Generate Form 5000 field map');
  console.log('');
}
