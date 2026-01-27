/**
 * Analyze Form 5000 structure and fields
 * Similar to what we did for Form 5001
 */

import { PDFDocument } from 'pdf-lib';

export async function analyzeForm5000Structure(pdfUrl: string = '/forms/5000-template.pdf') {
  console.log('🔍 Analyzing Form 5000 structure...');

  try {
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

    console.log('📊 Form 5000 Structure Analysis:');
    console.log(JSON.stringify(analysis, null, 2));

    // Group fields by pattern
    console.log('\n📋 Field categories:');

    const categories = {
      checkboxes: fields.filter(f => f.constructor.name.includes('CheckBox')),
      textFields: fields.filter(f => f.constructor.name.includes('TextField')),
      other: fields.filter(f =>
        !f.constructor.name.includes('CheckBox') &&
        !f.constructor.name.includes('TextField')
      ),
    };

    console.log(`✓ Checkboxes: ${categories.checkboxes.length}`);
    console.log(`  Names: ${categories.checkboxes.map(f => f.getName()).join(', ')}`);

    console.log(`✓ Text fields: ${categories.textFields.length}`);
    if (categories.textFields.length <= 20) {
      console.log(`  Names: ${categories.textFields.map(f => f.getName()).join(', ')}`);
    } else {
      console.log(`  First 20: ${categories.textFields.slice(0, 20).map(f => f.getName()).join(', ')}`);
    }

    console.log(`✓ Other types: ${categories.other.length}`);

    return analysis;
  } catch (error) {
    console.error('❌ Error analyzing Form 5000:', error);
    throw error;
  }
}

/**
 * Generate a field map PDF for Form 5000
 * Each field will be filled with its own name
 */
export async function generateForm5000FieldMap() {
  console.log('🗺️ Generating Form 5000 field map PDF...');

  try {
    const templateUrl = '/forms/5000-template.pdf';
    const existingPdfBytes = await fetch(templateUrl).then(res => res.arrayBuffer());
    const pdfDoc = await PDFDocument.load(existingPdfBytes);

    const form = pdfDoc.getForm();
    const fields = form.getFields();

    console.log(`📝 Found ${fields.length} fields in Form 5000`);

    let filledCount = 0;
    let errorCount = 0;

    // Fill each field with its own name
    fields.forEach((field) => {
      const fieldName = field.getName();
      const fieldType = field.constructor.name;

      try {
        if (fieldType.includes('TextField')) {
          const textField = form.getTextField(fieldName);
          textField.setText(fieldName);
          filledCount++;

          if (filledCount <= 10) {
            console.log(`✓ Filled text field: ${fieldName}`);
          }
        } else if (fieldType.includes('CheckBox')) {
          const checkbox = form.getCheckBox(fieldName);
          checkbox.check();
          filledCount++;
          console.log(`✓ Checked: ${fieldName}`);
        }
      } catch (error) {
        errorCount++;
        if (errorCount <= 5) {
          console.warn(`⚠️ Could not fill field: ${fieldName}`, error);
        }
      }
    });

    console.log(`✅ Filled ${filledCount} fields (${errorCount} errors)`);

    // Save the PDF
    const pdfBytes = await pdfDoc.save();
    const blob = new Blob([pdfBytes as any], { type: 'application/pdf' });
    const url = URL.createObjectURL(blob);

    // Download the PDF
    const link = document.createElement('a');
    link.href = url;
    link.download = '5000-FIELD-MAP.pdf';
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);

    console.log('📥 Form 5000 field map downloaded: 5000-FIELD-MAP.pdf');
    console.log('👀 Open the PDF to see which field name appears where!');

  } catch (error) {
    console.error('❌ Error generating Form 5000 field map:', error);
  }
}
